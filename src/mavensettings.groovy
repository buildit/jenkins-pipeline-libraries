import groovy.xml.MarkupBuilder

import static java.util.UUID.randomUUID

def withSettingsXml(serverId, credentialsId, closure) {
    def filename
    try {
        def uuid = randomUUID() as String
        filename = "${uuid}-settings.xml"

        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentialsId, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            writeFile file: filename, text: createXml(serverId, USERNAME, PASSWORD)
        }

        closure(filename)
    } finally {
        // Ensure temporary settings.xml is deleted.
        def settingsXml = new File(filename)
        if (settingsXml.exists()) {
            settingsXml.delete()
        }
    }
}

def createXml(serverId, username, password) {
    def writer = new StringWriter()
    def xml = new MarkupBuilder(writer)
    xml.settings() {
        delegate.servers() {
            delegate.server() {
                delegate.id(serverId)
                delegate.username(username)
                delegate.password(password)
            }
        }
    }

    return writer.toString()
}

return this
