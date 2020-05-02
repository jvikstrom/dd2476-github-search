package githubsearch.crawler;

import githubsearch.util.Log;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class takes a list of urls to git repositories, clones and extracts and Java files in them in parallel.
 */
public class MultiGitCloner implements Crawler, CrawlSubscriber{
    /**
     * A worker that simply clones a repository until there are no more.
     */
    public static class MultiGitClonerWorker implements Crawler {
        final MultiGitCloner parent;
        final GitCloner cloner;
        final CloneCache cloneCache;
        final int waitTimeMs;
        private static String tag = "CloneWorker";
        MultiGitClonerWorker(GitCloner cloner, CloneCache cloneCache, MultiGitCloner parent, int waitTimeMs) {
            this.parent = parent;
            this.cloner = cloner;
            this.waitTimeMs = waitTimeMs;
            this.cloneCache = cloneCache;
        }
        public void crawl() throws IOException {
            Log.i(tag, "Starting crawling");
            while(true) {
                String url = parent.nextURL();
                if(url == null) {
                    System.out.println("MultiGitClonerWorker done working");
                    return;
                }
                if(!cloneCache.shouldClone(url)) {
                    Log.i(tag, "Skipping " + url + ", was already cloned");
                    continue;
                }
                Log.i(tag, "Clone repository at URL: " + url);
                try {
                    String path = cloner.gitClone(new URI(url));
                    parent.onClonedURL(path, url);
                }catch(URISyntaxException e) {
                    System.out.println("URI: " + url + " has an invalid URI format: " + e);
                } catch(GitCloner.CloneException e) {
                    System.out.println("ClonerException for URI: " + url + ", " + e);
                }
                try {
                    Thread.sleep(waitTimeMs);
                } catch(InterruptedException e) {
                    System.err.println("Was interrupted when sleeping. Exiting.");
                    return;
                }
            }
        }
    }

    /**
     * All URLs we should clone.
     */
    private final String[] urls;
    /**
     * How many workers we should run in parallel.
     */
    private final int nWorkers;
    /**
     * How long a worker should wait before cloning a new git repository.
     */
    private final int waitTimeMs;
    private final String repositoryStorageRoot;
    private List<MultiGitClonerWorker> workers = new ArrayList<>();
    private final AtomicInteger currentURLIdx = new AtomicInteger(0);
    private final JavaFileStorage storage;
    private final CloneCache cloneCache;

    MultiGitCloner(String[] urls, int nWorkers, int waitTimeMs, String repositoryStorageRoot, String javaFileRoot, String javaIndexFile, String cloneCachePath) {
        this.urls = urls;
        this.nWorkers = nWorkers;
        this.waitTimeMs = waitTimeMs;
        this.repositoryStorageRoot = repositoryStorageRoot;
        this.storage = new JavaFileStorage(javaFileRoot, javaIndexFile);
        this.cloneCache = new CloneCache(cloneCachePath);
    }

    public void crawl() throws IOException {
        GitCloner cloner = new GitCloner(repositoryStorageRoot);
        for(int i = 0; i < nWorkers; i++) {
            workers.add(new MultiGitClonerWorker(cloner, cloneCache, this, waitTimeMs));
        }
        List<Thread> threads = new ArrayList<>();
        for(MultiGitClonerWorker worker : workers) {
            Thread t = new Thread(() -> {
                try {
                    worker.crawl();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();
            threads.add(t);
        }
        Log.d("MultiClone", "Waiting for workers to finish");
        for(Thread t : threads) {
            try {
                t.join(); // Wait for this to finish.
            } catch(InterruptedException e) {
                System.err.println("Was interrupted while waiting for thread to finish.");
                return;
            }
        }
        Log.i("MultiClone", "Done cloning repositories");
    }

    @Override
    public void onSourceFile(String url, String sourceCode) {
        // FIXME: Write this into the Java file storage.
        try {
            storage.writeFile(url, sourceCode);
        } catch (IOException e) {
            System.out.println("IOException when writing for url: " + url);
            e.printStackTrace();
        }
    }

    void onClonedURL(String path, String url) {
        // Now crawl this folder.
        try {
            LocalFolderCrawler crawler = new LocalFolderCrawler(Paths.get(path), new URL(url), this);
            crawler.crawl();
            cloneCache.onClone(url);
            FileUtils.deleteDirectory(new File(path));
        } catch(MalformedURLException e) {
            System.err.println("Error creating URL, malformed: " + e);
        } catch(IOException e) {
            System.err.println("There was an IOException when crawling folder: "+path+", exception: " + e);
        }
    }

    /**
     * Thread safe. If it returns null there are no more urls.
     * @return
     */
    String nextURL() {
        int idx = currentURLIdx.getAndAdd(1);
        if(idx < urls.length) {
            return urls[idx];
        }
        return null;
    }


}
