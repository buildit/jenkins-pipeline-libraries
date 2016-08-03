shell = load "lib/shell.groovy"

def login(convoxRack, convoxPassword) {
    sh "convox login ${convoxRack} --password ${convoxPassword}"
}

def deploy(appName, description) {
    sh "convox deploy --app ${appName}-staging --description '${description}'"
}

def waitUntilDeployed(appName) {
    for (int i=0; i < 10; i++) {
        def status = shell.pipe("convox apps info --app ${appName} | grep Status | sed 's/Status *\\(.*\\)/\\1/'").trim()
        echo "${appName} is ${status}"
        if (status == "running") return
        sleep 30
    }
    error "Application failed to start running within 5 minutes"
}

def ensureSecurityGroupSet(appName, securityGroup) {

    if (!securityGroup.startsWith('sg-')) {
        error "Ensure you use the AWS Group ID for the security group"
        return
    }

    def groupId = shell.pipe("convox apps info --app ${appName} | grep SecurityGroup | sed 's/SecurityGroup *\\(.*\\)/\\1/'").trim()

    if (groupId != securityGroup) {
        sh "convox params set SecurityGroup=${securityGroup} --app ${appName}"
    }
}

return this
