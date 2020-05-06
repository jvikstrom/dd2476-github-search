package githubsearch;

/**
 * Ranks packages by the number of packages importing it.
 */
public class ImportDAGGraphRanker {
    private final ImportDAGStorage storage;
    public ImportDAGGraphRanker(ImportDAGStorage storage) {
        this.storage = storage;
    }

    void processPackage(SymbolPackage p) {
        if(!p.getPackageName().isPresent()) {
            return;
        }
        String pkg = p.getPackageName().get();
        for(String imp : p.getImports()) {
            storage.addImporter(imp, pkg);
        }
    }
    double getScore(String pkg) {
        ImportDAGStorage.Entry e = storage.getEntry(pkg);
        if(e == null) {
            return 0;
        }
        return e.size;
    }
}
