package githubsearch.crawler;

import githubsearch.util.Log;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;

/**
 * Keeps track if a certain repository has been cloned and if it should be cloned.
 */
public class CloneCache {
    final String cloneCachePath;
    HashSet<String> clonedURLs = new HashSet<>();
    CloneCache(String cloneCachePath) {
        this.cloneCachePath = cloneCachePath;
        try {
            // Load old cloned urls.
            List<String> urls = Files.readAllLines(new File(cloneCachePath).toPath());
            clonedURLs.addAll(urls);
        } catch(IOException e) {
            Log.d("CloneCache", "Got IOException trying to read: " + cloneCachePath + ", " + e);
        }
    }

    // onClone should be called every time a git repository has been cloned.
    synchronized void onClone(String url) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(cloneCachePath, true));
        writer.write(url);
        writer.newLine();
        writer.close();
        clonedURLs.add(url);
    }

    synchronized boolean shouldClone(String url) {
        return !clonedURLs.contains(url);
    }
}
