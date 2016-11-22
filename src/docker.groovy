def getShell() {
    new shell()
}

def hostIp(container) {
    getShell().pipe(/docker inspect --format='{{.NetworkSettings.IPAddress}}' / + container.id).trim()
}

return this
