def Map<String, String> slurpToMap(String raw) {
    Map<String, String> map = new HashMap<>()
    def configObject = new ConfigSlurper().parse(raw as String)
    def result = configObject.flatten(map)
    return result
}

def writeToFile(Map map, String fileName){
    def config = new ConfigObject()
    config.putAll(map)
    def file = new File("${fileName}")
    file.createNewFile()
    def writer = new FileWriter(file);
    config.writeTo(writer)
}

def mergeWithFile(Map map, String fileName){
    def raw = readFile(fileName) as String
    def newConfig = new ConfigObject()
    newConfig.putAll(map)
    def toSave = new ConfigSlurper().parse(raw as String).merge(newConfig)
    def resultsFile = new File("${fileName}")
    resultsFile.createNewFile()
    def writer = new FileWriter(resultsFile);
    toSave.writeTo(writer)
}

return this