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
    """
    <settings>
        <servers>
            <server>
                <id>${serverId}</id>
                <username>${username}</username>
                <password>${password}</password>
            </server>
        </servers>
    </settings>
    """
}

return this
