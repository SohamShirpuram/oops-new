import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class AdminApiHandler implements HttpHandler {

    private final AdmissionSystem system;

    public AdminApiHandler(AdmissionSystem system) {
        this.system = system;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.endsWith("/admin-login")) {
            handleLogin(exchange);
        } else if (path.endsWith("/applications")) {
            handleApplications(exchange);
        } else if (path.endsWith("/action")) {
            handleAction(exchange);
        } else if (path.endsWith("/verify-documents")) {
            handleVerifyDocuments(exchange);
        } else if (path.endsWith("/allocate-branch")) {
            handleAllocateBranch(exchange);
        } else {
            HttpHelper.sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"Endpoint not found\"}");
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }
        Map<String, String> params = HttpHelper.parseRequestBody(exchange);
        Admin admin = system.authenticateAdmin(params.get("email"), params.get("password"));
        if (admin != null) {
            String json = String.format("{\"success\":true,\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"message\":\"Admin authentication successful!\"}",
                    HttpHelper.escapeJson(admin.getId()), HttpHelper.escapeJson(admin.getName()),
                    HttpHelper.escapeJson(admin.getEmail()), HttpHelper.escapeJson(admin.getRoleDescription()));
            HttpHelper.sendJsonResponse(exchange, 200, json);
        } else {
            HttpHelper.sendJsonResponse(exchange, 401, "{\"success\":false,\"message\":\"Invalid Admin credentials! Access Denied.\"}");
        }
    }

    private void handleApplications(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }
        ArrayList<Application> allApps = system.getAllApplications();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < allApps.size(); i++) {
            Application a = allApps.get(i);
            Student s = system.getStudentByEmail(a.getStudentEmail());
            sb.append(HttpHelper.formatApplicationJson(a, s != null ? s.getPhoneNumber() : ""));
            if (i < allApps.size() - 1) sb.append(",");
        }
        sb.append("]");
        HttpHelper.sendJsonResponse(exchange, 200, sb.toString());
    }

    private void handleAction(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }
        Map<String, String> p = HttpHelper.parseRequestBody(exchange);
        String action = p.getOrDefault("action", "");
        if (system.updateApplicationStatus(p.get("appId"), action, p.get("remarks"))) {
            HttpHelper.sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Application status updated to " + HttpHelper.escapeJson(action) + "!\"}");
        } else {
            HttpHelper.sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"Application ID not found.\"}");
        }
    }

    private void handleVerifyDocuments(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }
        Map<String, String> p = HttpHelper.parseRequestBody(exchange);
        String status = p.getOrDefault("status", "Documents Verified");
        if (system.verifyApplicationDocuments(p.get("appId"), status, p.get("remarks"))) {
            HttpHelper.sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Document verification updated to " + HttpHelper.escapeJson(status) + "!\"}");
        } else {
            HttpHelper.sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"Application ID not found.\"}");
        }
    }

    private void handleAllocateBranch(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }
        Map<String, String> p = HttpHelper.parseRequestBody(exchange);
        String appId = p.getOrDefault("appId", "");
        String branch = p.getOrDefault("branch", "");
        if (appId.isEmpty() || branch.isEmpty()) {
            HttpHelper.sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Application ID and branch must be provided!\"}");
            return;
        }
        if (system.allocateBranch(appId, branch, p.get("remarks"))) {
            HttpHelper.sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Branch '" + HttpHelper.escapeJson(branch) + "' allocated successfully! Decision sent to candidate.\"}");
        } else {
            HttpHelper.sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"Application ID not found.\"}");
        }
    }
}
