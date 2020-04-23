package githubsearch;

import java.util.Optional;

public class SourceLocation {
    private final String pkg;
    private final String url;
    private final int col;
    private final int row;
    SourceLocation(String pkg, String url, int col, int row) {
        this.pkg  = pkg;
        this.url = url;
        this.col = col;
        this.row = row;
    }

    Optional<String> getPackage() {
        return Optional.of(pkg);
    }
    String getURL() {
        return url;
    }
    int getCol() {
        return col;
    }
    int getRow() {
        return row;
    }

    @Override
    public int hashCode() {
        return (pkg + url + col +":" + row).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof SourceLocation) {
            SourceLocation loc = (SourceLocation)o;
            return loc.pkg.equals(pkg) && loc.url.equals(url) && loc.col == col && loc.row == row;
        }
        return false;
    }
}
