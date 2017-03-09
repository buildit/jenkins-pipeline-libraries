def configureMaven(String name='maven', String opts="-Xmx1024m -XX:MaxPermSize=1024m") {
    def path = configureTool(name)
    env.MAVEN_HOME = path
    env.MAVEN_OPTS = opts
    echo("Configured MAVEN_OPTS: ${opts}")
    return path
}

def configureJava(String name='java') {
    def path = configureTool(name)
    env.JAVA_HOME = path
    echo("Configured JAVA_HOME: ${path}")
    return path
}

def configureAndroid(String name='android', String pathToExecutable='tools', String sdkOpts='"platform-tools"') {
    def path = configureTool(name, pathToExecutable)
    env.ANDROID_HOME = path
    echo("Configured ANDROID_HOME: ${path}")

//    sh("(while sleep 2; do echo \"y\"; done) | ${path}/${pathToExecutable}/bin/sdkmanager ${sdkOpts}")
//    sh("echo 'y' | ${path}/${pathToExecutable}/sdkmanager ${sdkOpts}")
    sh("echo 'y' | android update sdk --no-ui --filter ${sdkOpts}")
}

def configureTool(String name, String pathToExecutable="bin") {
    def path = tool name
    def executablePath = stripLastSlash("${path}/${pathToExecutable}")
    env.PATH = "${env.PATH}:${executablePath}"
    echo("Configured ${name}: ${path}")
    echo("Path is now ${env.PATH}")
    return path
}

private stripLastSlash(String path){
    (path.endsWith("/")) ? path.substring(0, path.lastIndexOf("/")) : path;
}

return this
