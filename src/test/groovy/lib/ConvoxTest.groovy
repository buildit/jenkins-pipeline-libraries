package lib

import org.junit.Before
import org.junit.Test

import utilities.ScriptLoader

import static org.hamcrest.Matchers.empty
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.not
import static org.hamcrest.CoreMatchers.startsWith
import static org.junit.Assert.assertThat

class ConvoxTest {

    def convox
    def shell
    def errors = []
    def shellCommands = []
    def timeSlept = 0

    @Before
    void setUp() {
        convox = ScriptLoader.load("lib/convox.groovy")
        shell = new Object()
        convox.shell = shell
        convox.metaClass.error = { String error -> errors.add(error) }
        convox.metaClass.sleep = { int time -> timeSlept += time }
        convox.metaClass.sh = { String s -> shellCommands.add(s) }
    }

    @Test
    void shouldLogin() {
        def convoxRack = "convox.rack"
        def convoxPassword = "password"

        convox.login(convoxRack, convoxPassword)

        assertThat(shellCommands[0], startsWith("convox login"))
    }

    @Test
    void shouldDeploy() {
        def appName = "sample-app"
        def description = "v1.0.0"

        convox.deploy(appName, description)

        assertThat(shellCommands[0], startsWith("convox deploy"))
    }

    @Test
    void shouldWaitUntilDeployed() {
        shell.metaClass.pipe = { String s ->
            if (timeSlept == 30) return "running  "
            else return "updating"
        }
        convox.waitUntilDeployed("test-app")

        assertThat(timeSlept, equalTo(30))
    }

    @Test
    void shouldErrorAfterMaxTime() {
        shell.metaClass.pipe = { String s -> return "updating" }

        convox.waitUntilDeployed("test-app")

        assertThat(errors.size(), equalTo(1))
        assertThat(errors[0], not(empty()))
    }
}