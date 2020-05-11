package githubsearch;

import java.util.Iterator;
import java.util.Set;

public interface ImportDAGStorage {
    class Entry {
        String pkg;
        double size;
        public Entry(String pkg, double size) {
            this.pkg = pkg;
            this.size = size;
        }
    }
    // "importer" imports pkg
    void addImporter(String pkg, String importer);

    // Returns null if there is no entry.
    Entry getEntry(String pkg);

    int numEntries();

    Set<String> getImportsAt(String doc);
    Iterator<String> keyIt();
}
