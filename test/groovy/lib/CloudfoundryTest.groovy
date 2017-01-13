package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.core.IsCollectionContaining.hasItem
import static org.hamcrest.core.IsEqual.equalTo
import static org.hamcrest.core.IsNot.not
import static org.hamcrest.core.StringContains.containsString
import static org.junit.Assert.assertThat
import static utilities.ReadFromResources.readFromResources

class CloudfoundryTest {

    def cloudfoundry
    def shell
    def errors = []
    def shellCommands = []

    def cfSpace = "development"
    def appName = "helloworld"
    def hostName = "helloworld-v1-development"
    def appLocation = "target/helloworld.jar"
    def uniqueVersion = "1.0.1.20160208100933"
    def cfOrg = "big-red-fun-bus"
    def cfApiEndpoint = "https://api.run.pivotal.io"
    def credentialsId = "credentials"

    @Before
    void setUp() {
        this.getClass().classLoader
        cloudfoundry = ScriptLoader.load("cloudfoundry")
        errors = []
        shellCommands = []
        shell = [:]
        cloudfoundry.metaClass.getShell = { shell }
        cloudfoundry.metaClass.error = { String s -> errors.add(s) }
        cloudfoundry.metaClass.sh = { String s -> shellCommands.add(s) }
        shell.pipe = { String s ->
            return readFromCfApiResources(s)
        }
    }

    @Test
    void shouldDeployToCloudFoundry() {
        cloudfoundry.push(appName, hostName, appLocation, uniqueVersion, cfSpace, cfOrg, cfApiEndpoint, credentialsId)

        assertThat(shellCommands, hasItem("cf push ${appName} -p ${appLocation} -n ${hostName} --no-start" as String));
        assertThat(shellCommands, hasItem("cf set-env ${appName} VERSION ${uniqueVersion}" as String))
        assertThat(shellCommands, hasItem("cf start ${appName}" as String))
    }

    @Test
    void shouldReturnOrganizations() {
        def result = cloudfoundry.getOrganizations("https://api.run.pivotal.io", credentialsId)
        assertThat(result.resources[0].entity.name as String, equalTo("big-red-fun-bus"))
    }

    @Test
    void shouldReturnDigitalPlatformOrganization() {
        def result = cloudfoundry.getOrganization("big-red-fun-bus", "https://api.run.pivotal.io", credentialsId)
        assertThat(result.entity.name as String, equalTo("big-red-fun-bus"))
    }

    @Test
    void shouldReturnDevelopmentSpace() {
        def result = cloudfoundry.getSpace("development", "big-red-fun-bus", "https://api.run.pivotal.io", credentialsId)
        assertThat(result.entity.name as String, equalTo("development"))
    }

    @Test
    void shouldReturnDomain() {
        def result = cloudfoundry.getDomains("development", "big-red-fun-bus", "https://api.run.pivotal.io", credentialsId)
        assertThat(result.resources[0].entity.name as String, equalTo("cfapps.io"))
    }

    @Test
    void shouldReturnActiveAppName() {
        def result = cloudfoundry.getActiveAppNameForRoute("hello-boot-v1", "https://api.run.pivotal.io", credentialsId)
        assertThat(result as String, equalTo("hello-boot-1-0-4"))
    }

    @Test
    void shouldNotMapRouteWhenRouteAlreadyExistsOnSameApplication() {
        cloudfoundry.mapRoute("hello-boot-1-0-4", "hello-boot-v1", "development", "big-red-fun-bus", "https://api.run.pivotal.io", credentialsId)
        println shellCommands
        assertThat(shellCommands, not(hasItem(containsString("map-route"))))
    }

    @Test
    void shouldMapRouteAndDeletePreviousVersion() {
        cloudfoundry.mapRoute("hello-boot-1-0-5", "hello-boot-v1", "development", "big-red-fun-bus", "https://api.run.pivotal.io", credentialsId)
        assertThat(shellCommands, hasItem(containsString("map-route hello-boot-1-0-5")))
        assertThat(shellCommands, hasItem(containsString("unmap-route hello-boot-1-0-4")))
    }

    @Test
    void shouldAuthenticateCorrectlyWhenDeployingApplications() {
        assertAuthenticationCall()
        cloudfoundry.push(appName, hostName, appLocation, uniqueVersion, cfSpace, cfOrg, cfApiEndpoint, credentialsId)
    }

    @Test
    void shouldAuthenticateCorrectlyWhenMappingRoutes() {
        assertAuthenticationCall()
        cloudfoundry.mapRoute("hello-boot-1-0-5", "hello-boot-v1", cfSpace, cfOrg, cfApiEndpoint, credentialsId)
    }

    private void assertAuthenticationCall(){
        cloudfoundry.metaClass.authenticate = { String cfApiEndpoint, String credentialsId, String cfOrg, String cfSpace, closure ->
            assertThat(cfApiEndpoint as String, equalTo(this.cfApiEndpoint))
            assertThat(credentialsId as String, equalTo(this.credentialsId))
            assertThat(cfOrg as String, equalTo(this.cfOrg))
            assertThat(cfSpace as String, equalTo(this.cfSpace))
        }
    }

    private String readFromCfApiResources(String s) {
        if (s.startsWith("cf curl")) {
            def path = s.split(" ")[2].replace("\"", "")
            return readFromResources(path, "cfapi/")
        }
    }
}
