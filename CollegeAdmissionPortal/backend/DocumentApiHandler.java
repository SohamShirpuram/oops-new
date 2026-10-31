import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DocumentApiHandler implements HttpHandler {

    private final AdmissionSystem system;

    public DocumentApiHandler(AdmissionSystem system) {
        this.system = system;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.endsWith("/upload-document")) {
            handleUpload(exchange);
        } else if (path.endsWith("/document/view")) {
            handleViewOrDownload(exchange, false);
        } else if (path.endsWith("/document/download")) {
            handleViewOrDownload(exchange, true);
        } else {
            HttpHelper.sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"Endpoint not found\"}");
        }
    }

    private void handleUpload(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }

        Map<String, String> q = HttpHelper.parseQueryString(exchange.getRequestURI().getQuery());
        String appId = q.getOrDefault("appId", q.getOrDefault("email", ""));
        String docType = q.getOrDefault("docType", "");
        String fileName = q.getOrDefault("fileName", "");

        if (appId.isEmpty() && exchange.getRequestHeaders().containsKey("X-App-Id")) {
            appId = exchange.getRequestHeaders().getFirst("X-App-Id");
        }
        if (docType.isEmpty() && exchange.getRequestHeaders().containsKey("X-Doc-Type")) {
            docType = exchange.getRequestHeaders().getFirst("X-Doc-Type");
        }
        if (fileName.isEmpty() && exchange.getRequestHeaders().containsKey("X-File-Name")) {
            fileName = exchange.getRequestHeaders().getFirst("X-File-Name");
        }

        if (appId.isEmpty() || docType.isEmpty() || fileName.isEmpty()) {
            HttpHelper.sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Missing required parameters: appId, docType, fileName\"}");
            return;
        }

        byte[] fileBytes = HttpHelper.readAllBytes(exchange.getRequestBody());
        if (fileBytes.length == 0) {
            HttpHelper.sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Uploaded file is empty!\"}");
            return;
        }

        if (system.saveUploadedDocument(appId, docType, fileName, fileBytes)) {
            String json = String.format("{\"success\":true,\"message\":\"Document '%s' uploaded successfully!\",\"docType\":\"%s\",\"fileName\":\"%s\"}",
                    HttpHelper.escapeJson(Document.getTitleForType(docType)),
                    HttpHelper.escapeJson(docType), HttpHelper.escapeJson(fileName));
            HttpHelper.sendJsonResponse(exchange, 200, json);
        } else {
            HttpHelper.sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Failed to upload document. Verify application ID and allowed formats (PDF, JPG, PNG under 5MB).\"}");
        }
    }

    private void handleViewOrDownload(HttpExchange exchange, boolean isDownload) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }

        Map<String, String> q = HttpHelper.parseQueryString(exchange.getRequestURI().getQuery());
        String appId = q.getOrDefault("appId", q.getOrDefault("email", ""));
        String docType = q.getOrDefault("docType", "");

        File docFile = system.getDocumentFile(appId, docType);
        if (docFile != null && docFile.exists()) {
            String name = docFile.getName().toLowerCase();
            String contentType = "application/octet-stream";
            if (name.endsWith(".pdf")) contentType = "application/pdf";
            else if (name.endsWith(".png")) contentType = "image/png";
            else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) contentType = "image/jpeg";

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Content-Disposition", (isDownload ? "attachment" : "inline") + "; filename=\"" + docFile.getName() + "\"");

            byte[] fileBytes = new byte[(int) docFile.length()];
            try (FileInputStream fis = new FileInputStream(docFile)) {
                fis.read(fileBytes);
            }
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        } else {
            String notFound = "<h1>404 Not Found</h1><p>The requested document was not found or has not been uploaded yet.</p>";
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(404, notFound.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(notFound.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
