package githubsearch.crawler;

import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
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

    Iterator<FileData> files() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(indexFilePath));
        return new Iterator<FileData>() {
            String nextLine = reader.readLine();
            @Override
            public boolean hasNext() {
                return nextLine != null;
            }

            @Override
            public FileData next() {
                if(!hasNext()) {
                    return null;
                }
                String name = nextLine.split(":")[0];
                String url = nextLine.substring(name.length());
                try {
                    List<String> lines = Files.readAllLines(new File(root + "/" + name).toPath());
                    String source = String.join("\n", lines);
                    return new FileData(source, new FileMetadata(name, url));
                } catch(IOException e) {
                    throw new RuntimeException("Could not read file: " + root + "/" + name + ", exception: " + e);
                }
            }
        };
    }
}
