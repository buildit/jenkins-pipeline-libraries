def upload(credentialsId, artifactId, pomVersion, fileExtension, fileLocation, bintrayOrg, bintrayRepo, shouldPublish=true) {
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentialsId, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        def credentials = "'${env.USERNAME}':'${env.PASSWORD}'"
        def publish = shouldPublish ? 1 : 0
        def uploadUrl = "\"https://api.bintray.com/content/${bintrayOrg}/${bintrayRepo}/${artifactId}/${pomVersion}/${artifactId}-${pomVersion}.${fileExtension};publish=${publish}\""
        sh("curl -v -u ${credentials} -T ${fileLocation} ${uploadUrl}")
    }
}

return this
