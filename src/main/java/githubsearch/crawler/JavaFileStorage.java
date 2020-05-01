package githubsearch.crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * Class for writing raw java files to storage.
 */
public class JavaFileStorage {
    private final String root;
    private final String indexFilePath;
    JavaFileStorage(String root, String indexFilePath) {
        this.root = root;
        this.indexFilePath = indexFilePath;
    }

    public synchronized void writeFile(String url, String sourceCode) throws IOException {
        // Writes the file and puts metadata to an index.
        String name = UUID.randomUUID().toString() + ".java";
        BufferedWriter writer = new BufferedWriter(new FileWriter(indexFilePath, true));
        writer.write(name+":"+url);
        writer.newLine();
        writer.close();
        // Now write the source code.
        writer = new BufferedWriter(new FileWriter(root + "/" + name));
        writer.write(sourceCode);
        writer.close();
    }
}
