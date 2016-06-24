package lib

import static utilities.AssertAndExecute.assertCommandRegexAndExecute

import utilities.AssertCommandAndExecute
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

import org.junit.Before
import org.junit.Test

import org.codehaus.groovy.control.CompilerConfiguration

class ShellTest implements AssertCommandAndExecute {

    public static final String SCRIPT_NAME = "lib/shell.groovy"
    def shell
    def shellCommands = []


    @Before
    void setUp() {
        def CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = new WorkflowStub().getClass().getCanonicalName()
        def Binding binding = new Binding()
        shell = new GroovyShell(this.class.classLoader, binding, compilerConfiguration).evaluate(new File(SCRIPT_NAME))
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