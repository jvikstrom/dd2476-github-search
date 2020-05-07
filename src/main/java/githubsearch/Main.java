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
import java.util.Set;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Config conf = new Config("config.properties");
        JavaFileStorage storage = new JavaFileStorage(conf.javaFileRoot, conf.javaIndexFile);
        Iterator<FileData> files = storage.files();
        Indexer indexer = new Indexer();
        indexer.createIndex();
        Log.i("Indexer", "Starting indexing");

        long startTime = System.currentTimeMillis();
        ImportDAGStorage dagStorage = new HashImportDAGStorage();
        ImportDAGGraphRanker ranker = new ImportDAGGraphRanker(dagStorage);
        HashXRefIndexStorage xrefStorage = new HashXRefIndexStorage();
        XRefIndex xref = new XRefIndex(xrefStorage);
        while(files.hasNext()) {
            FileData file = files.next();
            try {
                SymbolPackage symbols = SymbolExtractor.parse(file.metadata.url, file.source);
                ranker.processPackage(symbols);
                xref.resolveSymbols(symbols);
            } catch(ParseException e) {
                System.err.println(e.getMessage());
                //e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Done processing dag");
        files = storage.files();
        while (files.hasNext()) {
            FileData file = files.next();
            try {
                SymbolPackage symbols = SymbolExtractor.parse(file.metadata.url, file.source);
                String pkg = "";
                if(symbols.getPackageName().isPresent()) {
                    pkg = symbols.getPackageName().get();
                }
                for (MethodDecl method : symbols.getMethods()) {
                    Set<CallExpr> callees = xref.getCallExprs(method);
                    indexer.indexMethod(method.name, method.type, file.metadata.url, method.loc.getRow(), ranker.getScore(pkg));
                }
                //Log.d("Indexer", symbols.toString());
            } catch (ParseException e) {
                System.err.println(e.getMessage());
            }
        }
        long finalEndTime = System.currentTimeMillis();
        System.out.println("Time for first loop: " + (endTime - startTime) + ", time for second loop: " + (finalEndTime - endTime));
    }

}
