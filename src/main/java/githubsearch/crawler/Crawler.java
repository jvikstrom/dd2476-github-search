package githubsearch.crawler;

import java.io.IOException;

public interface Crawler {
    /**
     * Starts the crawling process.
     *
     * May block.
     * @throws IOException
     */
    void crawl() throws IOException;
}
