package githubsearch;

import java.util.*;

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
    // Implement PageRank

    double getScore(String pkg) {
        if(pageranks == null) {
            throw new RuntimeException("Need to call rank before calling getScore");
        }
        ImportDAGStorage.Entry e = storage.getEntry(pkg);
        if(e == null) {
            return 0;
        }
        return e.size;
    }

    public void rank() {
        iterativemontecarlo();
    }

    private static class Pair implements Comparable<Pair>{
        final int doc;
        final double score;
        Pair(int doc, double score) {
            this.doc = doc;
            this.score = score;
        }

        @Override
        public int compareTo(Pair o) {
            return Double.compare(o.score, score);
        }
    }
    HashMap<String,Double> pageranks;
    private void iterativemontecarlo() {
        final int N = storage.numEntries();
        HashMap<String,Integer> doc2id = new HashMap<>();
        String[] id2doc = new String[N];
        Iterator<String> keys = storage.keyIt();
        int ii = 0;
        while(keys.hasNext()) {
            String key = keys.next();
            doc2id.put(key, ii);
            id2doc[ii] = key;
            ii++;
        }
        final double BORED = 0.15;
        long[] visits = new long[N];
        long totalVisits = 0;
        final int m = 2;
        Random r = new Random();
        HashMap<Integer,Double> oldTop30 = new HashMap<>();
        do {
            for(int i = 0; i < N; i++) {
                for(int j = 0; j < m; j++) {
                    int start = i;
                    visits[start]++;
                    totalVisits++;
                    while(r.nextDouble() > BORED) {
                        String[] imports = storage.getImportsAt(id2doc[start]).toArray(new String[0]);
                        // There might be imports that we don't have an entry for. Just take
                        int rand = r.nextInt(imports.length);
                        int idx = rand;
                        while(!doc2id.containsKey(imports[idx])) {
                            idx = (idx + 1) % imports.length;
                            if(idx == rand) {
                                // No more steps can be made. Just go to next loop.
                                break;
                            }
                        }
                        if(!doc2id.containsKey(imports[idx])) {
                            break;
                        }
                        start = doc2id.get(imports[idx]);
                        visits[start]++;
                        totalVisits++;
                    }
                }
            }
            HashMap<Integer,Double> pageranks = new HashMap<>();
            ArrayList<Pair> pageranksArray = new ArrayList<>();
            for(int i = 0; i < N; i++) {
                double score = visits[i] / (double)totalVisits;
                pageranks.put(i, score);
                pageranksArray.add(new Pair(i, score));
            }
            pageranksArray.sort(Pair::compareTo);
            double diff = 0;
            for(int i = 0; i < 100; i++) {
                Double oldScoreRef = oldTop30.get(pageranksArray.get(i).doc);
                double oldScore = 0;
                if(oldScoreRef != null) {
                    oldScore = oldScoreRef;
                }
                diff += Math.abs(pageranksArray.get(i).score - oldScore);
            }

            oldTop30 = pageranks;
            if(diff < 1e-4) {
                break;
            }
            System.out.println("DIFF: " + diff);
        }while(true);
        ArrayList<Pair> pageranksArray = new ArrayList<>();
        for(Integer doc : oldTop30.keySet()) {
            pageranksArray.add(new Pair(doc, oldTop30.get(doc)));
        }
        pageranksArray.sort(Pair::compareTo);
        System.out.println("\nranks:\n");
        for(int i = 0; i < 50; i++) {
            System.out.println(id2doc[pageranksArray.get(i).doc] + " has score: " + pageranksArray.get(i).score);
        }
        this.pageranks = new HashMap<>();
        for(Integer doc : oldTop30.keySet()) {
            pageranks.put(id2doc[doc], oldTop30.get(doc));
        }
    }
}
