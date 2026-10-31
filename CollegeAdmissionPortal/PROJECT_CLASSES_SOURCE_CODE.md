# College Admission Portal - Refactored Backend Architecture & Source Code

This document provides the complete source code of the refactored, beginner-friendly Core Java backend for the **College Admission Portal** (Orchid College of Engineering & Technology).

---

## 1. Backend Architecture & OOP Classification

| # | Java File | Lines | Primary OOP Role / Responsibility | Key Methods / Capabilities |
|---|---|---|---|---|
| 1 | `User.java` | 74 | Abstract Base Class for Users | Core OOP concept: Abstraction & Inheritance base. |
| 2 | `Student.java` | 35 | Student Entity (Inherits User) | Core OOP concept: Inheritance & Polymorphism (`getRoleDescription()`). |
| 3 | `Admin.java` | 35 | Admin Entity (Inherits User) | Core OOP concept: Inheritance & Polymorphism (`getRoleDescription()`). |
| 4 | `Document.java` | 97 | Document Entity & Metadata Helper | Encapsulates document file types, names, sizes and formatting. |
| 5 | `Application.java` | 200 | Admission Application Entity | Encapsulates applicant marks, course choices, multi-branch preferences, and status. |
| 6 | `FileStorage.java` | 125 | Data Persistence Manager | Encapsulates plain text file reading/writing for students, admins, applications and documents. |
| 7 | `AdmissionSystem.java` | 195 | Central Admission Controller | Coordinates registration, authentication, application submission, document verification, and seat allocation. |
| 8 | `HttpHelper.java` | 118 | HTTP Request & JSON Serialization Helper | Handles query string decoding, UTF-8 form parsing, byte streams, and JSON formatting. |
| 9 | `StaticFileHandler.java` | 87 | Web Portal Static File Server | Serves HTML, CSS, JavaScript, images, and documents with MIME type mapping. |
| 10 | `StudentApiHandler.java` | 149 | Student REST API Endpoints | Handles `/api/register`, `/api/login`, `/api/apply`, `/api/status`, `/api/student/allocation-response`. |
| 11 | `AdminApiHandler.java` | 113 | Admin REST API Endpoints | Handles `/api/admin-login`, `/api/admin/applications`, `/api/admin/action`, `/api/admin/verify-documents`, `/api/admin/allocate-branch`. |
| 12 | `DocumentApiHandler.java` | 112 | Document Upload & Download API | Handles `/api/upload-document`, `/api/document/view`, `/api/document/download`. |
| 13 | `Main.java` | 71 | Server Application Entry Point | Configures routes, initializes HTTP server on port 8080, and starts the system. |

---

## 2. Complete Source Code for All Classes

### 1. `User.java` (74 lines)

> **Purpose**: Abstract Base Class for Users  
> **Role**: Core OOP concept: Abstraction & Inheritance base.

```java
public abstract class User {

    private String id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;

    public User(String id, String name, String email, String password, String phoneNumber, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public abstract String getRoleDescription();

    public void displayUserDetails() {
        System.out.println("Name: " + this.name);
        System.out.println("Email: " + this.email);
        System.out.println("Role: " + getRoleDescription());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

```

---

### 2. `Student.java` (35 lines)

> **Purpose**: Student Entity (Inherits User)  
> **Role**: Core OOP concept: Inheritance & Polymorphism (`getRoleDescription()`).

```java
public class Student extends User {

    public Student(String id, String name, String email, String password, String phoneNumber) {
        super(id, name, email, password, phoneNumber, "STUDENT");
    }

    @Override
    public String getRoleDescription() {
        return "Student Applicant eligible to apply for Degree College Admissions.";
    }

    public String toDataString() {
        return getId() + "|" + getName() + "|" + getEmail() + "|" + getPassword() + "|" + getPhoneNumber();
    }

    public static Student fromDataString(String dataLine) {
        if (dataLine == null || dataLine.trim().isEmpty() || dataLine.startsWith("#")) {
            return null;
        }

        String[] parts = dataLine.split("\\|");

        if (parts.length >= 5) {
            String id = parts[0].trim();
            String name = parts[1].trim();
            String email = parts[2].trim();
            String password = parts[3].trim();
            String phone = parts[4].trim();

            return new Student(id, name, email, password, phone);
        }

        return null;
    }
}

```

