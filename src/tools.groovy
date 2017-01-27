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
