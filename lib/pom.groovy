
def version(String path) {
    def contents = readFile(path)
    def project = new XmlSlurper().parseText(contents)
    return project.version.text().trim()
}

def majorVersion(String path) {
    def version = version(path)
    if(version.count(".") == 2){
        return version.trim().split("\\.")[0]
    }
    return ""
}

def minorVersion(String path) {
    def version = version(path)
    if(version.count(".") == 2){
        return version.trim().split("\\.")[1]
    }
    return ""
}

def patchVersion(String path) {
    def version = version(path)
    if(version.count(".") == 2){
        return version.trim().split("\\.")[2]
    }
    return ""
}

def artifactId(String path) {
    def contents = readFile(path)
    def project = new XmlSlurper().parseText(contents)
    return project.artifactId.text().trim()
}

def groupId(String path) {
    def contents = readFile(path)
    def project = new XmlSlurper().parseText(contents)
    return project.groupId.text().trim()
}

return this
