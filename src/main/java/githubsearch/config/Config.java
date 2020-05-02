package githubsearch.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public final String repositorySaveRoot;
    public final String javaFileRoot;
    public final String javaIndexFile;
    public final String cloneCachePath;
    public Config(String path) throws IOException {
        Properties prop =new Properties();
        InputStream ip = getClass().getClassLoader().getResourceAsStream(path);
        prop.load(ip);
        repositorySaveRoot = prop.getProperty("repositorySaveRoot");
        javaFileRoot = prop.getProperty("javaFileRoot");
        javaIndexFile = prop.getProperty("javaIndexFile");
        cloneCachePath = prop.getProperty("cloneCachePath");
    }
}
