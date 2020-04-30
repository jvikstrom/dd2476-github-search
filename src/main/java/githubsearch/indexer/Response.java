package githubsearch.indexer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Response {
    private List<Entry> entries = new ArrayList<>();

    private class Entry {
        String URL;
        String line;

        public Entry(String URL, String line) {
            this.URL = URL;
            this.line = line;
        }
    }

    public Response(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);
        List<Object> hits = jsonObject.getJSONObject("hits").getJSONArray("hits").toList();
        for (Object hit : hits) {
            HashMap<String, Object> hitMap = (HashMap) hit;
            HashMap<String, Object> sourceMap = (HashMap) hitMap.get("_source");
            String URL = (String) sourceMap.get("URL");
            String line = (String) sourceMap.get("LINE");
            entries.add(new Entry(URL, line));
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("Matches found: ");
        string.append(entries.size());
        string.append("\n");
        for (Entry entry : entries) {
            string.append(entry.URL);
        }
        return string.toString();
    }
}
