package lib

import hudson.util.Secret
import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.core.IsEqual.equalTo
import static org.junit.Assert.assertThat
import static utilities.ReadFromResources.readFromResources

class RemoteConfigurationTest {

    def remoteconfig

    @Before
    void setUp() {
        remoteconfig = ScriptLoader.load("remoteconfiguration")
        remoteconfig.metaClass.env = [:]
        remoteconfig.metaClass.readFile { String name ->
            return readFromResources(name, "remoteconfig/")
        }
        Secret.metaClass.static.decrypt = { String string ->
            return string.reverse()
        }
    }

    @Test
    void shouldReturnCorrectCredentials() {
        remoteconfig.withRemoteCredentials(["cloudfoundry", "sonar"]) { credentials ->
            assertThat(credentials.cloudfoundry.username as String, equalTo("batman"))
            assertThat(credentials.cloudfoundry.password as String, equalTo("password"))
            assertThat(credentials.sonar.username as String, equalTo("spiderman"))
            assertThat(credentials.sonar.password as String, equalTo("secret"))
        }
    }

    @Test
    void shouldReturnCorrectPasswords() {
        remoteconfig.withRemotePasswords(["sonar"]) { def passwords ->
            assertThat(passwords.sonar as String, equalTo("secret"))
        }
    }

    @Test
    void shouldFetchRemoteEnv() {
        def env = remoteconfig.fetchRemoteEnv(["GITHUB_URL"])
        assertThat(env.GITHUB_URL as String, equalTo("https://github.com/"))
    }


}
