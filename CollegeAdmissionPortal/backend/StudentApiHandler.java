import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudentApiHandler implements HttpHandler {

    private final AdmissionSystem system;

    public StudentApiHandler(AdmissionSystem system) {
        this.system = system;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.endsWith("/register")) {
            handleRegister(exchange);
        } else if (path.endsWith("/login")) {
            handleLogin(exchange);
        } else if (path.endsWith("/apply")) {
            handleApply(exchange);
        } else if (path.endsWith("/status")) {
            handleStatus(exchange);
        } else if (path.endsWith("/allocation-response")) {
            handleAllocationResponse(exchange);
        } else {
            HttpHelper.sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"Endpoint not found\"}");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }
        Map<String, String> params = HttpHelper.parseRequestBody(exchange);
        String name = params.getOrDefault("name", "").trim();
        String email = params.getOrDefault("email", "").trim();
        String password = params.getOrDefault("password", "").trim();
        String phone = params.getOrDefault("phone", "").trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            HttpHelper.sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"All fields are required!\"}");
            return;
        }
        if (system.registerStudent(name, email, password, phone)) {
            HttpHelper.sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Registration successful! You can now login.\"}");
        } else {
            HttpHelper.sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"This email is already registered! Please login.\"}");
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }
        Map<String, String> params = HttpHelper.parseRequestBody(exchange);
        Student student = system.authenticateStudent(params.get("email"), params.get("password"));
        if (student != null) {
            String json = String.format("{\"success\":true,\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"message\":\"Login successful!\"}",
                    HttpHelper.escapeJson(student.getId()), HttpHelper.escapeJson(student.getName()),
                    HttpHelper.escapeJson(student.getEmail()), HttpHelper.escapeJson(student.getPhoneNumber()));
            HttpHelper.sendJsonResponse(exchange, 200, json);
        } else {
            HttpHelper.sendJsonResponse(exchange, 401, "{\"success\":false,\"message\":\"Invalid email or password! Please check credentials.\"}");
        }
    }

    private void handleApply(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }
        Map<String, String> params = HttpHelper.parseRequestBody(exchange);
        double ssc, hsc, cet;
        try {
            ssc = Double.parseDouble(params.getOrDefault("sscMarks", "0"));
            hsc = Double.parseDouble(params.getOrDefault("hscMarks", "0"));
            cet = Double.parseDouble(params.getOrDefault("cetScore", "0"));
        } catch (NumberFormatException e) {
            HttpHelper.sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Please enter valid numerical marks!\"}");
            return;
        }

        List<String> prefs = new ArrayList<>();
        String prefRaw = params.getOrDefault("preferences", "");
        if (!prefRaw.isEmpty()) {
            for (String p : prefRaw.split("[,;]")) {
                String clean = p.trim();
                if (!clean.isEmpty() && !prefs.contains(clean)) prefs.add(clean);
            }
        }
        String course = params.getOrDefault("course", "");
        if (prefs.isEmpty() && !course.isEmpty()) prefs.add(course.trim());

        Application app = system.submitApplication(params.getOrDefault("email", ""), params.getOrDefault("name", ""),
                ssc, hsc, cet, course, params.getOrDefault("category", "General"), prefs);

        String json = String.format("{\"success\":true,\"appId\":\"%s\",\"message\":\"Application submitted successfully!\"}",
                HttpHelper.escapeJson(app.getApplicationId()));
        HttpHelper.sendJsonResponse(exchange, 200, json);
    }

    private void handleStatus(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }
        Map<String, String> q = HttpHelper.parseQueryString(exchange.getRequestURI().getQuery());
        Application app = system.getApplication(q.getOrDefault("appId", q.get("email")));

        if (app != null) {
            Student student = system.getStudentByEmail(app.getStudentEmail());
            String phone = student != null ? student.getPhoneNumber() : "";
            HttpHelper.sendJsonResponse(exchange, 200, HttpHelper.formatApplicationJson(app, phone));
        } else {
            HttpHelper.sendJsonResponse(exchange, 200, "{\"found\":false,\"message\":\"No application found for this candidate.\"}");
        }
    }

    private void handleAllocationResponse(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpHelper.sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            return;
        }
        Map<String, String> params = HttpHelper.parseRequestBody(exchange);
        String appId = params.getOrDefault("appId", "");
        String decision = params.getOrDefault("action", "");
        String remarks = params.getOrDefault("remarks", "");

        if (appId.isEmpty() || decision.isEmpty()) {
            HttpHelper.sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Application ID and action are required!\"}");
            return;
        }

        if (system.respondToAllocation(appId, decision, remarks)) {
            String msg = "Accept".equalsIgnoreCase(decision)
                    ? "Congratulations! You have accepted the allocated branch. Your admission is CONFIRMED."
                    : "Allocation rejected. Your application has been returned to the Admission Committee for review and reallocation.";
            HttpHelper.sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"" + msg + "\"}");
        } else {
            HttpHelper.sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Failed to process allocation decision. Application not found.\"}");
        }
    }
}
