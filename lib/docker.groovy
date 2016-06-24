shell = load "lib/shell.groovy"

def hostIp(container) {
    shell.pipe(/docker inspect --format='{{.NetworkSettings.IPAddress}}' / + container.id).trim()
}

return this