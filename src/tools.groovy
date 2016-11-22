def configureMaven() {
    def mavenHome = tool "maven"
    env.PATH = "${env.PATH}:${mavenHome}/bin"
    env.MAVEN_OPTS = "-Xmx1024m -XX:MaxPermSize=1024m"
    echo 'YES!'
}

return this
