import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHelper {

    public static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static Map<String, String> parseRequestBody(HttpExchange exchange) throws IOException {
        Map<String, String> params = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) body.append(line);

            for (String pair : body.toString().split("&")) {
                String[] kv = pair.split("=");
                if (kv.length >= 2) {
                    params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8.name()),
                               URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name()));
                } else if (kv.length == 1 && !kv[0].isEmpty()) {
                    params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8.name()), "");
                }
            }
        }
        return params;
    }

    public static Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;

        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length >= 2) {
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                           URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            } else if (kv.length == 1 && !kv[0].isEmpty()) {
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), "");
            }
        }
        return params;
    }

    public static byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    public static String formatApplicationJson(Application app, String phone) {
        StringBuilder prefsJson = new StringBuilder("[");
        List<String> prefs = app.getBranchPreferences();
        for (int i = 0; i < prefs.size(); i++) {
            prefsJson.append("\"").append(escapeJson(prefs.get(i))).append("\"");
            if (i < prefs.size() - 1) prefsJson.append(",");
        }
        prefsJson.append("]");

        StringBuilder docsJson = new StringBuilder("{");
        Map<String, String> docs = app.getUploadedDocuments();
        int docIndex = 0;
        for (Map.Entry<String, String> entry : docs.entrySet()) {
            if (docIndex > 0) docsJson.append(",");
            docsJson.append("\"").append(escapeJson(entry.getKey())).append("\":\"")
                    .append(escapeJson(entry.getValue())).append("\"");
            docIndex++;
        }
        docsJson.append("}");

        return String.format(
            "{\"found\":true,\"appId\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\"," +
            "\"ssc\":%.2f,\"hsc\":%.2f,\"cet\":%.2f,\"course\":\"%s\"," +
            "\"category\":\"%s\",\"status\":\"%s\",\"remarks\":\"%s\"," +
            "\"preferences\":%s,\"allocatedBranch\":\"%s\",\"allocationStatus\":\"%s\"," +
            "\"verificationStatus\":\"%s\",\"documents\":%s,\"allocationRemarks\":\"%s\"}",
            escapeJson(app.getApplicationId()), escapeJson(app.getStudentName()),
            escapeJson(app.getStudentEmail()), escapeJson(phone),
            app.getSscMarks(), app.getHscMarks(), app.getCetScore(),
            escapeJson(app.getCourseChoice()), escapeJson(app.getCategory()),
            escapeJson(app.getStatus()), escapeJson(app.getRemarks()),
            prefsJson.toString(), escapeJson(app.getAllocatedBranch()),
            escapeJson(app.getAllocationStatus()), escapeJson(app.getVerificationStatus()),
            docsJson.toString(), escapeJson(app.getAllocationRemarks())
        );
    }
}
