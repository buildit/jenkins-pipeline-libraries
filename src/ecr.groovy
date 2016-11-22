import com.cloudbees.groovy.cps.NonCPS

def getShell() {
    new shell()
}

def authenticate(region='us-west-2'){
    sh(getShell().pipe("aws ecr get-login --region=${region}"))
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
    def result = getShell().pipe("aws ecr list-images --repository-name ${repository} --region=${region} --filter tagStatus=TAGGED --no-paginate")
    return parseResponse(result);
}

return this
