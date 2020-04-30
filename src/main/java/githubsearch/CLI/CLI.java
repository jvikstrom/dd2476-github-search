package githubsearch.CLI;

import githubsearch.indexer.Indexer;
import githubsearch.indexer.Query;
import githubsearch.indexer.Response;

import java.util.Scanner;

public class CLI {
    Indexer indexer = new Indexer();

    public static void main(String[] args) {
        new CLI().loop();
    }

    public void loop() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String line = scanner.nextLine().toLowerCase();

            if (line.equals("exit")) {
                return;
            }

            Query query = parseQuery(line);
            if (query != null) {
                Response response = indexer.search(query);
                System.out.println(response);
            }
        }
    }

    public Query parseQuery(String queryLine) {
        String[] tokens = queryLine.split(" ");
        Query.QueryType queryType = null;
        String name = null;
        String returnType = null;

        if (tokens.length != 2 & tokens.length != 3) {
            System.out.println("Query must contain 2 or 3 tokens.");
            return null;
        }

        if (tokens[0].equals("method")) {
            queryType = Query.QueryType.METHOD;
            name = tokens[1];
            if (tokens.length == 3) {
                returnType = tokens[2];
            }
        } else if (tokens[0].equals("class")) {
            queryType = Query.QueryType.CLASS;
            name = tokens[1];
            if (tokens.length != 2) {
                System.out.println("Class query must contain 2 tokens.");
                return null;
            }
        } else {
            System.out.println("First query token must be either \"class\" or \"method\".");
            return null;
        }

        return returnType == null ? new Query(queryType, name) : new Query(queryType, name, returnType);
    }
}
