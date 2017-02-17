package buildit

class Jenkins {
    static Map getGlobalEnv() {
        def envProps = jenkins.model.Jenkins.getInstance().globalNodeProperties.get(hudson.slaves.EnvironmentVariablesNodeProperty)
        return envProps ? envProps.envVars : [:]
    }
}
