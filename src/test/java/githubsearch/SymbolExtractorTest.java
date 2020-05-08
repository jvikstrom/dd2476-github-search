package githubsearch;

import com.github.javaparser.ParseException;
import org.junit.Assert;

public class SymbolExtractorTest {
    public SymbolExtractorTest() {

    }
    @org.junit.jupiter.api.Test
    void extractClassName() throws ParseException {
        SymbolPackage p = SymbolExtractor.parse("", "class ABC {void foo();}");
        Assert.assertEquals(1, p.getMethods().size());
        Assert.assertEquals("ABC", p.getMethods().get(0).className);
        Assert.assertEquals("foo", p.getMethods().get(0).name);
        Assert.assertEquals("void", p.getMethods().get(0).type);
        Assert.assertEquals(1, p.getClassDecls().size());
        Assert.assertEquals("ABC", p.getClassDecls().get(0).name);

    }
}
