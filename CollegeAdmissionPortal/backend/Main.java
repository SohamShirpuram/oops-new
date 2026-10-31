import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.net.InetSocketAddress;

public class Main {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            AdmissionSystem system = new AdmissionSystem();
            String frontendPath = locateFrontendFolder();

            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            server.createContext("/", new StaticFileHandler(frontendPath));

            StudentApiHandler studentHandler = new StudentApiHandler(system);
            server.createContext("/api/register", studentHandler);
            server.createContext("/api/login", studentHandler);
            server.createContext("/api/apply", studentHandler);
            server.createContext("/api/status", studentHandler);
            server.createContext("/api/student/allocation-response", studentHandler);

            AdminApiHandler adminHandler = new AdminApiHandler(system);
            server.createContext("/api/admin-login", adminHandler);
            server.createContext("/api/admin/applications", adminHandler);
            server.createContext("/api/admin/action", adminHandler);
            server.createContext("/api/admin/verify-documents", adminHandler);
            server.createContext("/api/admin/allocate-branch", adminHandler);

            DocumentApiHandler docHandler = new DocumentApiHandler(system);
            server.createContext("/api/upload-document", docHandler);
            server.createContext("/api/document/view", docHandler);
            server.createContext("/api/document/download", docHandler);

            server.setExecutor(null);
            server.start();

            System.out.println("Web server started at http://localhost:" + PORT + "/");
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static String locateFrontendFolder() {
        String[] candidates = { "frontend", "CollegeAdmissionPortal/frontend", "../frontend", "../../frontend" };
        for (String c : candidates) {
            File dir = new File(c);
            if (new File(dir, "index.html").exists()) {
                return dir.getAbsolutePath();
            }
        }
        try {
            File codeLoc = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File parent = codeLoc.isDirectory() ? codeLoc : codeLoc.getParentFile();
            File f1 = new File(parent, "frontend");
            if (new File(f1, "index.html").exists()) return f1.getAbsolutePath();
            if (parent.getParentFile() != null) {
                File f2 = new File(parent.getParentFile(), "frontend");
                if (new File(f2, "index.html").exists()) return f2.getAbsolutePath();
                File f3 = new File(parent.getParentFile(), "CollegeAdmissionPortal/frontend");
                if (new File(f3, "index.html").exists()) return f3.getAbsolutePath();
            }
        } catch (Exception ignored) {
        }
        return "frontend";
    }
}
