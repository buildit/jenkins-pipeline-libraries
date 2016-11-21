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

        assertThat(result.name, equalTo("tag"))
        assertThat(result.choices, equalTo(""))
    }

    @Test
    void shouldSelectTag() {
        def input = []
        ui.metaClass.input = { args -> input = args }

        ui.selectTag([1,2,3])

        assertThat(input.message, equalTo("Select tag"))
    }
}
