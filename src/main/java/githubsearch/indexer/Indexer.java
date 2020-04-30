package githubsearch.indexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Indexer {
    private static final String address = "localhost";
    private static final String port = "9200";

    public Response search(Query query) {
        try {
            URL url = new URL("http://" + address + ":" + port + "/github/_search");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = query.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                return new Response(response.toString());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private void index(String jsonPayload, String id) throws IllegalArgumentException {
        if (id.contains("/")) {
            throw new IllegalArgumentException("id can not contain forward slash '/': " + id);
        }
        try {
            URL url = new URL("http://" + address + ":" + port + "/github/_doc/" + id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void indexClass(String name, String URL, int line) {
        String jsonPayload = "{" +
                "\"TYPE\":\"CLASS\"," +
                "\"NAME\":\"" + name + "\"," +
                "\"LINE\":\"" + line + "\"," +
                "\"URL\":\"" + URL + "\"" +
                "}";
        index(jsonPayload, "placeholder-id-0");
    }

    public void indexMethod(String name, String returnType, String URL, int line) {
        String jsonPayload = "{" +
                "\"TYPE\":\"METHOD\"," +
                "\"NAME\":\"" + name + "\"," +
                "\"LINE\":\"" + line + "\"," +
                "\"URL\":\"" + URL + "\"," +
                "\"RETURN_TYPE\":\"" + returnType + "\"" +
                "}";
        index(jsonPayload, "placeholder-id-1");
    }
}