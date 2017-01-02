package lib;

import hudson.FilePath;
import hudson.model.Result;
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
public class TemplateIntTest extends PipelineTest {

    @Test
    public void testTemplateFailFast() throws Exception {

        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "p");
        final FilePath dir = new FilePath(new File("."));
        GlobalLibraries.get().setLibraries(singletonList(new LibraryConfiguration("buildit", new LocalLibraryRetriever(dir))));
        p.setDefinition(new CpsFlowDefinition(
                "@Library('buildit@local')\n" +
                        "def ann = ''\n" +
                        "node() {\n" +
                        "   echo new template().transform('result is ${notDefined} and ${definedVal}', [definedVal: 123])\n" +
                        "   echo 'YES!'\n" +
                        "}", true));
        WorkflowRun b = p.scheduleBuild2(0).waitForStart();
        assertNotNull(b);
        r.assertBuildStatus(Result.FAILURE, r.waitForCompletion(b));
        r.assertLogContains("No such property: notDefined", b);
    }
}