---

### 3. `Admin.java` (35 lines)

> **Purpose**: Admin Entity (Inherits User)  
> **Role**: Core OOP concept: Inheritance & Polymorphism (`getRoleDescription()`).

```java
public class Admin extends User {

    public Admin(String id, String name, String email, String password, String phoneNumber) {
        super(id, name, email, password, phoneNumber, "ADMIN");
    }

    @Override
    public String getRoleDescription() {
        return "System Administrator with full authority to review, approve, or reject admission applications.";
    }

    public String toDataString() {
        return getId() + "|" + getName() + "|" + getEmail() + "|" + getPassword() + "|" + getPhoneNumber();
    }

    public static Admin fromDataString(String dataLine) {
        if (dataLine == null || dataLine.trim().isEmpty() || dataLine.startsWith("#")) {
            return null;
        }

        String[] parts = dataLine.split("\\|");

        if (parts.length >= 5) {
            String id = parts[0].trim();
            String name = parts[1].trim();
            String email = parts[2].trim();
            String password = parts[3].trim();
            String phone = parts[4].trim();

            return new Admin(id, name, email, password, phone);
        }

        return null;
    }
}

```

---

### 4. `Document.java` (97 lines)

> **Purpose**: Document Entity & Metadata Helper  
> **Role**: Encapsulates document file types, names, sizes and formatting.

```java
public class Document {

    private String docType;
    private String docTitle;
    private String originalFileName;
    private String storedFileName;
    private long fileSize;
    private String fileType;

    public Document() {
    }

    public Document(String docType, String docTitle, String originalFileName,
                    String storedFileName, long fileSize, String fileType) {
        this.docType = docType;
        this.docTitle = docTitle;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    public static String getTitleForType(String docType) {
        if (docType == null) return "Supporting Document";
        switch (docType.toLowerCase()) {
            case "aadhaar":
                return "Aadhaar Card";
            case "ssc":
                return "SSC (10th) Marksheet";
            case "hsc":
                return "HSC (12th) Marksheet";
            case "cet":
                return "CET Marksheet / Scorecard";
            case "other":
                return "Other Supporting Document";
            default:
                return "Supporting Document";
        }
    }

    public String getFormattedFileSize() {
        if (fileSize <= 0) return "0 KB";
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        }
        return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}

```

---

### 5. `Application.java` (200 lines)

> **Purpose**: Admission Application Entity  
> **Role**: Encapsulates applicant marks, course choices, multi-branch preferences, and status.

