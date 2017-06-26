def getShell() {
    new shell()
}

def login(convoxRack, credentialsId = "convox") {
    withCredentials([[$class: 'StringBinding', credentialsId: credentialsId, variable: 'ACCESS_TOKEN']]) {
        sh "convox login ${convoxRack} --password ${ACCESS_TOKEN}"
    }
}

def ensureApplicationCreated(appName) {
    def result = sh returnStatus: true, script: "convox apps info ${appName}"
    if (result) {
        sh "convox apps create ${appName}"
        waitUntilDeployed(appName)
    }
}

def deploy(appName, description) {
    sh "convox deploy --app ${appName}-staging --description '${description}'"
}

def waitUntilDeployed(appName) {
    for (int i=0; i < 50; i++) {
        def status = getShell().pipe("convox apps info --app ${appName} | grep Status | sed 's/Status *\\(.*\\)/\\1/'").trim()
        echo "${appName} is ${status}"
        if (status == "running") return
        sleep 30
    }
    error "Application failed to start running within 5 minutes"
}

def ensureParameterSet(appName, parameter, value) {
    def currentValue = getShell().pipe("convox apps params --app ${appName} | grep ${parameter} | sed 's/${parameter} *\\(.*\\)/\\1/'").trim()

    if (currentValue != value) {
        waitUntilDeployed(appName)
        sh "convox apps params set ${parameter}=${value} --app ${appName}"
        sleep 10
        waitUntilDeployed(appName)
    }
}

def ensureSecurityGroupSet(appName, securityGroup) {
    if (securityGroup != '' && !securityGroup.startsWith('sg-')) {
        error "Ensure you use the AWS Group ID for the security group"
        return
    }

    ensureParameterSet(appName, 'SecurityGroup', securityGroup)
}

def ensureCertificateSet(appName, process, port, certificate) {
    def currentCert = getShell().pipe("convox ssl --app ${appName} | grep ${process}:${port} | cut -d ' ' -f3").trim()
    if (currentCert != certificate) {
        waitUntilDeployed(appName)
        sh "convox ssl update ${process}:${port} ${certificate} --app ${appName}"
        sleep 10
        waitUntilDeployed(appName)
    }
}

return this
