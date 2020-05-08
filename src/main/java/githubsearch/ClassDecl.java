package githubsearch;

import java.util.ArrayList;

public class ClassDecl {
    final String name;
    final SourceLocation loc;
    public ClassDecl(String name, SourceLocation loc) {
        this.name = name;
        this.loc = loc;
    }
    public String resolvedName() {
        // FIXME: Might want to have this return parent"."name as there may be cases where we won't match otherwise.
        // FIXME: Bit this needs to be investigated.
        return name;
    }
    @Override
    public String toString() {
        return String.format("%s", name);
    }

    @Override
    public int hashCode() {
        // FIXME: Really need to fix these hash codes.
        return (name + ":" + loc.toString()).hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if(o instanceof MethodDecl) {
            MethodDecl md = (MethodDecl)o;
            return name.equals(md.name) && loc.equals(md.loc);
        }
        return false;
    }

}
