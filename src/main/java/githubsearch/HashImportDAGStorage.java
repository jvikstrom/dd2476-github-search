package githubsearch;

import java.util.*;

class HashImportDAGStorage implements ImportDAGStorage {
    private HashMap<String, Set<String>> pkgs = new HashMap<>();

    @Override
    public void addImporter(String pkg, String importer) {
        if(!pkgs.containsKey(pkg)) {
            pkgs.put(pkg, new HashSet<>());
        }
        pkgs.get(pkg).add(importer);
    }

    @Override
    public Entry getEntry(String pkg) {
        if(!pkgs.containsKey(pkg)) {
            return new Entry(pkg, 0);
        }
        double score = pkgs.get(pkg).size() / (double)pkgs.size();
        return new Entry(pkg, score);
    }

    @Override
    public int numEntries() {
        return pkgs.size();
    }

    @Override
    public Set<String> getImportsAt(String doc) {
        return pkgs.get(doc);
    }

    public Iterator<String> keyIt() {
        return pkgs.keySet().iterator();
    }
}
