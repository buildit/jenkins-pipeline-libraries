import groovy.json.JsonSlurperClassic

def getShell() {
    new shell()
}

def push(appName, hostName, appLocation, version, cfSpace, cfOrg, cfApiEndpoint, credentialsId) {
    authenticate(cfApiEndpoint, credentialsId, cfOrg, cfSpace) {
        sh "cf push ${appName} -p ${appLocation} -n ${hostName} --no-start"
        sh "cf set-env ${appName} VERSION ${version}"
        sh "cf start ${appName}"
    }
}

private authenticate(cfApiEndpoint, credentialsId, cfOrg=null, cfSpace=null, closure) {
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentialsId, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        sh("cf api ${cfApiEndpoint}")
        sh("cf auth ${env.USERNAME} ${env.PASSWORD}")
        if (cfOrg && cfSpace) {
            sh("cf target -o ${cfOrg}")
            sh("cf target -s ${cfSpace}")
        }
        closure()
    }
}

def mapRoute(appName, host, cfSpace, cfOrg, cfApiEndpoint, credentialsId) {
    def activeAppName = getActiveAppNameForRoute(host, cfApiEndpoint, credentialsId)
    if(appName.equals(activeAppName)){
        echo("Error mapping route. ${appName} already mapped to this route")
        return
    }
    if(activeAppName){
        input message: "Canary deployment initiated. Do you want to map ${appName} to ${host} along with ${activeAppName}?"
    }
    def domains = getDomains(cfSpace, cfOrg, cfApiEndpoint, credentialsId)
    for(int i = 0; i < (domains.resources.size() as Integer); i++){
        authenticate(cfApiEndpoint, credentialsId, cfOrg, cfSpace) {
            sh("cf map-route ${appName} ${domains.resources[i].entity.name} -n ${host}")
        }
    }
    if(activeAppName){
        input message: "Do you want to remove ${host} mapping from ${activeAppName}"
        for(int i = 0; i < (domains.resources.size() as Integer); i++){
            authenticate(cfApiEndpoint, credentialsId, cfOrg, cfSpace) {
                sh("cf unmap-route ${activeAppName} ${domains.resources[i].entity.name} -n ${host}")
            }
        }
    }
}

def getOrganizations(cfApiEndpoint, credentialsId) {
    return parseJson("/v2/organizations", cfApiEndpoint, credentialsId)
}

def getOrganization(cfOrg, cfApiEndpoint, credentialsId) {
    return getEntityByName(cfOrg, "/v2/organizations", cfApiEndpoint, credentialsId)
}

def getSpace(cfSpace, cfOrg, cfApiEndpoint, credentialsId) {
    def org = getOrganization(cfOrg, cfApiEndpoint, credentialsId)
    getEntityByName(cfSpace, org.entity.spaces_url, cfApiEndpoint, credentialsId)
}

def getDomains(cfSpace, cfOrg, cfApiEndpoint, credentialsId) {
    def space = getSpace(cfSpace, cfOrg, cfApiEndpoint, credentialsId)
    parseJson(space.entity.domains_url, cfApiEndpoint, credentialsId)
}

def parseJson(url, cfApiEndpoint, credentialsId) {
    authenticate(cfApiEndpoint, credentialsId) {
        def contents = getShell().pipe("cf curl \"${url}\"") as String
        new JsonSlurperClassic().parseText(contents)
    }
}

def getEntityByName(name, url, cfApiEndpoint, credentialsId){
    def result = parseJson(url, cfApiEndpoint, credentialsId)
    for(int i = 0; i < (result.resources.size() as Integer); i++){
        if(name.equals(result.resources[i].entity.name)){
            return result.resources[i]
        }
    }
}


def getActiveAppNameForRoute(host, cfApiEndpoint, credentialsId){
    def routes = parseJson("/v2/routes?q=host:${host}", cfApiEndpoint, credentialsId)
    if(routes.resources.size() == 0){
        return null
    }
    for(int i = 0; i < (routes.resources.size() as Integer); i++) {
        def apps = parseJson(routes.resources[i].entity.apps_url, cfApiEndpoint, credentialsId)
        for(int j = 0; j < (apps.resources.size() as Integer); j++) {
            if("STARTED".equals(apps.resources[j].entity.state)){
                return apps.resources[j].entity.name
            }
        }
    }
    return null
}


return this
