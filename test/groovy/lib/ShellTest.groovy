package lib

import utilities.ScriptLoader

import static utilities.AssertAndExecute.assertCommandRegexAndExecute


import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

import org.junit.Before
import org.junit.Test

class ShellTest {

    def shell
    def shellCommands = []

    @Before
    void setUp() {
        shell = ScriptLoader.load("shell")
        shellCommands = []
        shell.metaClass.sh = { String s -> shellCommands.add(s)}
    }

    @Test
    void shouldPipeResult() {
        String expectedResult = "Welcome to the BBC"
        String command = "curl -s -k http://www.bbc.co.uk"
        shell.metaClass.sh = { String actual ->
            assertCommandRegexAndExecute("${command}(.*)\\.tmp|rm (.*)\\.tmp", actual, {})
        }
        shell.metaClass.readFile = { String actual ->
            assertCommandRegexAndExecute("(.*)\\.tmp", actual, {return expectedResult})
        }
        def result = shell.pipe(command)
        assertThat(result, equalTo(expectedResult))
    }
}