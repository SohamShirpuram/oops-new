import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class StaticFileHandler implements HttpHandler {

    private final String frontendPath;

    public StaticFileHandler(String frontendPath) {
        this.frontendPath = frontendPath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        if (requestPath == null || requestPath.isEmpty() || requestPath.equals("/")) {
            requestPath = "/index.html";
        }
        if (requestPath.length() > 1 && requestPath.endsWith("/")) {
            requestPath = requestPath.substring(0, requestPath.length() - 1);
        }

        File baseDir = new File(frontendPath);
        String relativePath = requestPath.startsWith("/") ? requestPath.substring(1) : requestPath;

        if (relativePath.equalsIgnoreCase("favicon.ico")) {
            File fav = new File(baseDir, relativePath);
            if (!fav.exists()) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }
        }

        File file = new File(baseDir, relativePath);
        if (!file.exists() || file.isDirectory()) {
            File htmlFile = new File(baseDir, relativePath + ".html");
            if (htmlFile.exists() && htmlFile.isFile()) {
                file = htmlFile;
            }
        }

        if (file.exists() && file.isDirectory()) {
            File indexFile = new File(file, "index.html");
            if (indexFile.exists() && indexFile.isFile()) {
                file = indexFile;
            }
        }

        if (file.exists() && file.isFile()) {
            String contentType = getContentType(file.getName().toLowerCase());
            byte[] fileBytes = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.read(fileBytes);
            }
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, fileBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        } else {
            String notFound = "<h1>404 Not Found</h1><p>The requested page was not found.</p>";
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(404, notFound.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(notFound.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private String getContentType(String name) {
        if (name.endsWith(".html")) return "text/html; charset=UTF-8";
        if (name.endsWith(".css")) return "text/css; charset=UTF-8";
        if (name.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".pdf")) return "application/pdf";
        if (name.endsWith(".svg")) return "image/svg+xml";
        if (name.endsWith(".ico")) return "image/x-icon";
        if (name.endsWith(".json")) return "application/json; charset=UTF-8";
        return "text/plain";
    }
}
