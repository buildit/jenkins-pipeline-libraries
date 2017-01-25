package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertThat

class ToolsTest {

    def tools
    def defaultPath = "/usr/local/bin"

    @Before
    void setUp() {
        tools = ScriptLoader.load("tools")
        tools.env = [PATH: defaultPath]
    }

    @Test
    void shouldRetainOriginalPathWhenConfiguringMaven() {
        def path = "/usr/bin/maven"
        tools.metaClass.tool = { s -> return path}
        tools.configureMaven()
        assertThat(tools.env.PATH as String, containsString("${defaultPath}:"))
    }

    @Test
    void shouldAddMavenVersionToPath() {
        def path = "/usr/bin/maven"
        tools.metaClass.tool = { s -> return path}
        tools.configureMaven()
        assertThat(tools.env.PATH as String, containsString(path))
    }

    @Test
    void shouldAddMavenHomeToEnv() {
        def path = "/usr/bin/maven"
        tools.metaClass.tool = { s -> return path}
        tools.configureMaven()
        assertThat(tools.env.MAVEN_HOME as String, equalTo(path))
    }

    @Test
    void shouldAddDefaultMavenOptsToEnv() {
        tools.configureMaven()
        assertThat(tools.env.MAVEN_OPTS as String, equalTo("-Xmx1024m -XX:MaxPermSize=1024m"))
    }

    @Test
    void shouldAddSpecificMavenVersionToPath() {
        def version = "maven-3"
        tools.metaClass.tool = { s -> return "/usr/bin/${s}" }
        tools.configureMaven(version)
        assertThat(tools.env.PATH as String, containsString("/usr/bin/${version}/bin"))
    }

    @Test
    void shouldAddSpecificMavenOptsToEnv() {
        def mavenOpts = "-Xmx2048m -XX:MaxPermSize=2048m"
        tools.configureMaven("maven", mavenOpts)
        assertThat(tools.env.MAVEN_OPTS as String, equalTo(mavenOpts))
    }

    @Test
    void shouldRetainOriginalPathWhenConfiguringJava() {
        def path = "/usr/bin/java"
        tools.metaClass.tool = { s -> return path}
        tools.configureJava()
        assertThat(tools.env.PATH as String, containsString("${defaultPath}:"))
    }

    @Test
    void shouldAddJavaVersionToPath() {
        def path = "/usr/bin/java"
        tools.metaClass.tool = { s -> return path }
        tools.configureJava()
        assertThat(tools.env.PATH as String, containsString(path))
    }

    @Test
    void shouldAddJavaHomeToEnv() {
        def path = "/usr/bin/java"
        tools.metaClass.tool = { s -> return path }
        tools.configureJava()
        assertThat(tools.env.JAVA_HOME as String, equalTo(path))
    }

    @Test
    void shouldAddSpecificJavaVersionToPath() {
        def version = "java-7"
        tools.metaClass.tool = { s -> return "/usr/bin/${s}" }
        tools.configureJava(version)
        assertThat(tools.env.PATH as String, containsString(":/usr/bin/${version}/bin"))
    }

    @Test
    void shouldAddSpecificJavaHomeToEnv() {
        def version = "java-8"
        tools.metaClass.tool = { s -> return "/usr/bin/${s}" }
        tools.configureJava(version)
        assertThat(tools.env.JAVA_HOME as String,  equalTo("/usr/bin/${version}" as String))
    }

    @Test
    void shouldAddToolToPath() {
        def name = "someTool"
        tools.metaClass.tool = { s -> return "/usr/bin/${name}" }
        tools.configureTool(name)
        assertThat(tools.env.PATH as String, containsString("/usr/bin/${name}/bin"))
    }

    @Test
    void shouldAddToolToPathWithCustomPath() {
        def name = "someTool"
        tools.metaClass.tool = { s -> return "/usr/bin/${name}" }
        tools.configureTool(name, "lib")
        assertThat(tools.env.PATH as String, containsString("/usr/bin/${name}/lib"))
    }

    @Test
    void shouldAddToolToPathWithEmptyCustomPath() {
        def name = "someTool"
        tools.metaClass.tool = { s -> return "/usr/bin/${name}" }
        tools.configureTool(name, "")
        assertThat(tools.env.PATH as String, containsString("/usr/bin/${name}"))
    }
}

