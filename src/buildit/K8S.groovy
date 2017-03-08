package buildit

class K8S implements Serializable {

    def CACHE_BASE = '/var/cache/'
    def HOST_CACHE_BASE = '/tmp/'
    def DOCKER_IMG = 'docker:1.11'
    def KUBECTL_IMG = 'builditdigital/kube-utils'

    final script
    final JOB_NAME
    final cloud
    final region

    K8S(script, Cloud cloud, region = null) {
        this.script = script
        this.cloud = cloud
        this.region = region
        JOB_NAME = script.env.JOB_NAME
    }

    def withCache(String dir, String cacheDir = this.CACHE_BASE + this.JOB_NAME, step) {
        if (!dir) {
            throw new IllegalArgumentException('Target directory must be provided')
        }
        script.sh "mkdir -p ${cacheDir}/${dir}"
        script.sh "cp -r ${cacheDir}/* ."
        step()
        script.sh "rm -rf ${cacheDir}/${dir} && cp -r ${dir} ${cacheDir}/"
    }

    def helmDeploy(appName, env, image, tag, ns = 'default') {
        script.container('kubectl') {
            def deployment = "$appName-$env"
            def deploymentObj = "$deployment-$appName".take(24)
            def varsFile = "./k8s/${cloud}/vars/${env}.yaml"
            script.sh "helm ls -q | grep $deployment || helm install ./k8s/${appName} -f $varsFile -n $deployment --namespace=${ns}"
            script.sh "helm upgrade $deployment ./k8s/${appName} -f $varsFile --set image.repository=$image --set image.tag=$tag --namespace=${ns}"
            script.sh "kubectl rollout status deployment/$deploymentObj -n=${ns}"
        }
        getServiceName(appName, env)
    }

    def getServiceName(app, env) {
        "${app}-${env}-${app}".take(24)
    }

    def build(containers = [], volumes = [], steps) {
        def rand = 'build' + System.nanoTime()
        def defaultContainers = [
                script.containerTemplate(name: 'docker', image: this.DOCKER_IMG, ttyEnabled: true, command: 'cat'),
                script.containerTemplate(name: 'kubectl', image: this.KUBECTL_IMG, ttyEnabled: true, command: 'cat', alwaysPullImage: true)]
        def defaultVolumes = [
                script.hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
                script.hostPathVolume(mountPath: this.CACHE_BASE, hostPath: HOST_CACHE_BASE)
        ]

        if(cloud == Cloud.ec2) {
            defaultContainers << script.containerTemplate(name: 'aws', image: 'cgswong/aws', ttyEnabled: true, command: 'cat')
        }

        script.podTemplate(
                label: rand,
                containers: containers + defaultContainers,
                volumes: volumes + defaultVolumes) {
            script.node(rand) {
                steps()
            }
        }
    }

    def dockerAuth() {
        if(Cloud.ec2 == cloud) {
            if(!region) {
                throw new IllegalArgumentException('Region must be set')
            }
            def loginCmd
            script.container('aws') {
                loginCmd = script.sh script: "aws ecr get-login --region=${region}", returnStdout: true
            }
            inDocker {
                script.sh loginCmd
            }
        }
    }

    def inDocker(steps) {
        script.container('docker') {
            steps()
        }
    }

    def dockerBuild(image, tag, dir = '.') {
        inDocker {
            // Docker pipeline plugin does not work with kubernetes (see https://issues.jenkins-ci.org/browse/JENKINS-39664)
            script.sh "docker build -t $image:$tag $dir"
        }
    }

    def dockerPush(image, tag) {
        dockerAuth()
        if(cloud != Cloud.local) {
            inDocker {
                script.sh "docker push $image:$tag"
            }
        }
    }
}
