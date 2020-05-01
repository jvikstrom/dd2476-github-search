package githubsearch.crawler;

import java.io.InputStream;

public interface CrawlSubscriber {
    void onSourceFile(String url, String sourceCode);
}
