package githubsearch;

import java.util.Optional;

public class CallExpr {
    final private String name;
    final private String type;
    final private SourceLocation loc;
    CallExpr(String name, String type, SourceLocation loc) {
        this.name = name;
        this.type = type;
        this.loc = loc;
    }
    String getName() {
        return name;
    }
    Optional<String> getType() {
        if(type == null) {
            return Optional.empty();
        }
        return Optional.of(type);
    }
    SourceLocation getSourceLocation() {
        return loc;
    }

    @Override
    public String toString() {
        String tp = type;
        if(tp == null) {
            tp = "";
        }
        return tp+";"+name+";"+loc.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof CallExpr) {
            CallExpr ce = (CallExpr)o;
            return ce.name.equals(name) && ce.getType().equals(getType()) && ce.loc.equals(loc);
        }
        return super.equals(o);
    }

}
