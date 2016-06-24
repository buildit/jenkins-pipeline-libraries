package lib

import static org.hamcrest.CoreMatchers.*

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.junit.Assert.assertThat

class ToolsTest {

    def tools

    @Before
    void setUp() {
        tools = ScriptLoader.load("lib/tools.groovy")
    }

    @Test
    void shouldPipeResult() {
        tools.env = [:]
        tools.metaClass.tool = { s -> return "/usr/bin/maven"}
        tools.configureMaven()
        assertThat(tools.env.PATH as String, containsString(":/usr/bin/maven/bin"))
    }
}
