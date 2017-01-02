package lib;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.RestartableJenkinsRule;

import java.util.logging.Level;

@Ignore("WIP: example of full integration test")
abstract public class PipelineTest {

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

}