```java
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Application {

    private String applicationId;
    private String studentEmail;
    private String studentName;
    private double sscMarks;
    private double hscMarks;
    private double cetScore;
    private String courseChoice;
    private String category;
    private String status;
    private String remarks;
    private List<String> branchPreferences;
    private String allocatedBranch;
    private String allocationStatus;
    private String verificationStatus;
    private Map<String, String> uploadedDocuments;
    private String allocationRemarks;

    public Application() {
        this.branchPreferences = new ArrayList<>();
        this.uploadedDocuments = new LinkedHashMap<>();
        this.allocatedBranch = "";
        this.allocationStatus = "Pending Allocation";
        this.verificationStatus = "Pending Verification";
        this.allocationRemarks = "";
    }

    public Application(String applicationId, String studentEmail, String studentName,
                       double sscMarks, double hscMarks, double cetScore,
                       String courseChoice, String category, String status, String remarks) {
        this();
        this.applicationId = applicationId;
        this.studentEmail = studentEmail;
        this.studentName = studentName;
        this.sscMarks = sscMarks;
        this.hscMarks = hscMarks;
        this.cetScore = cetScore;
        this.courseChoice = courseChoice;
        this.category = category;
        this.status = status;
        this.remarks = remarks;

        if (courseChoice != null && !courseChoice.trim().isEmpty()) {
            this.branchPreferences.add(courseChoice.trim());
        }
        if ("Approved".equalsIgnoreCase(status) || "Accepted".equalsIgnoreCase(status)) {
            this.allocatedBranch = courseChoice;
            this.allocationStatus = "Accepted";
            this.verificationStatus = "Documents Verified";
        } else if ("Rejected".equalsIgnoreCase(status)) {
            this.allocationStatus = "Rejected";
        }
    }

    public Application(String applicationId, String studentEmail, String studentName,
                       double sscMarks, double hscMarks, double cetScore,
                       String courseChoice, String category, String status, String remarks,
                       List<String> branchPreferences, String allocatedBranch,
                       String allocationStatus, String verificationStatus,
                       Map<String, String> uploadedDocuments, String allocationRemarks) {
        this.applicationId = applicationId;
        this.studentEmail = studentEmail;
        this.studentName = studentName;
        this.sscMarks = sscMarks;
        this.hscMarks = hscMarks;
        this.cetScore = cetScore;
        this.courseChoice = courseChoice;
        this.category = category;
        this.status = status;
        this.remarks = remarks;
        this.branchPreferences = branchPreferences != null ? branchPreferences : new ArrayList<>();
        this.allocatedBranch = allocatedBranch != null ? allocatedBranch : "";
        this.allocationStatus = allocationStatus != null ? allocationStatus : "Pending Allocation";
        this.verificationStatus = verificationStatus != null ? verificationStatus : "Pending Verification";
        this.uploadedDocuments = uploadedDocuments != null ? uploadedDocuments : new LinkedHashMap<>();
        this.allocationRemarks = allocationRemarks != null ? allocationRemarks : "";
    }

    public String toDataString() {
        String prefsStr = String.join(";", branchPreferences);
        StringBuilder docsSb = new StringBuilder();
        int count = 0;
        for (Map.Entry<String, String> entry : uploadedDocuments.entrySet()) {
            if (count > 0) docsSb.append(";");
            docsSb.append(entry.getKey()).append(":").append(entry.getValue());
            count++;
        }

        return applicationId + "|" + studentEmail + "|" + studentName + "|" +
                sscMarks + "|" + hscMarks + "|" + cetScore + "|" +
                (courseChoice != null ? courseChoice : "") + "|" + category + "|" +
                status + "|" + (remarks != null ? remarks : "") + "|" + prefsStr + "|" +
                (allocatedBranch != null ? allocatedBranch : "") + "|" +
                (allocationStatus != null ? allocationStatus : "Pending Allocation") + "|" +
                (verificationStatus != null ? verificationStatus : "Pending Verification") + "|" +
                docsSb.toString() + "|" + (allocationRemarks != null ? allocationRemarks : "");
    }

    public static Application fromDataString(String dataLine) {
        if (dataLine == null || dataLine.trim().isEmpty() || dataLine.startsWith("#")) return null;
        String[] parts = dataLine.split("\\|", -1);
        if (parts.length < 10) return null;

        String appId = parts[0].trim();
        String email = parts[1].trim();
        String name = parts[2].trim();
        double ssc = 0.0, hsc = 0.0, cet = 0.0;
        try {
            ssc = Double.parseDouble(parts[3].trim());
            hsc = Double.parseDouble(parts[4].trim());
            cet = Double.parseDouble(parts[5].trim());
        } catch (NumberFormatException ignored) {}

        String course = parts[6].trim();
        String cat = parts[7].trim();
        String status = parts[8].trim();
        String remarks = parts[9].trim();

        List<String> prefs = new ArrayList<>();
        if (parts.length > 10 && !parts[10].trim().isEmpty()) {
            for (String p : parts[10].trim().split(";")) {
                if (!p.trim().isEmpty()) prefs.add(p.trim());
            }
        } else if (!course.isEmpty()) {
            prefs.add(course);
        }

        String allocBranch = parts.length > 11 ? parts[11].trim() : ("Approved".equalsIgnoreCase(status) ? course : "");
        String allocStatus = parts.length > 12 && !parts[12].trim().isEmpty() ? parts[12].trim()
                : ("Approved".equalsIgnoreCase(status) ? "Accepted" : ("Rejected".equalsIgnoreCase(status) ? "Rejected" : "Pending Allocation"));
        String verifStatus = parts.length > 13 && !parts[13].trim().isEmpty() ? parts[13].trim()
                : ("Approved".equalsIgnoreCase(status) ? "Documents Verified" : "Pending Verification");

        Map<String, String> docs = new LinkedHashMap<>();
        if (parts.length > 14 && !parts[14].trim().isEmpty()) {
            for (String pair : parts[14].trim().split(";")) {
                int colonIdx = pair.indexOf(':');
                if (colonIdx > 0 && colonIdx < pair.length() - 1) {
                    docs.put(pair.substring(0, colonIdx).trim(), pair.substring(colonIdx + 1).trim());
                }
            }
        }
        String allocRemarks = parts.length > 15 ? parts[15].trim() : "";

        return new Application(appId, email, name, ssc, hsc, cet, course, cat, status, remarks,
                prefs, allocBranch, allocStatus, verifStatus, docs, allocRemarks);
    }

    public void addUploadedDocument(String docType, String fileName) {
        if (uploadedDocuments == null) uploadedDocuments = new LinkedHashMap<>();
        uploadedDocuments.put(docType.trim().toLowerCase(), fileName.trim());
    }

    public String getUploadedDocument(String docType) {
        return (uploadedDocuments == null || docType == null) ? null : uploadedDocuments.get(docType.trim().toLowerCase());
    }

    public boolean hasDocument(String docType) {
        return uploadedDocuments != null && docType != null && uploadedDocuments.containsKey(docType.trim().toLowerCase());
    }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String id) { this.applicationId = id; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String email) { this.studentEmail = email; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String name) { this.studentName = name; }
    public double getSscMarks() { return sscMarks; }
    public void setSscMarks(double ssc) { this.sscMarks = ssc; }
    public double getHscMarks() { return hscMarks; }
    public void setHscMarks(double hsc) { this.hscMarks = hsc; }
    public double getCetScore() { return cetScore; }
    public void setCetScore(double cet) { this.cetScore = cet; }
    public String getCourseChoice() { return courseChoice; }
    public void setCourseChoice(String course) { this.courseChoice = course; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public List<String> getBranchPreferences() { return branchPreferences; }
    public void setBranchPreferences(List<String> prefs) { this.branchPreferences = prefs; }
    public String getAllocatedBranch() { return allocatedBranch; }
    public void setAllocatedBranch(String branch) { this.allocatedBranch = branch; }
    public String getAllocationStatus() { return allocationStatus; }
    public void setAllocationStatus(String status) { this.allocationStatus = status; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String status) { this.verificationStatus = status; }
    public Map<String, String> getUploadedDocuments() { return uploadedDocuments; }
    public void setUploadedDocuments(Map<String, String> docs) { this.uploadedDocuments = docs; }
    public String getAllocationRemarks() { return allocationRemarks; }
    public void setAllocationRemarks(String remarks) { this.allocationRemarks = remarks; }
}

```

