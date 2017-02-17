@Library('buildit')
        
def shellLib = new shell()
def pomLib = new pom()
def gitLib = new git()

try {

    node() {

        checkout scm
        sh("git checkout master")

        stage('create package') {

            def commitId = shellLib.pipe("git rev-parse HEAD")
            def pomVersion = pomLib.version(pwd() + "/pom.xml")

            sh("mvn clean package")

            jenkinsUnitRunner = load("test/groovy/jenkinsUnit/runner.groovy")
            jenkinsUnitRunner.run("test/groovy/jenkinsUnit/test")

            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "global.github", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                def repositoryUrl = shellLib.pipe("git config --get remote.origin.url")
                def authenticatedUrl = gitLib.authenticatedUrl(repositoryUrl, env.USERNAME, env.PASSWORD)
                echo("setting remote to authenticated url : ${authenticatedUrl}")
                sh("git remote set-url origin ${authenticatedUrl} &> /dev/null")
                sh("git tag -af ${pomVersion} -m \"Built version: ${pomVersion}\" ${commitId}")
                sh("git push --tags")
            }
        }

        stage('promote package') {
            def pomVersion = pomLib.version(pwd() + "/pom.xml")
            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'global.bintray', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
                def credentials = "'${env.USERNAME}':'${env.PASSWORD}'"
                sh("curl -u ${credentials} -T target/*.zip \"https://api.bintray.com/content/buildit/maven/jenkins-pipeline-libraries/${pomVersion}/jenkins-pipeline-libraries-${pomVersion}.zip?publish=1\"")
            }
        }

        stage('increment version') {
            def majorVersion = pomLib.majorVersion(pwd() + "/pom.xml")
            def minorVersion = pomLib.minorVersion(pwd() + "/pom.xml").toInteger()
            def patchVersion = pomLib.patchVersion(pwd() + "/pom.xml").toInteger()
            def newVersion = "${majorVersion}.${minorVersion + 1}.0"
            if (patchVersion > 0) {
                newVersion = "${majorVersion}.${minorVersion}.${patchVersion + 1}"
            }

            withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "global.github", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
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
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "global.github", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            // delete the tag off origin
            sh("git push origin :refs/tags/${pomVersion}")
            sh("git fetch --tags --prune")
        }
    }
    throw err
}
