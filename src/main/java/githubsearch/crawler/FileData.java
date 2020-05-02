package githubsearch.crawler;

public class FileData {
    public final String source;
    public final FileMetadata metadata;
    public FileData(String source, FileMetadata metadata) {
        this.source = source;
        this.metadata = metadata;
    }
}
