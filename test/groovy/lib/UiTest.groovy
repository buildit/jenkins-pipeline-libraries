package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class UiTest {

    def ui

    @Before
    void setUp() {
        ui = ScriptLoader.load("ui")
    }

    @Test
    void shouldGetDropdown() {

        def result = ui.dropdown("tag", "Tag", { ["latest"] })

        assertThat(result.name, equalTo("tag"))
        assertThat(result.choices, equalTo("\nlatest\n"))
    }

    @Test
    void shouldGetEmptyDropdown() {

        def result = ui.dropdown("tag", "Tag", {})

        assertThat(result.name as String, equalTo("tag"))
        assertThat(result.choices as String, equalTo(""))
    }

    @Test
    void shouldSelectTag() {
        def input = []
        ui.metaClass.input = { args -> input = args }

        ui.selectTag([1, 2, 3])

        assertThat(input.message, equalTo("Select tag"))
    }

    @Test
    void shouldReturnStringParameterDefinition() {

        def clazz = "hudson.model.StringParameterDefinition"
        def name = "test"
        def description = "Just a test"
        def defaultValue = "sensible default"

        def result = ui.stringbox(name, description, defaultValue)

        assertThat(result.$class as String, equalTo(clazz))
        assertThat(result.name as String, equalTo(name))
        assertThat(result.description as String, equalTo(description))
        assertThat(result.defaultValue as String, equalTo(defaultValue))
    }

    @Test
    void shouldReturnPasswordParameterDefinition() {

        def clazz = "hudson.model.PasswordParameterDefinition"
        def name = "test"
        def description = "Just a test"
        def defaultValue = "sensible default"

        def result = ui.passwordbox(name, description, defaultValue)

        assertThat(result.$class as String, equalTo(clazz))
        assertThat(result.name as String, equalTo(name))
        assertThat(result.description as String, equalTo(description))
        assertThat(result.defaultValue as String, equalTo(defaultValue))
    }

    @Test
    void shouldReturnTextParameterDefinition() {

        def clazz = "hudson.model.TextParameterDefinition"
        def name = "test"
        def description = "Just a test"
        def defaultValue = "sensible default"

        def result = ui.textbox(name, description, defaultValue)

        assertThat(result.$class as String, equalTo(clazz))
        assertThat(result.name as String, equalTo(name))
        assertThat(result.description as String, equalTo(description))
        assertThat(result.defaultValue as String, equalTo(defaultValue))
    }
}
