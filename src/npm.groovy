def getShell() {
    new shell()
}

def getVersion() {
    def version = getShell().pipe("node -e \"console.log(require('./package.json').version);\"")
    return version.trim()
}

return this
