package lib

import org.codehaus.groovy.control.CompilerConfiguration
import org.junit.Before
import org.junit.Test

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.core.StringContains.containsString
import static org.hamcrest.core.StringStartsWith.startsWith
import static org.junit.Assert.assertThat

class GitTest {

    public static final String SCRIPT_NAME = "lib/git.groovy"
    def GroovyShell shell
    def git
    def shellCommands = []

    @Before
    void setUp() {
        def CompilerConfiguration compilerConfiguration = new CompilerConfiguration()
        compilerConfiguration.scriptBaseClass = new WorkflowStub().getClass().getCanonicalName()
        def Binding binding = new Binding()
        shell = new GroovyShell(this.class.classLoader, binding, compilerConfiguration)

        git = shell.evaluate(new File(SCRIPT_NAME))
        git.metaClass.sh = { String s -> shellCommands.add(s)}
    }

    @Test
    void shouldSetCorrectRemoteUrl() {

        def source = "feature"
        def target = "master"
        def repositoryUrl = "https://stash.hk.hsbc/scm/rdp/workflowLibs.git"
        def authenticatedUrl = "https://USERNAME:PASSWORD@stash.hk.hsbc/scm/rdp/workflowLibs.git "
        def credentialsId = UUID.randomUUID().toString()

        git.mergeBranch(source, target, repositoryUrl, credentialsId)

        assertThat(shellCommands[0], startsWith("git remote set-url"))
        assertThat(shellCommands[0], containsString("${authenticatedUrl}"))
    }

    @Test
    void shouldCheckoutTarget() {

        def source = "feature"
        def target = "master"
        def repositoryUrl = "https://stash.hk.hsbc/scm/rdp/workflowLibs.git"
        def credentialsId = UUID.randomUUID().toString()

        git.mergeBranch(source, target, repositoryUrl, credentialsId)

        assertThat(shellCommands[2], startsWith("git checkout ${target}"))
    }



    @Test
    void shouldMergeOrigin() {

        def source = "feature"
        def target = "master"
        def repositoryUrl = "https://stash.hk.hsbc/scm/rdp/workflowLibs.git"
        def credentialsId = UUID.randomUUID().toString()

        git.mergeBranch(source, target, repositoryUrl, credentialsId)

        assertThat(shellCommands[3], startsWith("git merge ${source}"))
    }

    @Test
    void shouldPushOrigin() {

        def source = "feature"
        def target = "master"
        def repositoryUrl = "https://stash.hk.hsbc/scm/rdp/workflowLibs.git"
        def credentialsId = UUID.randomUUID().toString()

        git.mergeBranch(source, target, repositoryUrl, credentialsId)

        assertThat(shellCommands[4], startsWith("git push origin ${target}"))
    }

    @Test
    void shouldCallGitCommandCorrectly() {

        def source = "feature"
        def target = "master"
        def repositoryUrl = "https://stash.hk.hsbc/scm/rdp/workflowLibs.git"
        def credentialsId = UUID.randomUUID().toString()

        def args = null

        git.metaClass.git = { Map m -> args = m}

        git.mergeBranch(source, target, repositoryUrl, credentialsId)

        assertThat(args.branch as String, equalTo(target))
        assertThat(args.url as String, equalTo(repositoryUrl))
        assertThat(args.credentialsId as String,  equalTo(credentialsId))
    }
}