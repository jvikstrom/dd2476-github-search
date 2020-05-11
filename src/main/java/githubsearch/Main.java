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
        int nread = 0;
        while (files.hasNext()) {
            nread++;
            if(nread % 100 == 0)
                System.out.println("Read ranker: " + nread);
            FileData file = files.next();
            try {
                SymbolPackage symbols = SymbolExtractor.parse(file.metadata.url, file.source);
                ranker.processPackage(symbols);
                xref.resolveSymbols(symbols);
            } catch (ParseException e) {
                System.err.println(e.getMessage());
                //e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Done processing dag");
        ranker.rank();

        files = storage.files();
        nread = 0;
        while (files.hasNext()) {
            nread++;
            if(nread % 100 == 0)
                System.out.println("Read elastic: " + nread);
            FileData file = files.next();
            try {
                SymbolPackage symbols = SymbolExtractor.parse(file.metadata.url, file.source);
                String pkg = "";
                if (symbols.getPackageName().isPresent()) {
                    pkg = symbols.getPackageName().get();
                }

                for (ClassDecl classDecl : symbols.getClassDecls()) {
                    indexer.indexClass(classDecl.name, classDecl.loc.getURL(), classDecl.loc.getRow(), ranker.getScore(pkg));
                }

                for (MethodDecl method : symbols.getMethods()) {
                    indexer.indexMethod(method.name, method.type, file.metadata.url, method.loc.getRow(), ranker.getScore(pkg));

                    Set<CallExpr> callExprs = xref.getCallExprs(method);
                    if (callExprs != null) {
                        for (CallExpr callExpr : xref.getCallExprs(method)) {
                            indexer.indexMethodCall(method.name, method.type, callExpr.getSourceLocation().getURL(),
                                    callExpr.getSourceLocation().getRow(), ranker.getScore(pkg));
                        }
                    }
                }
                //Log.d("Indexer", symbols.toString());
            } catch (ParseException e) {
                System.err.println(e.getMessage());
            }
        }
        long finalEndTime = System.currentTimeMillis();
        long timeSpentElastic = (finalEndTime - endTime) - indexer.totalTime;
        System.out.println("Time for first loop: " + (endTime - startTime) + ", time for second loop: " + (finalEndTime - endTime));
        long timeSpentElse = finalEndTime - endTime - timeSpentElastic;
        System.out.println("Time spent blocking on elastic: " + timeSpentElastic + ", time spent remaining: " + timeSpentElse);
    }

}
