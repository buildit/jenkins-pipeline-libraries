package lib

import org.junit.Before
import org.junit.Test
import utilities.ScriptLoader

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.Matchers.containsInAnyOrder
import static org.hamcrest.core.StringContains.containsString
import static org.hamcrest.core.StringStartsWith.startsWith
import static org.junit.Assert.assertThat
import static utilities.AssertAndExecute.assertCommandAndExecute
import static utilities.ResourcePath.resourcePath

class GitTest {

    def GroovyShell shell
    def git
    def shellCommands = []

    @Before
    void setUp() {
        git = ScriptLoader.load("git")
        git.metaClass.sh = { String s -> shellCommands.add(s) }
    }

    @Test
    void shouldSetCorrectRemoteUrl() {

        https://github.com/buildit/jenkins-pipeline-libraries.git

        def source = "feature"
        def target = "master"
        def repositoryUrl = "https://github.com/buildit/jenkins-pipeline-libraries.git"
        def authenticatedUrl = "https://USERNAME:PASSWORD@github.com/buildit/jenkins-pipeline-libraries.git "
        def credentialsId = UUID.randomUUID().toString()

        git.mergeBranch(source, target, repositoryUrl, credentialsId)

        assertThat(shellCommands[0], startsWith("git remote set-url"))
        assertThat(shellCommands[0], containsString("${authenticatedUrl}"))
    }

    @Test
    void shouldCheckoutTarget() {

        def source = "feature"
        def target = "master"
        def repositoryUrl = "https://github.com/buildit/jenkins-pipeline-libraries.git"
        def credentialsId = UUID.randomUUID().toString()

        git.mergeBranch(source, target, repositoryUrl, credentialsId)

        assertThat(shellCommands[2], startsWith("git checkout ${target}"))
    }

    @Test
    void shouldMergeOrigin() {

        def source = "feature"
        def target = "master"
        def repositoryUrl = "https://github.com/buildit/jenkins-pipeline-libraries.git"
        def credentialsId = UUID.randomUUID().toString()

        git.mergeBranch(source, target, repositoryUrl, credentialsId)

        assertThat(shellCommands[3], startsWith("git merge ${source}"))
    }

    @Test
    void shouldPushOrigin() {

        def source = "feature"
        def target = "master"
        def repositoryUrl = "https://github.com/buildit/jenkins-pipeline-libraries.git"
        def credentialsId = UUID.randomUUID().toString()

        git.mergeBranch(source, target, repositoryUrl, credentialsId)

        assertThat(shellCommands[4], startsWith("git push origin ${target}"))
    }

    @Test
    void shouldCallGitCommandCorrectly() {

        def source = "feature"
        def target = "master"
        def repositoryUrl = "https://github.com/buildit/jenkins-pipeline-libraries.git"
        def credentialsId = UUID.randomUUID().toString()

        def args = null

        git.metaClass.git = { Map m -> args = m }

        git.mergeBranch(source, target, repositoryUrl, credentialsId)

        assertThat(args.branch as String, equalTo(target))
        assertThat(args.url as String, equalTo(repositoryUrl))
        assertThat(args.credentialsId as String, equalTo(credentialsId))
    }

    @Test
    void shouldListBranches() {

        def branch = "master"
        def repositoryUrl = "https://github.com/buildit/jenkins-pipeline-libraries.gitZ"
        def credentialsId = UUID.randomUUID().toString()
        def expectedBranches = ["master", "release/1.0"]

        def args = null

        git.metaClass.git = { Map m -> args = m }

        git.metaClass.getShell = {
            [pipe: { String s ->
                assertCommandAndExecute("git branch -r", s, {
                    return new File(resourcePath("branches.txt", "git")).text
                })
            }]
        }

        def branches = git.listBranches(repositoryUrl, branch, credentialsId)

        assertThat(branches, equalTo(expectedBranches))
    }

    @Test
    void shouldGetShortCommit() {
        def commit = "23rnc2vn2vnf2mc2"

        def shell = new Object()
        shell.metaClass.pipe = { commit }
        git.metaClass.getShell = {
            [pipe: { String s -> commit }]
        }

        def shortCommit = git.getShortCommit()

        assertThat(commit, startsWith(shortCommit))
    }

    @Test
    void shouldGetCommitMessage() {
        def shell = new Object()
        def pipeCommands = []

        git.metaClass.getShell = {
            [pipe: { String s ->
                pipeCommands.add(s)
                return "Last message"
            }]
        }

        def commitMsg = git.getCommitMessage()

        assertThat(pipeCommands[0], startsWith("git log"))
        assertThat(commitMsg, equalTo("Last message"))
    }

    @Test
    void shouldConfigureCorrectGitIdentity() {

        def gitUsername = "Spiderman"
        def gitEmail = "peter.parker@dailybugle.com"

        git.configureIdentity(gitUsername, gitEmail)

        assertThat(shellCommands, containsInAnyOrder("git config user.name ${gitUsername}" as String, "git config user.email ${gitEmail}" as String))
    }

}
