package githubsearch;

import com.github.javaparser.ParseException;
import githubsearch.config.Config;
import githubsearch.crawler.FileData;
import githubsearch.crawler.JavaFileStorage;
import githubsearch.crawler.LocalFolderCrawler;
import githubsearch.indexer.Indexer;
import githubsearch.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Config conf = new Config("config.properties");
        JavaFileStorage storage = new JavaFileStorage(conf.javaFileRoot, conf.javaIndexFile);
        Iterator<FileData> files = storage.files();
        Indexer indexer = new Indexer();
        Log.i("Indexer", "Starting indexing");


        while (files.hasNext()) {
            FileData file = files.next();
            try {
                SymbolPackage symbols = SymbolExtractor.parse(file.metadata.url, file.source);
                for (MethodDecl method : symbols.getMethods()) {
                    indexer.indexMethod(method.name, method.type, file.metadata.url, method.loc.getRow());
                }
                Log.d("Indexer", symbols.toString());
            } catch (ParseException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
