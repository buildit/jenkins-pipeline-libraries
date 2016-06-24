def transform(String template, Map map) {

    Map flattened = flatten(map)
    def keys = flattened.keySet().toArray()
    def result = template
    for (int i = 0; i < keys.size(); i++) {
        def key = keys[i]
        def regex = '${' + key + '}'
        while (result.indexOf(regex) != -1) {
            result = result.replace(regex, flattened.get(key) as String)
        }
    }
    return result
}

private Map flatten(Map map) {
    ConfigObject flattened = new ConfigObject()
    flattened.putAll(map)
    new HashMap(flattened.flatten())
}

return this