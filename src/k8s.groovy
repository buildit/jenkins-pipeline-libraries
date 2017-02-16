JOB_NAME = env.JOB_NAME
CACHE_BASE = '/var/cache/'
DOCKER_IMG = 'docker:1.11'
KUBECTL_IMG = 'builditdigital/kube-utils'

def withCache(String dir, String cacheDir = CACHE_BASE + JOB_NAME, step) {
    if (!dir) {
        throw new IllegalArgumentException('Target directory must be provided')
    }
    sh "mkdir -p ${cacheDir}/${dir}"
    sh "cp -r ${cacheDir}/* ."
    step()
    sh "rm -rf ${cacheDir}/${dir} && cp -r ${dir} ${cacheDir}/"
}

def helmDeploy(appName, env, cloud, image) {
    container('kubectl') {
        def deployment = "$appName-$env"
        deploymentObj = "$deployment-$appName".take(24)
        def varsFile = "./k8s/${cloud}/vars/${env}.yaml"
        sh "helm ls -q | grep $deployment || helm install ./k8s/${appName} -f $varsFile -n $deployment"
        sh "helm upgrade $deployment ./k8s/${appName} -f $varsFile --set image.repository=$image --set image.tag=$tag"
        sh "kubectl rollout status deployment/$deploymentObj"
    }
}

def build(containers = [], volumes = [], steps) {
    def rand = 'build' + System.nanoTime()
    podTemplate(label: rand,
            containers: containers + [
                    containerTemplate(name: 'docker', image: DOCKER_IMG, ttyEnabled: true, command: 'cat'),
                    containerTemplate(name: 'kubectl', image: KUBECTL_IMG, ttyEnabled: true, command: 'cat')],
            volumes: volumes + [
                    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
                    hostPathVolume(mountPath: '/var/cache', hostPath: '/tmp')
            ]) {
        node(rand) {
            steps()
        }
    }
}

def inDocker(steps) {
    container('docker') {
        steps()
    }
}

def dockerBuild(image, tag, dir = '.') {
    inDocker {
        // Docker pipeline plugin does not work with kubernetes (see https://issues.jenkins-ci.org/browse/JENKINS-39664)
        sh "docker build -t $image:$tag $dir"
    }
}

return this