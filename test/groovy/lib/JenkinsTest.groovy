package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.core.IsCollectionContaining.hasItem
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThat

class JenkinsTest {

    def jenkins
    def filesystem
    def template
    def shellCommands = []
    def jenkinsUrl = "http://localhost:9000/jenkins"
    def jenkinsCredentials = "20nf2m2mf2msvsvafm2fm2mfm2"
    def jenkinsScmPoll = "* * * * *"

    @Before
    void setUp() {
        shellCommands = []
        template = new Object()
        filesystem = [:]
        jenkins = ScriptLoader.load("jenkins")
        jenkins.metaClass.getTemplate = { template }
        jenkins.metaClass.getFilesystem = { filesystem }
        jenkins.metaClass.sh = { String s -> shellCommands.add(s) }
    }

    @Test
    void shouldCreatePipelineJob() {
        def jobName = "test-project"
        def branch = "master"
        def gitUrl = "git@bitbucket.org:digitalrigbitbucketteam/test-project.git"
        def pipelinePath = "./piplines/staging.groovy"

        UUID.metaClass.static.randomUUID = { "temp_file" }
        template.metaClass.transform = { text, mapping -> "<xml>" }

        jenkins.createPipelineJob(jobName, gitUrl, branch, pipelinePath, true, jenkinsUrl, jenkinsCredentials, jenkinsScmPoll)

        // clean up file created as shell is mocked
        new File("temp_file.xml").delete()

        assertThat(shellCommands, hasItem("curl --header 'Content-Type: application/xml' --data-binary @temp_file.xml ${jenkinsUrl}/createItem?name=${jobName}" as String))
    }

    @Test
    void shouldCreateFreestyleJob() {
        def gitUrl = "git@bitbucket.org:digitalrigbitbucketteam/test-project.git"

        jenkins.metaClass.git = {}
        filesystem.listing = {
            new ArrayList(["./jenkinsJobs", "./jenkinsJobs/backup.xml", "./jenkinsJobs/pr-builder.xml"])
        }

        jenkins.createFreestyleJobs(gitUrl, jenkinsUrl)

        assertEquals(shellCommands.size(), 2)
        assertThat(shellCommands, hasItem("curl --header 'Content-Type: application/xml' --data-binary @./jenkinsJobs/backup.xml ${jenkinsUrl}/createItem?name=backup" as String))
        assertThat(shellCommands, hasItem("curl --header 'Content-Type: application/xml' --data-binary @./jenkinsJobs/pr-builder.xml ${jenkinsUrl}/createItem?name=pr-builder" as String))
    }
}
