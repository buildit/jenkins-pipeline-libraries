
def mergeBranch(source, target, repositoryUrl, credentialsId){
    ws{
        git poll: false, changelog: false, url: repositoryUrl, branch: target, credentialsId: credentialsId
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentialsId, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            def authenticatedUrl = authenticatedUrl(repositoryUrl, env.USERNAME, env.PASSWORD)
            sh("git remote set-url origin ${authenticatedUrl} &> /dev/null")
            sh("git fetch --all &> /dev/null")
            sh("git checkout ${target} &> /dev/null")
            sh("git merge ${source} &> /dev/null")
            sh("git push origin ${target} &> /dev/null")
        }
    }
}

def authenticatedUrl(url, username, password){
    encodedUsername = URLEncoder.encode(username as String, "UTF-8")
    encodedPassword = URLEncoder.encode(password as String, "UTF-8")
    def bits = (url as String).split("://")
    if(bits.length == 2){
        return bits[0] + "://${encodedUsername}:${encodedPassword}@" + bits[1] as String
    }
    return "${encodedUsername}:${encodedPassword}@" + bits[0] as String
}

// ensure we return 'this' on last line to allow this script to be loaded into flows
return this
