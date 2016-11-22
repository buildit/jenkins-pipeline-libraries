shell = load "lib/shell.groovy"

def getVersion() {
    def version = shell.pipe("node -e \"console.log(require('./package.json').version);\"")
    return version.trim()
}

return this