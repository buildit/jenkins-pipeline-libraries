package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.core.IsCollectionContaining.hasItem

class BintrayTest {
    def bintray
    def shellCommands = []

    def credentialsId = "credentials"
    def artifactId = "hello-world"
    def pomVersion = "1.0.2"
    def fileExtension = "ext"
    def fileLocation = "build/test.ext"
    def bintrayOrg = "big-red-fun-bus"
    def bintrayRepo = "test"

    @Before
    void setUp() {
        bintray = ScriptLoader.load("bintray")
        bintray.metaClass.sh = { String s -> shellCommands.add(s) }
    }

    @Test
    void publishShouldBeEnabledByDefault() {
        bintray.upload(credentialsId, artifactId, pomVersion, fileExtension, fileLocation, bintrayOrg, bintrayRepo)
        assertThat(shellCommands, hasItem(containsString(";publish=1")))
    }

    @Test
    void publishShouldBeDisabledIfTold() {
        bintray.upload(credentialsId, artifactId, pomVersion, fileExtension, fileLocation, bintrayOrg, bintrayRepo, false)
        assertThat(shellCommands, hasItem(containsString(";publish=0")))
    }

    @Test
    void shouldUploadFileToBintray() {
        bintray.upload(credentialsId, artifactId, pomVersion, fileExtension, fileLocation, bintrayOrg, bintrayRepo)
        assertThat(shellCommands, hasItem(equalTo("curl -v -u 'USERNAME':'PASSWORD' -T build/test.ext \"https://api.bintray.com/content/big-red-fun-bus/test/hello-world/1.0.2/hello-world-1.0.2.ext;publish=1\"")))
    }
}
