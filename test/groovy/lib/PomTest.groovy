package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.core.IsEqual.equalTo
import static org.junit.Assert.assertThat
import static utilities.ResourcePath.resourcePath

class PomTest {

    def pom

    @Before
    void setUp() {
        pom = ScriptLoader.load("pom")
    }

    @Test
    void shouldReturnPomVersion() {
        String result = pom.version(resourcePath("pom.xml", "pom"))
        assertThat(result, equalTo("1.0.0"))
    }

    @Test
    void shouldReturnArtifactId() {
        String result = pom.artifactId(resourcePath("pom.xml", "pom"))
        assertThat(result, equalTo("jenkins-pipeline-libraries"))
    }

    @Test
    void shouldReturnGroupId() {
        String result = pom.groupId(resourcePath("pom.xml", "pom"))
        assertThat(result, equalTo("com.wiprodigital"))
    }
}
