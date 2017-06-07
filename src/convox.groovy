def getShell() {
    new shell()
}

def login(convoxRack, convoxPassword) {
    sh "convox login ${convoxRack} --password ${convoxPassword}"
}

def deploy(appName, description) {
    sh "convox deploy --app ${appName}-staging --description '${description}'"
}

def waitUntilDeployed(appName) {
    for (int i=0; i < 10; i++) {
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
        sh "convox apps params set ${parameter}=${value} --app ${appName}"
        waitUntilDeployed(appName)
    }
}

def ensureSecurityGroupSet(appName, securityGroup) {

    if (!securityGroup.startsWith('sg-')) {
        error "Ensure you use the AWS Group ID for the security group"
        return
    }

    ensureParameterSet(appName, 'SecurityGroup', securityGroup)
}

def ensureCertificateSet(appName, process, port, certificate) {
    def currentCert = getShell().pipe("convox ssl --app ${appName} | grep ${process}:${port} | cut -d ' ' -f3").trim()
    if (currentCert != certificate) {
        sh "convox ssl update ${process}:${port} ${certificate} --app ${appName}"
        waitUntilDeployed("${appName}")
    }
}

return this
