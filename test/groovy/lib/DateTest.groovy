package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class DateTest {

    def date

    @Before
    void setUp() {
        date = ScriptLoader.load("date")
    }

    @Test
    void shouldPipeResult() {
        def result = date.timestamp(new Date(1469351339000))
        assertThat(result as String, equalTo("20160724090859"))
    }
}
