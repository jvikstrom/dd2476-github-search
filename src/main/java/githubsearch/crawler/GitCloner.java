package githubsearch.crawler;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for cloning git repositories to a specified root.
 */
public class GitCloner {
    public static class CloneException extends Exception {
        Exception e;
        CloneException(Exception wrapped) {
            this.e = wrapped;
        }
    }
    private final String storageRoot;

    private AtomicInteger idx = new AtomicInteger(0);
    /**
     *
     * @param repositoryStorageRoot The root that it should start saving repositories at.
     */
    public GitCloner(String repositoryStorageRoot) {
        this.storageRoot = repositoryStorageRoot;
    }

    /**
     * Clones a git repository at the repositoryURI. This function is thread safe and can be called from multiple threads.
     * @return the path to the repository
     */
    String gitClone(URI repositoryURI) throws CloneException {
        // Just runs the git clone command with a few parameters set.
        // git -C ~/Documents/ clone --depth 1 <repo>
        String repoName = "repo-" + idx.getAndAdd(1);
        try {
            Process p = Runtime.getRuntime().exec("git -C " + storageRoot + " clone --depth 1 " + repositoryURI.toString() + " " + repoName);
            int exitVal = p.waitFor();
            if(exitVal != 0) {
                throw new Exception("Exit value from clone was not 0, was: " + exitVal);
            }
        } catch(Exception e) {
            throw new CloneException(e);
        }
        return storageRoot + "/" + repoName;
    }
}
