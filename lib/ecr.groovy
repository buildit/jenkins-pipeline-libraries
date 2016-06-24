shell = load "lib/shell.groovy"

def authenticate(region='us-west-2'){
    sh(shell.pipe("aws ecr get-login --region=${region}"))
}

def imageTags(repository, region='us-west-2'){
    def list = []
    def result = shell.pipe("aws ecr list-images --repository-name ${repository} --region=${region}")
    def object = new groovy.json.JsonSlurper().parseText(result)
    for(int i=0; i < object.imageIds.size(); i++){
        if(object.imageIds[i].imageTag){
            list.add(object.imageIds[i].imageTag)
        }
    }
    return list
}

return this