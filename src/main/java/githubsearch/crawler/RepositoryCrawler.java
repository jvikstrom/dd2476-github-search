package githubsearch.crawler;

import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Crawls a single hot repository and writes Java files to the listener.
 * Calls the listener with the path instead of URL, meaning to get the URL one needs to calculate the difference.
 */
public class RepositoryCrawler implements Crawler {
    private final URI root;
    private final CrawlSubscriber listener;
    // Root is supposed to be the root of the git repository
    public RepositoryCrawler(@NotNull URI root, @NotNull CrawlSubscriber listener) {
        this.root = root;
        this.listener = listener;
    }
    public void crawl() throws IOException {
        for(Path p : Files.walk(Paths.get(root)).filter(Files::isReadable).filter((path -> path.toString().toLowerCase().endsWith(".java"))).toArray(Path[]::new)) {
            List<String> bts = Files.readAllLines(p);
            listener.onSourceFile(p.toString(), String.join("\n", bts));
        }
    }
}
