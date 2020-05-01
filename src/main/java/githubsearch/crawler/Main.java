package githubsearch.crawler;

import java.io.IOException;
import java.net.URI;

public class Main implements CrawlSubscriber{
    public static void main(String[] args) throws IOException {
        RepositoryCrawler crawler = new RepositoryCrawler(URI.create("file:/home/jovi/school/zookeeper"), new Main());
        System.out.println("Start crawling!");
        crawler.crawl();
    }

    @Override
    public void onSourceFile(String url, String sourceCode) {
        System.out.println("Source file at: " + url + " contains: " + sourceCode);
    }
}
