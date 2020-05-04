package githubsearch;

public interface ImportDAGStorage {
    class Entry {
        String pkg;
        int size;
    }
    // "importer" imports pkg
    void addImporter(String pkg, String importer);

    // Returns null if there is no entry.
    Entry getEntry(String pkg);
}
