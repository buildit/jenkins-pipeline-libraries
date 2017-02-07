package lib

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import utilities.ScriptLoader

import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class ConfigslurperTest {

    def configslurper

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Before
    void setUp() {
        configslurper = ScriptLoader.load("configslurper")
    }

    @Test
    void shouldSlurpSimplePropertiesToMap() {
        def result = configslurper.slurpToMap("password='p@ssw0rd'")

        assertThat(result.password as String, equalTo("p@ssw0rd"))
    }

    @Test
    void shouldSlurpHierarchicalPropertiesToMap() {
        def result = configslurper.slurpToMap("""
            passwords {
                 password='p@ssw0rd'
            }
        """)

        assertThat(result.'passwords.password' as String, equalTo("p@ssw0rd"))
    }

    @Test
    void shouldWriteMapToConfigFile() {
        def file = temp.newFile("passwords.config")

        configslurper.writeToFile([password: 'p@ssw0rd'], file.getAbsolutePath())

        assertThat(file.text as String, containsString("password='p@ssw0rd'"))
    }

    @Test
    void shouldMergeMapToConfigFile() {
        def file = temp.newFile("passwords.config")
        file.write("""
            passwords {
                 password='p@ssw0rd'
            }
        """)

        configslurper.mergeWithFile(['passwords.other': 'p@55w0rd'], file.getAbsolutePath())

        assertThat(file.text as String, containsString("password='p@ssw0rd'"))
        assertThat(file.text as String, containsString("other='p@55w0rd'"))

    }
}
