package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class SlackTest {

    def slack
    def shellCommands = []

    @Before
    void setUp() {
        shellCommands = []
        slack = ScriptLoader.load("slack")
        slack.metaClass.sh = { String s -> shellCommands.add(s) }
    }

    @Test
    void shouldNotifySlack() {
        def title = "Success"
        def text = "Your project has been deployed with last commit msg we've set"
        def color = "green"
        def icon = "http://path-to-some/test.img"
        def channel = "test-channel"

        slack.notify(title, text, color, icon, channel)

        assertThat(shellCommands.size(), equalTo(1))
    }
}
