try {
    currentBuild.result = "SUCCESS"

    stage 'load libraries'
    node() {
        shell = load "lib/shell.groovy"
        pom = load "lib/pom.groovy"
        git = load "lib/git.groovy"
        jenkinsUnitRunner = load "src/it/jenkinsUnit/runner.groovy"

        repositoryUrl = shell.pipe("git config --get remote.origin.url")
        pomVersion = pom.version(pwd() + "/pom.xml")
    }

    stage 'create package'
    node() {
        def commitId = shell.pipe("git rev-parse HEAD")

        sh("mvn clean package")
        jenkinsUnitRunner.run("src/it/lib")

        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "git-credentials", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            def authenticatedUrl = git.authenticatedUrl(repositoryUrl, env.USERNAME, env.PASSWORD)
            sh("git remote set-url origin ${authenticatedUrl} &> /dev/null")
            sh("git tag -a ${pomVersion} -m \"Built version: ${pomVersion}\" ${commitId}")
            sh("git push --tags")
        }
    }

    stage 'promote package'
    node() {
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "bintray-credentials", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            def credentials = "'${env.USERNAME}':'${env.PASSWORD}'"
            sh("curl -u ${credentials} -T target/*.zip \"https://api.bintray.com/content/buildit/maven/jenkins-pipeline-libraries/${pomVersion}/jenkins-pipeline-libraries-${pomVersion}.zip?publish=1\"")
        }
    }

    stage 'increment version'
    node() {
        def majorVersion = pom.majorVersion(pwd() + "/pom.xml")
        def minorVersion = pom.minorVersion(pwd() + "/pom.xml").toInteger()
        def patchVersion = pom.patchVersion(pwd() + "/pom.xml").toInteger()
        def newVersion = "${majorVersion}.${minorVersion + 1}.0"
        if (patchVersion > 0) {
            newVersion = "${majorVersion}.${minorVersion}.${patchVersion + 1}"
        }

        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "git-credentials", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            sh("mvn versions:set -DnewVersion=${newVersion} versions:commit")
            sh("git add pom.xml")
            sh("git commit -m'Bumping version to ${newVersion}'")
            sh("git push origin")
        }
    }
}
catch (err) {
    currentBuild.result = "FAILURE"
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "git-credentials", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        // delete the tag off origin
        sh("git push origin :refs/tags/${pomVersion}")
        sh("git fetch --tags --prune")
    }
    throw err
}
finally {
    sh("git remote set-url origin ${repositoryUrl} &> /dev/null")
}