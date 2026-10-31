# College Admission Portal - Pure JDK Backend Architecture & Source Code

This document provides the complete source code of the **Pure Core Java (JDK 8+)** backend for the **College Admission Portal** (Orchid College of Engineering & Technology).

All external framework dependencies and separate API handler architectures have been completely removed. The system runs directly on Java's built-in `com.sun.net.httpserver.HttpServer`.

---

## 1. Backend Architecture & OOP Classification

| # | Java File | Lines | Primary OOP Role / Responsibility | Key Capabilities |
|---|---|---|---|---|
| 1 | `User.java` | 74 | Abstract Base Class for Users | Core OOP concept: Abstraction & Inheritance base. |
| 2 | `Student.java` | 35 | Student Entity (Inherits User) | Core OOP concept: Inheritance & Polymorphism (`getRoleDescription()`). |
| 3 | `Admin.java` | 35 | Admin Entity (Inherits User) | Core OOP concept: Inheritance & Polymorphism (`getRoleDescription()`). |
| 4 | `Document.java` | 97 | Document Entity & Metadata Helper | Encapsulates document types, titles, sizes and file formatting. |
| 5 | `Application.java` | 200 | Admission Application Model | Encapsulates applicant marks, course choices, multi-branch preferences, and status. |
| 6 | `FileStorage.java` | 125 | Pure Java File Persistence | Direct file I/O (`BufferedReader`/`BufferedWriter`) for plain text storage of students, admins, and applications. |
| 7 | `AdmissionSystem.java` | 195 | Admission Management Controller | Coordinates student registration, login validation, application submission, verification, and branch allocation. |
| 8 | `Main.java` | 332 | Pure JDK HTTP Web Server | Uses `com.sun.net.httpserver.HttpServer` to serve static web files and handle form submissions directly in pure Java. |

---

## 2. Complete Source Code for All Pure JDK Classes

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
> **Role**: Encapsulates document types, titles, sizes and file formatting.

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

> **Purpose**: Admission Application Model  
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

> **Purpose**: Pure Java File Persistence  
> **Role**: Direct file I/O (`BufferedReader`/`BufferedWriter`) for plain text storage of students, admins, and applications.

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

> **Purpose**: Admission Management Controller  
> **Role**: Coordinates student registration, login validation, application submission, verification, and branch allocation.

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

### 8. `Main.java` (332 lines)

> **Purpose**: Pure JDK HTTP Web Server  
> **Role**: Uses `com.sun.net.httpserver.HttpServer` to serve static web files and handle form submissions directly in pure Java.

```java
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

```

---

