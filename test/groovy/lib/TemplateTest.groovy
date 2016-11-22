package lib

import org.junit.Before
import org.junit.Test

import static org.hamcrest.core.IsEqual.equalTo
import static org.junit.Assert.assertThat
import static utilities.ScriptLoader.load

class TemplateTest {

    def template

    @Before
    void setUp() {
        template = load("template")
    }

    @Test
    void shouldDeriveCorrectArtifactNameFromPomValues() {
        def map = [pom: [project: [artifactId: "workflow-libs", version: "1.0.0"]]]
        String result = template.transform('target/${pom.project.artifactId}-${pom.project.version}.jar', map)
        assertThat(result, equalTo("target/workflow-libs-1.0.0.jar"))
    }

    @Test
    void shouldDeriveCorrectArtifactNameFromPomAndOtherValues() {
        def uniqueVersionValue = "12345"
        def map = [pom: [project: [artifactId: "workflow-libs"]], "uniqueVersion": uniqueVersionValue]
        String result = template.transform('target/${pom.project.artifactId}-${uniqueVersion}.jar', map)
        assertThat(result, equalTo("target/workflow-libs-${uniqueVersionValue}.jar" as String))
    }
}