---

### 6. `FileStorage.java` (125 lines)

> **Purpose**: Data Persistence Manager  
> **Role**: Encapsulates plain text file reading/writing for students, admins, applications and documents.

```java
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

```

---

### 7. `AdmissionSystem.java` (195 lines)

> **Purpose**: Central Admission Controller  
> **Role**: Coordinates registration, authentication, application submission, document verification, and seat allocation.

```java
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class AdmissionSystem {

    private final FileStorage storage;
    private final ArrayList<Student> studentList;
    private final ArrayList<Admin> adminList;
    private final ArrayList<Application> applicationList;

    public AdmissionSystem() {
        this.storage = new FileStorage();
        this.studentList = storage.loadStudents();
        this.adminList = storage.loadAdmins();
        this.applicationList = storage.loadApplications();
    }

    public synchronized boolean registerStudent(String name, String email, String password, String phone) {
        for (Student s : studentList) {
            if (s.getEmail().equalsIgnoreCase(email.trim())) return false;
        }
        String newId = "STU" + (100 + studentList.size() + 1);
        Student newStudent = new Student(newId, name.trim(), email.trim(), password.trim(), phone.trim());
        studentList.add(newStudent);
        storage.saveStudents(studentList);
        return true;
    }

    public Student authenticateStudent(String email, String password) {
        if (email == null || password == null) return null;
        for (Student s : studentList) {
            if (s.getEmail().equalsIgnoreCase(email.trim()) && s.getPassword().equals(password.trim())) return s;
        }
        return null;
    }

    public Admin authenticateAdmin(String email, String password) {
        if (email == null || password == null) return null;
        for (Admin a : adminList) {
            if (a.getEmail().equalsIgnoreCase(email.trim()) && a.getPassword().equals(password.trim())) return a;
        }
        return null;
    }

    public Student getStudentByEmail(String email) {
        if (email == null) return null;
        for (Student s : studentList) {
            if (s.getEmail().equalsIgnoreCase(email.trim())) return s;
        }
        return null;
    }

    public synchronized Application submitApplication(String email, String name, double ssc, double hsc, double cet, String course, String category) {
        List<String> prefs = new ArrayList<>();
        if (course != null && !course.trim().isEmpty()) prefs.add(course.trim());
        return submitApplication(email, name, ssc, hsc, cet, course, category, prefs);
    }

    public synchronized Application submitApplication(String email, String name, double ssc, double hsc, double cet,
                                                     String course, String category, List<String> preferences) {
        if ((course == null || course.trim().isEmpty()) && preferences != null && !preferences.isEmpty()) {
            course = preferences.get(0);
        }

        for (Application existing : applicationList) {
            if (existing.getStudentEmail().equalsIgnoreCase(email.trim())) {
                existing.setStudentName(name.trim());
                existing.setSscMarks(ssc);
                existing.setHscMarks(hsc);
                existing.setCetScore(cet);
                existing.setCourseChoice(course != null ? course.trim() : "");
                existing.setCategory(category.trim());
                if (preferences != null && !preferences.isEmpty()) existing.setBranchPreferences(new ArrayList<>(preferences));
                existing.setStatus("Pending");
                existing.setAllocationStatus("Pending Allocation");
                existing.setVerificationStatus("Pending Verification");
                existing.setRemarks("Application submitted. Scrutiny and verification in progress.");
                storage.saveApplications(applicationList);
                return existing;
            }
        }

        String newAppId = "APP" + (1000 + applicationList.size() + 1);
        List<String> prefs = (preferences != null && !preferences.isEmpty()) ? new ArrayList<>(preferences) : new ArrayList<>();
        if (prefs.isEmpty() && course != null && !course.trim().isEmpty()) prefs.add(course.trim());

        Application newApp = new Application(newAppId, email.trim(), name.trim(), ssc, hsc, cet,
                course != null ? course.trim() : "", category.trim(), "Pending",
                "Documents under scrutiny by CET committee.", prefs, "", "Pending Allocation",
                "Pending Verification", null, "");

        applicationList.add(newApp);
        storage.saveApplications(applicationList);
        return newApp;
    }

    public synchronized boolean saveUploadedDocument(String appIdOrEmail, String docType, String originalFileName, byte[] fileBytes) {
        Application app = getApplication(appIdOrEmail);
        if (app == null) return false;
        String storedName = storage.saveDocumentFile(app.getApplicationId(), docType, originalFileName, fileBytes);
        if (storedName == null) return false;
        app.addUploadedDocument(docType.trim().toLowerCase(), storedName);
        storage.saveApplications(applicationList);
        return true;
    }

    public File getDocumentFile(String appIdOrEmail, String docType) {
        Application app = getApplication(appIdOrEmail);
        if (app == null || docType == null) return null;
        return storage.getDocumentFile(app.getApplicationId(), app.getUploadedDocument(docType));
    }

    public synchronized boolean verifyApplicationDocuments(String appId, String status, String remarks) {
        Application app = getApplicationById(appId);
        if (app == null) return false;
        app.setVerificationStatus(status.trim());
        if (remarks != null && !remarks.trim().isEmpty()) app.setRemarks(remarks.trim());
        storage.saveApplications(applicationList);
        return true;
    }

    public synchronized boolean allocateBranch(String appId, String branch, String remarks) {
        Application app = getApplicationById(appId);
        if (app == null) return false;
        app.setAllocatedBranch(branch.trim());
        app.setAllocationStatus("Awaiting Student Acceptance");
        app.setStatus("Branch Allocated");
        String note = (remarks != null && !remarks.trim().isEmpty()) ? remarks.trim() : "Seat allotted in " + branch.trim() + ". Awaiting student acceptance.";
        app.setAllocationRemarks(note);
        app.setRemarks(note);
        storage.saveApplications(applicationList);
        return true;
    }

    public synchronized boolean respondToAllocation(String appIdOrEmail, String decision, String studentRemarks) {
        Application app = getApplication(appIdOrEmail);
        if (app == null) return false;
        if ("Accept".equalsIgnoreCase(decision)) {
            app.setAllocationStatus("Accepted");
            app.setStatus("Admission Confirmed");
            app.setRemarks("Candidate accepted the allocated branch (" + app.getAllocatedBranch() + "). Admission confirmed!");
        } else if ("Reject".equalsIgnoreCase(decision)) {
            app.setAllocationStatus("Allocation Rejected by Student");
            app.setStatus("Allocation Rejected - Pending Reallocation");
            String reason = (studentRemarks != null && !studentRemarks.trim().isEmpty()) ? studentRemarks.trim() : "Seeking reallocation in subsequent round";
            app.setRemarks("Allocation rejected by candidate: " + reason + ". Returned to Admission Committee for review.");
        } else {
            return false;
        }
        storage.saveApplications(applicationList);
        return true;
    }

    public synchronized boolean updateApplicationStatus(String appId, String newStatus, String remarks) {
        Application app = getApplicationById(appId);
        if (app == null) return false;
        app.setStatus(newStatus.trim());
        if ("Approved".equalsIgnoreCase(newStatus)) {
            app.setAllocationStatus("Accepted");
            if (app.getAllocatedBranch() == null || app.getAllocatedBranch().isEmpty()) app.setAllocatedBranch(app.getCourseChoice());
        } else if ("Rejected".equalsIgnoreCase(newStatus)) {
            app.setAllocationStatus("Rejected");
        }
        if (remarks != null && !remarks.trim().isEmpty()) app.setRemarks(remarks.trim());
        storage.saveApplications(applicationList);
        return true;
    }

    public Application getApplication(String idOrEmail) {
        if (idOrEmail == null) return null;
        Application app = getApplicationById(idOrEmail);
        return app != null ? app : getApplicationByEmail(idOrEmail);
    }

    public Application getApplicationById(String appId) {
        if (appId == null) return null;
        for (Application a : applicationList) {
            if (a.getApplicationId().equalsIgnoreCase(appId.trim())) return a;
        }
        return null;
    }

    public Application getApplicationByEmail(String email) {
        if (email == null) return null;
        for (Application a : applicationList) {
            if (a.getStudentEmail().equalsIgnoreCase(email.trim())) return a;
        }
        return null;
    }

    public ArrayList<Application> getAllApplications() { return applicationList; }
    public ArrayList<Student> getStudentList() { return studentList; }
    public ArrayList<Admin> getAdminList() { return adminList; }
}

```

