shell = load "lib/shell.groovy"

def login(convoxRack, convoxPassword) {
    sh "convox login ${convoxRack} --password ${convoxPassword}"
}

def deploy(appName, description) {
    sh "convox deploy --app ${appName}-staging --description '${description}'"
}

def waitUntilDeployed(appName) {
    for (int i=0; i < 10; i++) {
        def status = shell.pipe("convox apps info --app ${appName}-staging | grep Status | sed 's/Status *\\(.*\\)/\\1/'")
        echo "${appName}-staging is ${status}"
        if (status == "running") return
        sleep 30
    }
}

return this
