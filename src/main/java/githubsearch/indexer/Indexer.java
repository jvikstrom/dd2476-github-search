package githubsearch.indexer;

import githubsearch.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Indexer {
    private static final String address = "localhost";
    private static final String port = "9200";

    public Response search(Query query) {
        try {
            URL url = new URL("http://" + address + ":" + port + "/github/_search?size=1000");
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

    public void createIndex() throws IllegalArgumentException, IOException {
        String payload = "{\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"dagrank\": {\n" +
                "        \"type\": \"rank_feature\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        URL url = new URL("http://" + address + ":" + port + "/github");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        Log.i("Indexer", "Response from creating index: " + con.getResponseCode());
        if (responseCode != 200) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.e("Indexer", "RESPONSE: " + response.toString());
            }
        }
    }

    private void index(String jsonPayload, String index, String id) throws IllegalArgumentException {
        if (id.contains("/")) {
            throw new IllegalArgumentException("id can not contain forward slash '/': " + id);
        }
        try {
            URL url = new URL("http://" + address + ":" + port + "/" + index + "/_doc/" + id);
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

    public void indexClass(String name, String URL, int line, double dagrank) {
        String jsonPayload = "{" +
                "\"NAME\":\"" + name + "\"," +
                "\"LINE\":\"" + line + "\"," +
                "\"URL\":\"" + URL + "\"," +
                "\"dagrank\":" + dagrank +
                "}";
        index(jsonPayload, "class",
                ("url-" + URL + "-name-" + name + "-line-" + line)
                        .replaceAll("[/<>\\\\?#: ]", "-"));
    }

    public void indexMethod(String name, String returnType, String URL, int line, double dagrank) {
        String jsonPayload = "{" +
                "\"NAME\":\"" + name + "\"," +
                "\"LINE\":\"" + line + "\"," +
                "\"URL\":\"" + URL + "\"," +
                "\"RETURN_TYPE\":\"" + returnType + "\"," +
                "\"dagrank\":" + dagrank +
                "}";
        index(jsonPayload, "method",
                ("url-" + URL + "-name-" + name + "-return-type-" + returnType + "-line-" + line)
                        .replaceAll("[/<>\\\\?#: ]", "-"));
    }

    public void indexMethodCall(String name, String returnType, String URL, int line, double dagrank) {
        String jsonPayload = "{" +
                "\"NAME\":\"" + name + "\"," +
                "\"LINE\":\"" + line + "\"," +
                "\"URL\":\"" + URL + "\"," +
                "\"RETURN_TYPE\":\"" + returnType + "\"," +
                "\"dagrank\":" + dagrank +
                "}";
        index(jsonPayload, "method-call",
                ("url-" + URL + "-name-" + name + "-return-type-" + returnType + "-line-" + line)
                        .replaceAll("[/<>\\\\?#: ]", "-"));
    }
}