---

### 8. `HttpHelper.java` (118 lines)

> **Purpose**: HTTP Request & JSON Serialization Helper  
> **Role**: Handles query string decoding, UTF-8 form parsing, byte streams, and JSON formatting.

```java
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

```

---

### 9. `StaticFileHandler.java` (87 lines)

> **Purpose**: Web Portal Static File Server  
> **Role**: Serves HTML, CSS, JavaScript, images, and documents with MIME type mapping.

```java
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

```

---

### 10. `StudentApiHandler.java` (149 lines)

> **Purpose**: Student REST API Endpoints  
> **Role**: Handles `/api/register`, `/api/login`, `/api/apply`, `/api/status`, `/api/student/allocation-response`.

```java
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

```

---

### 11. `AdminApiHandler.java` (113 lines)

> **Purpose**: Admin REST API Endpoints  
> **Role**: Handles `/api/admin-login`, `/api/admin/applications`, `/api/admin/action`, `/api/admin/verify-documents`, `/api/admin/allocate-branch`.

```java
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

```

---

### 12. `DocumentApiHandler.java` (112 lines)

> **Purpose**: Document Upload & Download API  
> **Role**: Handles `/api/upload-document`, `/api/document/view`, `/api/document/download`.

```java
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

```

---

### 13. `Main.java` (71 lines)

> **Purpose**: Server Application Entry Point  
> **Role**: Configures routes, initializes HTTP server on port 8080, and starts the system.

```java
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

```

---

