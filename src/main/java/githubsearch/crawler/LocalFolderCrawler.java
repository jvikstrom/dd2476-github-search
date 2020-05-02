package githubsearch.crawler;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Crawls a single hot repository and writes Java files to the listener.
 * Calls the listener with the path instead of URL, meaning to get the URL one needs to calculate the difference.
 */
public class LocalFolderCrawler implements Crawler {
    private final Path root;
    private final URL repositoryURI;
    private final CrawlSubscriber listener;
    // Root is supposed to be the root of the git repository
    public LocalFolderCrawler(@NotNull Path root, @NotNull URL repositoryURI, @NotNull CrawlSubscriber listener) {
        this.root = root;
        this.repositoryURI = repositoryURI;
        this.listener = listener;
    }
    public void crawl() throws IOException {
        for(Path p : Files.walk(root).filter(Files::isReadable).filter((path -> path.toString().toLowerCase().endsWith(".java"))).toArray(Path[]::new)) {
            List<String> bts = Files.readAllLines(p);
            String psub = p.toString().substring(root.toString().length());
            String repoURIS = repositoryURI.toString();
            String url = repoURIS + "/" + psub;
            if(repoURIS.contains("github")) {
                // Github links are special.
                url = repoURIS.substring(0, repoURIS.length()-4) + "/tree/master" + psub;
            }

            listener.onSourceFile(url, String.join("\n", bts));
        }
    }
}
