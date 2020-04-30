package githubsearch.indexer;

public class Query {
    public enum QueryType {CLASS, METHOD}

    private QueryType queryType;
    private String name;
    private String returnType = null;

    public Query(QueryType queryType, String name) {
        this.queryType = queryType;
        this.name = name;
    }

    public Query(QueryType queryType, String name, String returnType) throws IllegalArgumentException {
        if (queryType.equals(QueryType.CLASS)) {
            throw new IllegalArgumentException("QueryType CLASS does not have a returnType");
        }
        this.queryType = queryType;
        this.name = name;
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        return "{" +
                "\"query\": {" +
                "\"bool\": {" +
                "\"must\": [" +
                "{" +
                "\"match\": {" +
                "\"TYPE\": \"" + queryType.name() + "\"" +
                "}" +
                "}," +
                "{" +
                "\"match\": {" +
                "\"NAME\": \"" + name + "\"" +
                "}" +
                "}" +
                (returnType == null ? "" :
                        ",{" +
                                "\"match\": {" +
                                "\"RETURN_TYPE\": \"int\"" +
                                "}" +
                                "}") +
                "]" +
                "}" +
                "}" +
                "}";
    }
}
