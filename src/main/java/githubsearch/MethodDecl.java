package githubsearch;

import java.util.ArrayList;

public class MethodDecl {
    final String name;
    final String type;
    final SourceLocation loc;
    final ArrayList<String> parents;
    public MethodDecl(String name, String type, SourceLocation loc, ArrayList<String> parents) {
        this.name = name;
        this.type = type;
        this.loc = loc;
        this.parents = parents;
    }
    public String resolvedName() {
        // FIXME: Might want to have this return parent"."name as there may be cases where we won't match otherwise.
        // FIXME: Bit this needs to be investigated.
        return name;
    }
    @Override
    public String toString() {
        return String.format("%s:%s", name, type);
    }

    @Override
    public int hashCode() {
        // FIXME: Really need to fix these hash codes.
        return (name + ":" + type + loc.toString() + parents.toString()).hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof MethodDecl) {
            MethodDecl md = (MethodDecl)o;
            return name.equals(md.name) && loc.equals(md.loc) && type.equals(md.type) && parents.equals(md.parents);
        }
        return false;
    }
}
