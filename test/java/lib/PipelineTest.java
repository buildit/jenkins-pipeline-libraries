package lib;

import hudson.FilePath;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries;
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.RestartableJenkinsRule;

import java.io.File;
import java.util.logging.Level;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNotNull;

@Ignore("WIP: example of full integration test")
public class PipelineTest {

    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();

    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Rule
    public RestartableJenkinsRule story = new RestartableJenkinsRule();
    @Rule
    public LoggerRule logs = new LoggerRule().record(PipelineTest.class, Level.ALL);

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void runSlack() throws Exception {

        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
        final FilePath dir = new FilePath(new File("."));
        GlobalLibraries.get().setLibraries(singletonList(new LibraryConfiguration("buildit", new LocalLibraryRetriever(dir))));
        p.setDefinition(new CpsFlowDefinition(
                "@Library('buildit@local')\n" +
                        "def ann = ''\n" +
                        "node() {\n" +
                        "   echo new template().transform('AAA BBB', [:])\n" +
                        "   echo 'YES!'\n" +
                        "}", true));
        WorkflowRun b = p.scheduleBuild2(0).waitForStart();
        assertNotNull(b);
        r.assertBuildStatusSuccess(r.waitForCompletion(b));
        r.assertLogContains("YES!", b);
    }
}
