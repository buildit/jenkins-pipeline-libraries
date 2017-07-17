@Library('buildit')

def shellLib = new shell()
def pomLib = new pom()
def gitLib = new git()
def bintray = new bintray()

try {

    node() {

        checkout scm
        sh("git checkout master && git pull origin master")

        stage('create package') {

            def commitId = shellLib.pipe("git rev-parse HEAD")
            def pomVersion = pomLib.version(pwd() + "/pom.xml")

            sh("mvn clean package")

            jenkinsUnitRunner = load("test/groovy/jenkinsUnit/runner.groovy")
            jenkinsUnitRunner.run("test/groovy/jenkinsUnit/test")

            withCredentials([usernamePassword(credentialsId: 'github-jenkins-buildit', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                def repositoryUrl = shellLib.pipe("git config --get remote.origin.url")
                def authenticatedUrl = gitLib.authenticatedUrl(repositoryUrl, env.USERNAME, env.PASSWORD)
                echo("setting remote to authenticated url : ${authenticatedUrl}")
                sh("git remote set-url origin ${authenticatedUrl} &> /dev/null")
                sh("git tag -af ${pomVersion} -m \"Built version: ${pomVersion}\" ${commitId}")
                sh("git push --tags")
            }
        }

        stage('promote package') {
            bintray.upload('bintray-credentials', pomLib.artifactId(pwd() + "/pom.xml"), pomLib.version(pwd() + "/pom.xml"), 'zip', 'target/*.zip', 'buildit', 'maven')
        }

        stage('increment version') {
            def newVersion = calculateNewPomVersion(pwd() + "/pom.xml")
            withCredentials([usernamePassword(credentialsId: 'github-jenkins-buildit', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                sh("mvn versions:set -DnewVersion=${newVersion} versions:commit")
                sh("git add pom.xml")
                sh("git commit -m'Bumping version to ${newVersion}'")
                sh("git push origin")
            }
        }
    }
}
catch (err) {
    echo("FAILURE: " + err.toString())
    currentBuild.result = "FAILURE"
    node() {
        def pomVersion = pomLib.version(pwd() + "/pom.xml")
        withCredentials([usernamePassword(credentialsId: 'github-jenkins-buildit', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            // delete the tag from origin
            sh("git push origin :refs/tags/${pomVersion}")
            sh("git fetch --tags --prune")
        }
    }
    throw err
}

def calculateNewPomVersion(pomLocation){
    def pomLib = new pom()
    def majorVersion = pomLib.majorVersion(pomLocation)
    def minorVersion = pomLib.minorVersion(pomLocation).toInteger()
    def patchVersion = pomLib.patchVersion(pomLocation).toInteger()
    def newVersion = "${majorVersion}.${minorVersion + 1}.0"
    if (patchVersion > 0) {
        newVersion = "${majorVersion}.${minorVersion}.${patchVersion + 1}"
    }
    return newVersion
}
