package githubsearch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
}
