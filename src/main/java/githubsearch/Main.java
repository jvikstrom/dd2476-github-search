package githubsearch;
import githubsearch.crawler.LocalFolderCrawler;

import java.io.IOException;
import java.lang.String;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws IOException {
        LocalFolderCrawler crawler = new LocalFolderCrawler(URI.create("file:/home/jovi/school/zookeeper"), new CrawlSymbolExtractor());
        System.out.println("Start crawling!");
        crawler.crawl();

//        SymbolPackage pack = SymbolExtractor.parse("urlURLurl","package main.com.pack;\nimport something.com.dom;\nclass X { int x() { return 1 + 1.0 - 5; } }");
    // Parse some code
//    System.out.println("Package: " + pack.toString());
    }

}
