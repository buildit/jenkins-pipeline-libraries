package buildit

class k8s implements Serializable {

    def CACHE_BASE = '/var/cache/'
    def DOCKER_IMG = 'docker:1.11'
    def KUBECTL_IMG = 'builditdigital/kube-utils'

    final script
    final JOB_NAME
    final cloud

    k8s(script, Cloud cloud) {
        this.script = script
        this.cloud = cloud
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

    def helmDeploy(appName, env, image, tag) {
        script.container('kubectl') {
            def deployment = "$appName-$env"
            def deploymentObj = "$deployment-$appName".take(24)
            def varsFile = "./k8s/${cloud}/vars/${env}.yaml"
            script.sh "helm ls -q | grep $deployment || helm install ./k8s/${appName} -f $varsFile -n $deployment"
            script.sh "helm upgrade $deployment ./k8s/${appName} -f $varsFile --set image.repository=$image --set image.tag=$tag"
            script.sh "kubectl rollout status deployment/$deploymentObj"
        }
    }

    def build(containers = [], volumes = [], steps) {
        def rand = 'build' + System.nanoTime()
        script.podTemplate(label: rand,
                containers: containers + [
                        script.containerTemplate(name: 'docker', image: this.DOCKER_IMG, ttyEnabled: true, command: 'cat'),
                        script.containerTemplate(name: 'kubectl', image: this.KUBECTL_IMG, ttyEnabled: true, command: 'cat')],
                volumes: volumes + [
                        script.hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
                        script.hostPathVolume(mountPath: '/var/cache', hostPath: '/tmp')
                ]) {
            script.node(rand) {
                steps()
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
}
