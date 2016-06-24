package lib

import org.codehaus.groovy.control.CompilerConfiguration
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.hamcrest.core.IsEqual.equalTo
import static org.junit.Assert.assertThat

class PomTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public static final String SCRIPT_NAME = "lib/pom.groovy"
    def GroovyShell shell
    def pom


    @Before
    void setUp() {
        def CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = new WorkflowStub().getClass().getCanonicalName()
        def Binding binding = new Binding()
        shell = new GroovyShell(this.class.classLoader, binding, compilerConfiguration)
        pom = shell.evaluate(new File(SCRIPT_NAME))

    }

    @Test
    void shouldReturnPomVersion() {
        String result = pom.version("pom.xml")
        assertThat(result, equalTo("1.0.0"))
    }

    @Test
    void shouldReturnArtifactId() {
        String result = pom.artifactId("pom.xml")
        assertThat(result, equalTo("jenkins-pipeline-examples"))
    }

    @Test
    void shouldReturnGroupId() {
        String result = pom.groupId("pom.xml")
        assertThat(result, equalTo("com.wiprodigital"))
    }
}