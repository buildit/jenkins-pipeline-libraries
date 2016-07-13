node() {
    checkout scm

    shell = load "lib/shell.groovy"
    pom = load "lib/pom.groovy"
    git = load "lib/git.groovy"

    nexusHost = "http://nexus.riglet:9000/nexus"
}

stage 'create package'
node() {
    def pomVersion = pom.version(pwd() + "/pom.xml")
    def commitId = shell.pipe("git rev-parse HEAD")

    sh("mvn clean package")

    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "git-credentials", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        def repositoryUrl = shell.pipe("git config --get remote.origin.url")
        def authenticatedUrl = git.authenticatedUrl(repositoryUrl, env.USERNAME, env.PASSWORD)
        sh("git remote set-url origin ${authenticatedUrl} &> /dev/null")
        sh("git tag -a ${pomVersion} -m \"Built version: ${pomVersion}\" ${commitId}")
        sh("git push --tags")
    }
}

stage 'promote package'
node() {
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "nexus-credentials", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
       def credentials = "'${env.USERNAME}':'${env.PASSWORD}'"
       sh("curl -v -u ${credentials} --upload-file target/*.zip \"${nexusHost}/content/repositories/staging/zips/jenkins-pipeline-libraries/\"")
    }
}

stage 'increment minor version'
node() {
    def majorVersion = pom.majorVersion(pwd() + "/pom.xml")
    def minorVersion = pom.minorVersion(pwd() + "/pom.xml").toInteger() + 1
    def patchVersion = pom.patchVersion(pwd() + "/pom.xml")
    def newVersion = "${majorVersion}.${minorVersion}.${patchVersion}"

    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "git-credentials", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        sh("mvn versions:set -DnewVersion=${newVersion} versions:commit")
        sh("git add pom.xml")
        sh("git commit -m'Bumping version to ${newVersion}'")
        sh("git push origin")
    }
}
