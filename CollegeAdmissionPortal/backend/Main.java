import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final int PORT = 8080;
    private static AdmissionSystem system;
    private static String frontendDir;

    public static void main(String[] args) {
        try {
            system = new AdmissionSystem();
            frontendDir = findFrontend();

            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/", Main::handleRequest);
            server.start();

            System.out.println("Portal running at http://localhost:" + PORT + "/");
            Thread.currentThread().join();
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.startsWith("/api/")) {
            handleApi(exchange, path);
        } else {
            serveFile(exchange, path);
        }
    }

    private static void serveFile(HttpExchange exchange, String path) throws IOException {
        if (path == null || path.isEmpty() || path.equals("/")) path = "/index.html";
        if (path.length() > 1 && path.endsWith("/")) path = path.substring(0, path.length() - 1);
        if (path.equals("/favicon.ico")) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        File file = new File(frontendDir, path.startsWith("/") ? path.substring(1) : path);
        if (!file.exists() || file.isDirectory()) {
            File html = new File(frontendDir, (path.startsWith("/") ? path.substring(1) : path) + ".html");
            if (html.exists() && html.isFile()) file = html;
        }

        if (file.exists() && file.isFile()) {
            String name = file.getName().toLowerCase();
            String type = "text/plain";
            if (name.endsWith(".html")) type = "text/html; charset=UTF-8";
            else if (name.endsWith(".css")) type = "text/css; charset=UTF-8";
            else if (name.endsWith(".js")) type = "application/javascript; charset=UTF-8";
            else if (name.endsWith(".png")) type = "image/png";
            else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) type = "image/jpeg";
            else if (name.endsWith(".pdf")) type = "application/pdf";
            else if (name.endsWith(".svg")) type = "image/svg+xml";

            byte[] bytes = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) { fis.read(bytes); }
            exchange.getResponseHeaders().set("Content-Type", type);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        } else {
            byte[] notFound = "<h1>404 Not Found</h1><p>Page does not exist.</p>".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(404, notFound.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(notFound); }
        }
    }

    private static void handleApi(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/api/register")) {
            Map<String, String> p = parseForm(exchange);
            String name = p.getOrDefault("name", "").trim();
            String email = p.getOrDefault("email", "").trim();
            String pass = p.getOrDefault("password", "").trim();
            String phone = p.getOrDefault("phone", "").trim();
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || phone.isEmpty()) {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"All fields are required!\"}");
            } else if (system.registerStudent(name, email, pass, phone)) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Registration successful! You can now login.\"}");
            } else {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"This email is already registered! Please login.\"}");
            }
        } else if (path.equals("/api/login")) {
            Map<String, String> p = parseForm(exchange);
            Student s = system.authenticateStudent(p.get("email"), p.get("password"));
            if (s != null) {
                sendJson(exchange, 200, String.format("{\"success\":true,\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"message\":\"Login successful!\"}",
                        escape(s.getId()), escape(s.getName()), escape(s.getEmail()), escape(s.getPhoneNumber())));
            } else {
                sendJson(exchange, 401, "{\"success\":false,\"message\":\"Invalid email or password! Please check credentials.\"}");
            }
        } else if (path.equals("/api/apply")) {
            Map<String, String> p = parseForm(exchange);
            double ssc = parseDouble(p.get("sscMarks")), hsc = parseDouble(p.get("hscMarks")), cet = parseDouble(p.get("cetScore"));
            List<String> prefs = new ArrayList<>();
            String rawPrefs = p.getOrDefault("preferences", "");
            if (!rawPrefs.isEmpty()) {
                for (String item : rawPrefs.split("[,;]")) {
                    if (!item.trim().isEmpty() && !prefs.contains(item.trim())) prefs.add(item.trim());
                }
            }
            String course = p.getOrDefault("course", "").trim();
            if (prefs.isEmpty() && !course.isEmpty()) prefs.add(course);
            Application app = system.submitApplication(p.getOrDefault("email", ""), p.getOrDefault("name", ""),
                    ssc, hsc, cet, course, p.getOrDefault("category", "General"), prefs);
            sendJson(exchange, 200, String.format("{\"success\":true,\"appId\":\"%s\",\"message\":\"Application submitted successfully!\"}", escape(app.getApplicationId())));
        } else if (path.equals("/api/status")) {
            Map<String, String> q = parseQuery(exchange.getRequestURI().getQuery());
            Application app = system.getApplication(q.getOrDefault("appId", q.get("email")));
            if (app != null) {
                Student s = system.getStudentByEmail(app.getStudentEmail());
                sendJson(exchange, 200, formatAppJson(app, s != null ? s.getPhoneNumber() : ""));
            } else {
                sendJson(exchange, 200, "{\"found\":false,\"message\":\"No application found for this candidate.\"}");
            }
        } else if (path.equals("/api/upload-document")) {
            handleUpload(exchange);
        } else if (path.equals("/api/document/view") || path.equals("/api/document/download")) {
            handleDocumentFile(exchange, path.endsWith("download"));
        } else if (path.equals("/api/student/allocation-response")) {
            Map<String, String> p = parseForm(exchange);
            String appId = p.getOrDefault("appId", ""), decision = p.getOrDefault("action", "");
            if (appId.isEmpty() || decision.isEmpty()) {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Application ID and action are required!\"}");
            } else if (system.respondToAllocation(appId, decision, p.get("remarks"))) {
                String msg = "Accept".equalsIgnoreCase(decision) ? "Congratulations! You have accepted the allocated branch. Your admission is CONFIRMED."
                        : "Allocation rejected. Your application has been returned to the Admission Committee for review and reallocation.";
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"" + msg + "\"}");
            } else {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Failed to process allocation decision. Application not found.\"}");
            }
        } else if (path.equals("/api/admin-login")) {
            Map<String, String> p = parseForm(exchange);
            Admin a = system.authenticateAdmin(p.get("email"), p.get("password"));
            if (a != null) {
                sendJson(exchange, 200, String.format("{\"success\":true,\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"message\":\"Admin authentication successful!\"}",
                        escape(a.getId()), escape(a.getName()), escape(a.getEmail()), escape(a.getRoleDescription())));
            } else {
                sendJson(exchange, 401, "{\"success\":false,\"message\":\"Invalid Admin credentials! Access Denied.\"}");
            }
        } else if (path.equals("/api/admin/applications")) {
            List<Application> apps = system.getAllApplications();
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < apps.size(); i++) {
                Application a = apps.get(i);
                Student s = system.getStudentByEmail(a.getStudentEmail());
                sb.append(formatAppJson(a, s != null ? s.getPhoneNumber() : ""));
                if (i < apps.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendJson(exchange, 200, sb.toString());
        } else if (path.equals("/api/admin/verify-documents")) {
            Map<String, String> p = parseForm(exchange);
            String status = p.getOrDefault("status", "Documents Verified");
            if (system.verifyApplicationDocuments(p.get("appId"), status, p.get("remarks"))) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Document verification updated to " + escape(status) + "!\"}");
            } else {
                sendJson(exchange, 404, "{\"success\":false,\"message\":\"Application ID not found.\"}");
            }
        } else if (path.equals("/api/admin/allocate-branch")) {
            Map<String, String> p = parseForm(exchange);
            String appId = p.getOrDefault("appId", ""), branch = p.getOrDefault("branch", "");
            if (appId.isEmpty() || branch.isEmpty()) {
                sendJson(exchange, 400, "{\"success\":false,\"message\":\"Application ID and branch must be provided!\"}");
            } else if (system.allocateBranch(appId, branch, p.get("remarks"))) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Branch '" + escape(branch) + "' allocated successfully! Decision sent to candidate.\"}");
            } else {
                sendJson(exchange, 404, "{\"success\":false,\"message\":\"Application ID not found.\"}");
            }
        } else if (path.equals("/api/admin/action")) {
            Map<String, String> p = parseForm(exchange);
            String action = p.getOrDefault("action", "");
            if (system.updateApplicationStatus(p.get("appId"), action, p.get("remarks"))) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Application status updated to " + escape(action) + "!\"}");
            } else {
                sendJson(exchange, 404, "{\"success\":false,\"message\":\"Application ID not found.\"}");
            }
        } else {
            sendJson(exchange, 404, "{\"success\":false,\"message\":\"API endpoint not found.\"}");
        }
    }

    private static void handleUpload(HttpExchange exchange) throws IOException {
        Map<String, String> q = parseQuery(exchange.getRequestURI().getQuery());
        String appId = q.getOrDefault("appId", q.getOrDefault("email", ""));
        String docType = q.getOrDefault("docType", "");
        String fileName = q.getOrDefault("fileName", "");
        if (appId.isEmpty() && exchange.getRequestHeaders().containsKey("X-App-Id")) appId = exchange.getRequestHeaders().getFirst("X-App-Id");
        if (docType.isEmpty() && exchange.getRequestHeaders().containsKey("X-Doc-Type")) docType = exchange.getRequestHeaders().getFirst("X-Doc-Type");
        if (fileName.isEmpty() && exchange.getRequestHeaders().containsKey("X-File-Name")) fileName = exchange.getRequestHeaders().getFirst("X-File-Name");

        byte[] bytes = readBytes(exchange.getRequestBody());
        if (appId.isEmpty() || docType.isEmpty() || fileName.isEmpty() || bytes.length == 0) {
            sendJson(exchange, 400, "{\"success\":false,\"message\":\"Invalid upload request or empty file.\"}");
            return;
        }
        if (system.saveUploadedDocument(appId, docType, fileName, bytes)) {
            sendJson(exchange, 200, String.format("{\"success\":true,\"message\":\"Document '%s' uploaded successfully!\",\"docType\":\"%s\",\"fileName\":\"%s\"}",
                    escape(Document.getTitleForType(docType)), escape(docType), escape(fileName)));
        } else {
            sendJson(exchange, 400, "{\"success\":false,\"message\":\"Failed to upload document. Verify application ID and allowed formats (PDF, JPG, PNG under 5MB).\"}");
        }
    }

    private static void handleDocumentFile(HttpExchange exchange, boolean download) throws IOException {
        Map<String, String> q = parseQuery(exchange.getRequestURI().getQuery());
        File file = system.getDocumentFile(q.getOrDefault("appId", q.get("email")), q.get("docType"));
        if (file != null && file.exists()) {
            String name = file.getName().toLowerCase();
            String type = name.endsWith(".pdf") ? "application/pdf" : (name.endsWith(".png") ? "image/png" : "image/jpeg");
            exchange.getResponseHeaders().set("Content-Type", type);
            exchange.getResponseHeaders().set("Content-Disposition", (download ? "attachment" : "inline") + "; filename=\"" + file.getName() + "\"");
            byte[] bytes = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) { fis.read(bytes); }
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        } else {
            byte[] err = "<h1>404 Not Found</h1><p>The requested document was not found or has not been uploaded yet.</p>".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(404, err.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(err); }
        }
    }

    private static void sendJson(HttpExchange exchange, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private static Map<String, String> parseForm(HttpExchange exchange) throws IOException {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
            for (String pair : sb.toString().split("&")) {
                String[] kv = pair.split("=");
                if (kv.length >= 2) map.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                else if (kv.length == 1 && !kv[0].isEmpty()) map.put(URLDecoder.decode(kv[0], "UTF-8"), "");
            }
        }
        return map;
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            try {
                if (kv.length >= 2) map.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                else if (kv.length == 1 && !kv[0].isEmpty()) map.put(URLDecoder.decode(kv[0], "UTF-8"), "");
            } catch (Exception ignored) {}
        }
        return map;
    }

    private static byte[] readBytes(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = is.read(buf)) != -1) bos.write(buf, 0, n);
        return bos.toByteArray();
    }

    private static double parseDouble(String str) {
        try { return str != null ? Double.parseDouble(str.trim()) : 0.0; } catch (Exception e) { return 0.0; }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\b", "\\b").replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static String formatAppJson(Application app, String phone) {
        StringBuilder prefs = new StringBuilder("[");
        for (int i = 0; i < app.getBranchPreferences().size(); i++) {
            prefs.append("\"").append(escape(app.getBranchPreferences().get(i))).append("\"");
            if (i < app.getBranchPreferences().size() - 1) prefs.append(",");
        }
        prefs.append("]");

        StringBuilder docs = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, String> e : app.getUploadedDocuments().entrySet()) {
            if (i > 0) docs.append(",");
            docs.append("\"").append(escape(e.getKey())).append("\":\"").append(escape(e.getValue())).append("\"");
            i++;
        }
        docs.append("}");

        return String.format("{\"found\":true,\"appId\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\"," +
                "\"ssc\":%.2f,\"hsc\":%.2f,\"cet\":%.2f,\"course\":\"%s\",\"category\":\"%s\",\"status\":\"%s\",\"remarks\":\"%s\"," +
                "\"preferences\":%s,\"allocatedBranch\":\"%s\",\"allocationStatus\":\"%s\",\"verificationStatus\":\"%s\"," +
                "\"documents\":%s,\"allocationRemarks\":\"%s\"}",
                escape(app.getApplicationId()), escape(app.getStudentName()), escape(app.getStudentEmail()), escape(phone),
                app.getSscMarks(), app.getHscMarks(), app.getCetScore(), escape(app.getCourseChoice()),
                escape(app.getCategory()), escape(app.getStatus()), escape(app.getRemarks()),
                prefs, escape(app.getAllocatedBranch()), escape(app.getAllocationStatus()),
                escape(app.getVerificationStatus()), docs, escape(app.getAllocationRemarks()));
    }

    private static String findFrontend() {
        String[] dirs = { "frontend", "CollegeAdmissionPortal/frontend", "../frontend", "../../frontend" };
        for (String d : dirs) {
            if (new File(d, "index.html").exists()) return new File(d).getAbsolutePath();
        }
        return "frontend";
    }
}
