package githubsearch;

import com.github.javaparser.ParseException;
import githubsearch.crawler.CrawlSubscriber;


public class CrawlSymbolExtractor implements CrawlSubscriber {
    @Override
    public void onSourceFile(String url, String sourceCode) {
        // FIXME: Edvin, index this pkg.
        try {
            SymbolPackage pkg = SymbolExtractor.parse(url, sourceCode);
            System.out.println(pkg);
        } catch(ParseException e) {
            System.err.println("There was a problem parsing for url: " + url + ", " + e.toString());
        }
    }
}
