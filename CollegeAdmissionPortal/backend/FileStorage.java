import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private String studentsFilePath;
    private String adminFilePath;
    private String applicationsFilePath;
    private String documentsDirPath;

    public FileStorage() {
        resolveFilePaths();
    }

    private void resolveFilePaths() {
        String[] candidates = { "data", "CollegeAdmissionPortal/data", "../data", "../../data" };
        File resolved = null;
        for (String c : candidates) {
            File dir = new File(c);
            if (new File(dir, "students.txt").exists() || new File(dir, "admin.txt").exists()) {
                resolved = dir;
                break;
            }
        }
        if (resolved == null) {
            for (String c : candidates) {
                File dir = new File(c);
                if (dir.exists() && dir.isDirectory()) { resolved = dir; break; }
            }
        }
        if (resolved == null) resolved = new File("data");

        studentsFilePath = new File(resolved, "students.txt").getPath();
        adminFilePath = new File(resolved, "admin.txt").getPath();
        applicationsFilePath = new File(resolved, "applications.txt").getPath();
        documentsDirPath = new File(resolved, "documents").getPath();

        File docDir = new File(documentsDirPath);
        if (!docDir.exists()) docDir.mkdirs();
    }

    public ArrayList<Student> loadStudents() {
        ArrayList<Student> list = new ArrayList<>();
        File file = new File(studentsFilePath);
        if (!file.exists()) return list;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Student s = Student.fromDataString(line);
                if (s != null) list.add(s);
            }
        } catch (IOException ignored) {}
        return list;
    }

    public ArrayList<Admin> loadAdmins() {
        ArrayList<Admin> list = new ArrayList<>();
        File file = new File(adminFilePath);
        if (!file.exists()) return list;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Admin a = Admin.fromDataString(line);
                if (a != null) list.add(a);
            }
        } catch (IOException ignored) {}
        return list;
    }

    public ArrayList<Application> loadApplications() {
        ArrayList<Application> list = new ArrayList<>();
        File file = new File(applicationsFilePath);
        if (!file.exists()) return list;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Application app = Application.fromDataString(line);
                if (app != null) list.add(app);
            }
        } catch (IOException ignored) {}
        return list;
    }

    public synchronized void saveStudents(List<Student> students) {
        File file = new File(studentsFilePath);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file, false))) {
            w.write("# Format: StudentID|FullName|Email|Password|PhoneNumber\n");
            for (Student s : students) { w.write(s.toDataString() + "\n"); }
        } catch (IOException ignored) {}
    }

    public synchronized void saveApplications(List<Application> apps) {
        File file = new File(applicationsFilePath);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file, false))) {
            w.write("# Format: ApplicationID|StudentEmail|StudentName|SSCMarks|HSCMarks|CETScore|CourseChoice|Category|Status|Remarks|BranchPreferences|AllocatedBranch|AllocationStatus|VerificationStatus|UploadedDocuments|AllocationRemarks\n");
            for (Application a : apps) { w.write(a.toDataString() + "\n"); }
        } catch (IOException ignored) {}
    }

    public String saveDocumentFile(String appId, String docType, String originalFileName, byte[] fileBytes) {
        if (fileBytes == null || fileBytes.length == 0 || fileBytes.length > 5 * 1024 * 1024) return null;
        String clean = new File(originalFileName).getName();
        String ext = clean.contains(".") ? clean.substring(clean.lastIndexOf('.')).toLowerCase() : "";
        if (!ext.equals(".pdf") && !ext.equals(".jpg") && !ext.equals(".jpeg") && !ext.equals(".png")) return null;

        File dir = new File(documentsDirPath, appId);
        if (!dir.exists()) dir.mkdirs();
        String storedName = docType.trim().toLowerCase() + "_" + clean.replaceAll("[^a-zA-Z0-9._-]", "_");
        try (FileOutputStream fos = new FileOutputStream(new File(dir, storedName))) {
            fos.write(fileBytes);
            return storedName;
        } catch (IOException e) {
            return null;
        }
    }

    public File getDocumentFile(String appId, String storedFileName) {
        if (appId == null || storedFileName == null || storedFileName.isEmpty()) return null;
        File file = new File(new File(documentsDirPath, appId), storedFileName);
        return (file.exists() && file.isFile()) ? file : null;
    }
}
