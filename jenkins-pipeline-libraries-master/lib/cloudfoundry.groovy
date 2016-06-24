import groovy.json.JsonSlurper

shell = load 'lib/shell.groovy'

def push(appName, hostName, appLocation, version, cfSpace, cfOrg, cfApiEndpoint) {
    authenticate(cfApiEndpoint, cfOrg, cfSpace) {
        sh "cf push ${appName} -p ${appLocation} -n ${hostName} --no-start"
        sh "cf set-env ${appName} VERSION ${version}"
        sh "cf start ${appName}"
    }
}

private authenticate(cfApiEndpoint, cfOrg=null, cfSpace=null, closure) {
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "cloudfoundry-credentials", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
        sh("cf api ${cfApiEndpoint}")
        sh("cf auth ${env.USERNAME} ${env.PASSWORD}")
        if (cfOrg && cfSpace) {
            sh("cf target -o ${cfOrg}")
            sh("cf target -s ${cfSpace}")
        }
        closure()
    }
}

def mapRoute(appName, host, cfSpace, cfOrg, cfApiEndpoint) {
    def activeAppName = getActiveAppNameForRoute(host, cfApiEndpoint)
    if(appName.equals(activeAppName)){
        echo("Error mapping route. ${appName} already mapped to this route")
        return
    }
    if(activeAppName){
        input message: "Canary deployment initiated. Do you want to map ${appName} to ${host} along with ${activeAppName}?"
    }
    def domains = getDomains(cfSpace, cfOrg, cfApiEndpoint)
    for(int i = 0; i < (domains.resources.size() as Integer); i++){
        authenticate(cfApiEndpoint, cfOrg, cfSpace) {
            sh("cf map-route ${appName} ${domains.resources[i].entity.name} -n ${host}")
        }
    }
    if(activeAppName){
        input message: "Do you want to remove ${host} mapping from ${activeAppName}"
        for(int i = 0; i < (domains.resources.size() as Integer); i++){
            authenticate(cfApiEndpoint, cfOrg, cfSpace) {
                sh("cf unmap-route ${activeAppName} ${domains.resources[i].entity.name} -n ${host}")
            }
        }
    }
}

def getOrganizations(cfApiEndpoint) {
    return parseJson("/v2/organizations", cfApiEndpoint)
}

def getOrganization(cfOrg, cfApiEndpoint) {
    return getEntityByName(cfOrg, "/v2/organizations", cfApiEndpoint)
}

def getSpace(cfSpace, cfOrg, cfApiEndpoint) {
    def org = getOrganization(cfOrg, cfApiEndpoint)
    getEntityByName(cfSpace, org.entity.spaces_url, cfApiEndpoint)
}

def getDomains(cfSpace, cfOrg, cfApiEndpoint) {
    def space = getSpace(cfSpace, cfOrg, cfApiEndpoint)
    parseJson(space.entity.domains_url, cfApiEndpoint)
}

def parseJson(url, cfApiEndpoint) {
    authenticate(cfApiEndpoint) {
        def contents = shell.pipe("cf curl \"${url}\"") as String
        new JsonSlurper().parseText(contents)
    }
}

def getEntityByName(name, url, cfApiEndpoint){
    def result = parseJson(url,cfApiEndpoint)
    for(int i = 0; i < (result.resources.size() as Integer); i++){
        if(name.equals(result.resources[i].entity.name)){
            return result.resources[i]
        }
    }
}


def getActiveAppNameForRoute(host, cfApiEndpoint){
    def routes = parseJson("/v2/routes?q=host:${host}", cfApiEndpoint)
    if(routes.resources.size() == 0){
        return null
    }
    for(int i = 0; i < (routes.resources.size() as Integer); i++) {
        def apps = parseJson(routes.resources[i].entity.apps_url, cfApiEndpoint)
        for(int j = 0; j < (apps.resources.size() as Integer); j++) {
            if("STARTED".equals(apps.resources[j].entity.state)){
                return apps.resources[j].entity.name
            }
        }
    }
    return null
}


return this
