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
        date = ScriptLoader.load("lib/date.groovy")
    }

    @Test
    void shouldPipeResult() {
        def result = date.timestamp(new Date(116,6,24,10,8,59))
        assertThat(result as String, equalTo("20160724090859"))
    }
}