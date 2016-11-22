package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.core.IsEqual.equalTo
import static org.junit.Assert.assertThat
import static utilities.ReadFromResources.readFromResources

class EcrTest {

    def ecr
    def shell
    def shellCommands = []

    @Before
    void setUp() {
        ecr = ScriptLoader.load("ecr")
        shellCommands = []
        shell = [:]
        ecr.metaClass.getShell = { shell }
        ecr.metaClass.sh = { String s -> shellCommands.add(s) }
    }

    @Test
    void shouldExecuteReturnedCommandInShell() {
        shell.pipe = { String s ->
            return readFromResources(s, "ecrapi/")
        }
        ecr.authenticate()
        assertThat(shellCommands[0], equalTo("docker login -u AWS -p CiD6lc4XIJw -e none https://601737501053.dkr.ecr.us-west-2.amazonaws.com"))
    }

    @Test
    void shouldReturnListOfImageTags() {
        shell.pipe = { String s ->
            return readFromResources(s, "ecrapi/")
        }
        def result = ecr.imageTags("hello-boot")
        assertThat(result.size() as Integer, equalTo(2))
        assertThat(result[0], equalTo("latest"))
        assertThat(result[1], equalTo("1.0.9"))
    }

    @Test
    void shouldReturnListOfImageTagsUsingRegionArgument() {
        shell.pipe = { String s ->
            return readFromResources(s, "ecrapi/")
        }
        def result = ecr.imageTags("hello-boot", "us-west-1")
        assertThat(result.size() as Integer, equalTo(2))
        assertThat(result[0], equalTo("latest"))
        assertThat(result[1], equalTo("1.0.8"))
    }
}
