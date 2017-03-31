import hudson.util.Secret

def withRemotePasswords(List<String> ids, String fileName = "passwords.config", String repositoryUrl = env.CONFIG_REPOSITORY, String branch = "master", cl) {
    def results = [:]
    def passwords = []
    def config = slurpToMap(readConfigFile(fileName, repositoryUrl, branch))
    for (int i = 0; i < ids.size(); i++) {
        def id = ids.get(i)
        def encryptedPassword = config.get("passwords.${id}" as String)
        def password = Secret.decrypt("${encryptedPassword}") as String
        results.put(id, password)
        passwords.add([password: password])
    }
    wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: passwords]) {
        cl(results)
    }
}

def withRemoteCredentials(List<String> ids, String fileName = "credentials.config", String repositoryUrl = env.CONFIG_REPOSITORY, String branch = "master", cl) {
    def results = [:]
    def passwords = []
    def config = slurpToMap(readConfigFile(fileName, repositoryUrl, branch))
    for (int i = 0; i < ids.size(); i++) {
        def id = ids.get(i)
        def username = config.get("credentials.${id}.username" as String)
        def encryptedPassword = config.get("credentials.${id}.password" as String)
        def password = Secret.decrypt("${encryptedPassword}") as String
        results.put(id, [username: username, password: password])
        passwords.add([password: password])
    }
    wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: passwords]) {
        cl(results)
    }
}

def fetchRemoteEnv(List<String> ids, String fileName = "environment.config", String repositoryUrl = env.CONFIG_REPOSITORY, String branch = "master") {
    def results = [:]
    def config = slurpToMap(readConfigFile(fileName, repositoryUrl, branch))
    for (int i = 0; i < ids.size(); i++) {
        def id = ids.get(i)
        def key = config.get("variables.${id}" as String)
        results.put(id, key)
    }
    return results
}

private String readConfigFile(String fileName, String repositoryUrl, String branch) {
    ws {
        git poll: false, changelog: false, url: repositoryUrl, branch: branch
        return readFile(fileName) as String
    }
}

private Map<String, String> slurpToMap(String raw) {
    Map<String, String> map = new HashMap<>()
    def configObject = new ConfigSlurper().parse(raw as String)
    return configObject.flatten(map)
}

return this
