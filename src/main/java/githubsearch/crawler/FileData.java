package githubsearch.crawler;

public class FileData {
    final String source;
    final FileMetadata metadata;
    public FileData(String source, FileMetadata metadata) {
        this.source = source;
        this.metadata = metadata;
    }
}
