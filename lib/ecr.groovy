shell = load "lib/shell.groovy"

def authenticate(region='us-west-2'){
    sh(shell.pipe("aws ecr get-login --region=${region}"))
}

@NonCPS
def parseResponse(result) {
    def list = []
    def object = new groovy.json.JsonSlurper().parseText(result)
    for(int i=0; i < object.imageIds.size(); i++){
        if(object.imageIds[i].imageTag){
            list.add(object.imageIds[i].imageTag)
        }
    }
    return list
}

def imageTags(repository, region='us-west-2'){
    def result = shell.pipe("aws ecr list-images --repository-name ${repository} --region=${region}")
    return parseResponse(result);
}

return this