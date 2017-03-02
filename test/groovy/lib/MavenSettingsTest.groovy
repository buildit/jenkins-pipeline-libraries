package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import javax.xml.parsers.DocumentBuilderFactory

import static junit.framework.TestCase.assertFalse
import static junit.framework.TestCase.assertTrue
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.hamcrest.xml.HasXPath.hasXPath

class MavenSettingsTest {
    def mavensettings

    def serverId = "my-server"
    def credentialsId = "credentials"
    // withCredentials in WorkflowStub maps the value to the variable name.
    def username = "USERNAME"
    def password = "PASSWORD"

    @Before
    void setUp() {
        mavensettings = ScriptLoader.load("mavensettings")
    }

    @Test
    void withSettingsXmlShouldHaveFileInClosure() {
        mavensettings.withSettingsXml(serverId, credentialsId) { settingsXml ->
            assertTrue(settingsXml.exists())
        }
    }

    @Test
    void withSettingsXmlShouldDeleteFileAfterwards() {
        def settingsXmlFile
        mavensettings.withSettingsXml(serverId, credentialsId) { settingsXml ->
            settingsXmlFile = settingsXml
        }

        assertFalse(settingsXmlFile.exists())
    }

    @Test
    void withSettingsXmlShouldCreateFileWithCorrectSettings() {
        mavensettings.withSettingsXml(serverId, credentialsId) { settingsXml ->
            def lines = settingsXml.collect { it }
            assertThat(lines, hasItem(containsString("<settings>")))
            assertThat(lines, hasItem(containsString("<servers>")))
            assertThat(lines, hasItem(containsString("<server>")))
            assertThat(lines, hasItem(containsString("<id>${serverId}</id>")))
            assertThat(lines, hasItem(containsString("<username>${username}</username>")))
            assertThat(lines, hasItem(containsString("<password>${password}</password>")))
        }
    }

    @Test
    void createXmlShouldGenerateSettingsXml() {
        def xml = mavensettings.createXml(serverId, username, password)
        def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        def inputStream = new ByteArrayInputStream(xml.bytes)
        def xmlDoc = builder.parse(inputStream)

        assertThat(xmlDoc, hasXPath("/settings/servers/server/id", equalTo(serverId)))
        assertThat(xmlDoc, hasXPath("/settings/servers/server/username", equalTo(username)))
        assertThat(xmlDoc, hasXPath("/settings/servers/server/password", equalTo(password)))

        inputStream.close()
    }
}
