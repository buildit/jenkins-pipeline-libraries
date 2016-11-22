package lib;

import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.libs.LibraryRetriever;

import javax.annotation.Nonnull;

public class LocalLibraryRetriever extends LibraryRetriever {
    private final hudson.FilePath dir;

    public LocalLibraryRetriever(final FilePath dir) {
        this.dir = dir;
    }

    @Override
    public void retrieve(@Nonnull String name, @Nonnull String version, @Nonnull FilePath target, @Nonnull Run<?, ?> run, @Nonnull TaskListener listener) throws Exception {
        dir.copyRecursiveTo("src/**/*.groovy,vars/*.groovy,vars/*.txt,resources/", null, target);
    }
}
