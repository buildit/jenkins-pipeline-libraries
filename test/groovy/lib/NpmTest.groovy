package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class NpmTest {

    def npm
    def shell
    def shellCommands = []

    @Before
    void setUp() {
        npm = ScriptLoader.load("npm")
        shell = [:]
        npm.metaClass.getShell = { shell }
    }

    @Test
    void shouldGetVersion() {

        shell.pipe = { String s ->
            shellCommands.add(s)
            return "1   "
        }

        def version = npm.getVersion()

        assertThat(shellCommands.size(), equalTo(1))
        assertThat(version, equalTo("1"))
    }
}
