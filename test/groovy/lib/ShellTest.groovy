package lib

import org.junit.Test
import utilities.ScriptLoader

class ShellTest {

    @Test
    void shouldPipeResult() {
        def shell = ScriptLoader.load("shell")
        String expectedResult = "Welcome to the BBC"
        String command = "curl -s -k http://www.bbc.co.uk"
        shell.metaClass.sh = { params ->
            assert params.returnStdout
            assert params.script == command
            return expectedResult
        }

        def result = shell.pipe(command)
        assert result == expectedResult
    }
}
