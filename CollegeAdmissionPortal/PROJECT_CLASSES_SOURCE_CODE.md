# College Admission Portal - Source Code & Class Documentation
*Ready-to-use reference formatted for Project Reports and PPT Presentations.*

---

## Table of Contents
1. [OOP Architecture & Class Mapping](#1-oop-architecture--class-mapping)
2. [Class 1: User.java (Abstract Base Class)](#2-class-1-userjava-abstract-base-class)
3. [Class 2: Student.java (Concrete Subclass - Inheritance)](#3-class-2-studentjava-concrete-subclass---inheritance)
4. [Class 3: Admin.java (Concrete Subclass - Inheritance)](#4-class-3-adminjava-concrete-subclass---inheritance)
5. [Class 4: Document.java (Entity Model - Encapsulation)](#5-class-4-documentjava-entity-model---encapsulation)
6. [Class 5: Application.java (Domain Entity - Overloading & Collections)](#6-class-5-applicationjava-domain-entity---overloading--collections)
7. [Class 6: AdmissionSystem.java (Final Service Controller & File I/O)](#7-class-6-admissionsystemjava-final-service-controller--file-io)
8. [Class 7: Main.java (Entry Point & Embedded HTTP Server)](#8-class-7-mainjava-entry-point--embedded-http-server)

---

## 1. OOP Architecture & Class Mapping

| Class Name | Type | Key OOP Concept | Role in System |
| :--- | :--- | :--- | :--- |
| **`User`** | `abstract class` | Abstraction, Encapsulation | Base identity superclass for accounts |
| **`Student`** | `class extends User` | Inheritance, Method Overriding | Represents applicant, serializes to `students.txt` |
| **`Admin`** | `class extends User` | Inheritance, Method Overriding | Represents admission officer, authorizes reviews |
| **`Document`** | `class` | Encapsulation, POJO | Manages metadata & formatted sizes of uploads |
| **`Application`** | `class` | Overloading, Composition | Tracks marks, 6 preferences, status lifecycle |
| **`AdmissionSystem`** | `final class` | Encapsulation, File I/O, Thread-safety | Synchronized business controller & `.txt` manager |
| **`Main`** | `class` | Modularity, Static Nested Classes | Driver with built-in `HttpServer` & 13 API handlers |

---

## 2. Class 1: User.java (Abstract Base Class)

* **Type:** `public abstract class User`
* **OOP Concepts:** **Abstraction**, **Encapsulation**, **Run-time Polymorphism**.
* **Role:** Serves as the parent superclass for both candidate students and administrative officers. Cannot be instantiated directly. Declares the abstract method `getRoleDescription()`.

### Code for PPT Presentation (Key Highlights)
```java
public abstract class User {
    private String id, name, email, password, phoneNumber, role;

    public User(String id, String name, String email, String password, String phoneNumber, String role) {
        this.id = id; this.name = name; this.email = email;
        this.password = password; this.phoneNumber = phoneNumber; this.role = role;
    }

    public abstract String getRoleDescription(); // Abstract Method (Polymorphism)

    public void displayUserDetails() {
        System.out.println("Name: " + this.name + " | Role: " + getRoleDescription());
    }
}
```

### Full Source Code for Project Report
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

## 3. Class 2: Student.java (Concrete Subclass - Inheritance)

* **Type:** `public class Student extends User`
* **OOP Concepts:** **Inheritance**, **Constructor Chaining (`super`)**, **Method Overriding**.
* **Role:** Models a student applicant. Contains custom serialization (`toDataString`) and deserialization (`fromDataString`) methods for storage in `data/students.txt`.

### Code for PPT Presentation (Key Highlights)
```java
public class Student extends User {
    public Student(String id, String name, String email, String password, String phone) {
        super(id, name, email, password, phone, "STUDENT"); // Constructor Chaining
    }

    @Override
    public String getRoleDescription() { // Method Overriding
        return "Student Applicant eligible to apply for Degree College Admissions.";
    }

    public String toDataString() { // Serialization
        return getId() + "|" + getName() + "|" + getEmail() + "|" + getPassword() + "|" + getPhoneNumber();
    }
}
```

### Full Source Code for Project Report
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

## 4. Class 3: Admin.java (Concrete Subclass - Inheritance)

* **Type:** `public class Admin extends User`
* **OOP Concepts:** **Inheritance**, **Constructor Chaining (`super`)**, **Method Overriding**.
* **Role:** Models an admission committee officer with authority to verify submitted student credentials and allocate engineering seats.

### Code for PPT Presentation (Key Highlights)
```java
public class Admin extends User {
    public Admin(String id, String name, String email, String password, String phone) {
        super(id, name, email, password, phone, "ADMIN");
    }

    @Override
    public String getRoleDescription() {
        return "System Administrator with full authority to review, approve, or reject admission applications.";
    }

    public String toDataString() {
        return getId() + "|" + getName() + "|" + getEmail() + "|" + getPassword() + "|" + getPhoneNumber();
    }
}
```

### Full Source Code for Project Report
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

## 5. Class 4: Document.java (Entity Model - Encapsulation)

* **Type:** `public class Document`
* **OOP Concepts:** **Encapsulation**, **POJO Modeling**, **Static Utility Methods**.
* **Role:** Stores metadata and disk references for candidate certificates (Aadhaar, SSC, HSC, CET Scorecard).

### Code for PPT Presentation (Key Highlights)
```java
public class Document {
    private String docType, docTitle, originalFileName, storedFileName, fileType;
    private long fileSize;

    public static String getTitleForType(String docType) {
        switch (docType.toLowerCase()) {
            case "aadhaar": return "Aadhaar Card";
            case "ssc": return "SSC (10th) Marksheet";
            case "hsc": return "HSC (12th) Marksheet";
            case "cet": return "CET Marksheet / Scorecard";
            default: return "Supporting Document";
        }
    }

    public String getFormattedFileSize() {
        return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
    }
}
```

### Full Source Code for Project Report
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

## 6. Class 5: Application.java (Domain Entity - Overloading & Collections)

* **Type:** `public class Application`
* **OOP Concepts:** **Encapsulation**, **Constructor Overloading (Compile-time Polymorphism)**, **Composition (`List<String>`, `Map<String, String>`)**.
* **Role:** Core admission aggregate root holding candidate marks, prioritized engineering choices, document maps, and allotment lifecycle states.

### Code for PPT Presentation (Key Highlights)
```java
public class Application {
    private String applicationId, studentEmail, studentName, category, status;
    private double sscMarks, hscMarks, cetScore;
    private List<String> branchPreferences;         // Priority order 1 to 6
    private Map<String, String> uploadedDocuments;   // docType -> storedFileName
    private String allocatedBranch, allocationStatus, verificationStatus;

    public Application() { /* default initialization */ }
    public Application(String id, String email, String name, double ssc, double hsc, double cet, ...) {
        // Overloaded constructor supporting backwards compatibility and 16-field CAP records
    }
    public String toDataString() { /* Serializes 16 fields with pipe delimiters */ }
}
```

### Full Source Code for Project Report
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

        return applicationId + "|" +
               studentEmail + "|" +
               studentName + "|" +
               sscMarks + "|" +
               hscMarks + "|" +
               cetScore + "|" +
               (courseChoice != null ? courseChoice : "") + "|" +
               category + "|" +
               status + "|" +
               (remarks != null ? remarks : "") + "|" +
               prefsStr + "|" +
               (allocatedBranch != null ? allocatedBranch : "") + "|" +
               (allocationStatus != null ? allocationStatus : "Pending Allocation") + "|" +
               (verificationStatus != null ? verificationStatus : "Pending Verification") + "|" +
               docsSb.toString() + "|" +
               (allocationRemarks != null ? allocationRemarks : "");
    }

    public static Application fromDataString(String dataLine) {
        if (dataLine == null || dataLine.trim().isEmpty() || dataLine.startsWith("#")) {
            return null;
        }

        String[] parts = dataLine.split("\\|", -1);

        if (parts.length >= 10) {
            String appId = parts[0].trim();
            String email = parts[1].trim();
            String name = parts[2].trim();
            double ssc = 0.0, hsc = 0.0, cet = 0.0;
            try {
                ssc = Double.parseDouble(parts[3].trim());
                hsc = Double.parseDouble(parts[4].trim());
                cet = Double.parseDouble(parts[5].trim());
            } catch (NumberFormatException e) {
            }
            String course = parts[6].trim();
            String cat = parts[7].trim();
            String status = parts[8].trim();
            String remarks = parts[9].trim();

            List<String> prefs = new ArrayList<>();
            String allocBranch = "";
            String allocStatus = "Pending Allocation";
            String verifStatus = "Pending Verification";
            Map<String, String> docs = new LinkedHashMap<>();
            String allocRemarks = "";

            if (parts.length > 10 && !parts[10].trim().isEmpty()) {
                String[] pArray = parts[10].trim().split(";");
                for (String p : pArray) {
                    if (!p.trim().isEmpty()) {
                        prefs.add(p.trim());
                    }
                }
            } else if (!course.isEmpty()) {
                prefs.add(course);
            }

            if (parts.length > 11) {
                allocBranch = parts[11].trim();
            } else if ("Approved".equalsIgnoreCase(status) || "Accepted".equalsIgnoreCase(status)) {
                allocBranch = course;
            }

            if (parts.length > 12 && !parts[12].trim().isEmpty()) {
                allocStatus = parts[12].trim();
            } else if ("Approved".equalsIgnoreCase(status) || "Accepted".equalsIgnoreCase(status)) {
                allocStatus = "Accepted";
            } else if ("Rejected".equalsIgnoreCase(status)) {
                allocStatus = "Rejected";
            }

            if (parts.length > 13 && !parts[13].trim().isEmpty()) {
                verifStatus = parts[13].trim();
            } else if ("Approved".equalsIgnoreCase(status) || "Accepted".equalsIgnoreCase(status)) {
                verifStatus = "Documents Verified";
            }

            if (parts.length > 14 && !parts[14].trim().isEmpty()) {
                String[] docPairs = parts[14].trim().split(";");
                for (String pair : docPairs) {
                    int colonIdx = pair.indexOf(':');
                    if (colonIdx > 0 && colonIdx < pair.length() - 1) {
                        String type = pair.substring(0, colonIdx).trim();
                        String fileName = pair.substring(colonIdx + 1).trim();
                        docs.put(type, fileName);
                    }
                }
            }

            if (parts.length > 15) {
                allocRemarks = parts[15].trim();
            }

            return new Application(appId, email, name, ssc, hsc, cet, course, cat, status, remarks,
                                   prefs, allocBranch, allocStatus, verifStatus, docs, allocRemarks);
        }

        return null;
    }

    public void addUploadedDocument(String docType, String fileName) {
        if (uploadedDocuments == null) {
            uploadedDocuments = new LinkedHashMap<>();
        }
        uploadedDocuments.put(docType.trim().toLowerCase(), fileName.trim());
    }

    public String getUploadedDocument(String docType) {
        if (uploadedDocuments == null || docType == null) return null;
        return uploadedDocuments.get(docType.trim().toLowerCase());
    }

    public boolean hasDocument(String docType) {
        if (uploadedDocuments == null || docType == null) return false;
        return uploadedDocuments.containsKey(docType.trim().toLowerCase());
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public double getSscMarks() {
        return sscMarks;
    }

    public void setSscMarks(double sscMarks) {
        this.sscMarks = sscMarks;
    }

    public double getHscMarks() {
        return hscMarks;
    }

    public void setHscMarks(double hscMarks) {
        this.hscMarks = hscMarks;
    }

    public double getCetScore() {
        return cetScore;
    }

    public void setCetScore(double cetScore) {
        this.cetScore = cetScore;
    }

    public String getCourseChoice() {
        return courseChoice;
    }

    public void setCourseChoice(String courseChoice) {
        this.courseChoice = courseChoice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<String> getBranchPreferences() {
        return branchPreferences;
    }

    public void setBranchPreferences(List<String> branchPreferences) {
        this.branchPreferences = branchPreferences;
    }

    public String getAllocatedBranch() {
        return allocatedBranch;
    }

    public void setAllocatedBranch(String allocatedBranch) {
        this.allocatedBranch = allocatedBranch;
    }

    public String getAllocationStatus() {
        return allocationStatus;
    }

    public void setAllocationStatus(String allocationStatus) {
        this.allocationStatus = allocationStatus;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Map<String, String> getUploadedDocuments() {
        return uploadedDocuments;
    }

    public void setUploadedDocuments(Map<String, String> uploadedDocuments) {
        this.uploadedDocuments = uploadedDocuments;
    }

    public String getAllocationRemarks() {
        return allocationRemarks;
    }

    public void setAllocationRemarks(String allocationRemarks) {
        this.allocationRemarks = allocationRemarks;
    }
}
```

---

## 7. Class 6: AdmissionSystem.java (Final Service Controller & File I/O)

* **Type:** `public final class AdmissionSystem`
* **OOP Concepts:** **Final Class**, **Thread Synchronization (`synchronized`)**, **Collections Framework (`ArrayList`)**, **Java File I/O (`BufferedReader`/`BufferedWriter`)**.
* **Role:** Central business controller managing collections, persistent text file updates, candidate document validation, and branch allocation decisions.

### Code for PPT Presentation (Key Highlights)
```java
public final class AdmissionSystem {
    private ArrayList<Student> studentList = new ArrayList<>();
    private ArrayList<Application> applicationList = new ArrayList<>();

    public synchronized Application submitApplication(String email, String name, double ssc, double hsc, 
                                                      double cet, String course, String cat, List<String> prefs) {
        // Overloaded submission supporting prioritized preferences
    }

    public synchronized boolean allocateBranch(String appId, String branch, String remarks) {
        // Committee merit allocation
    }

    public synchronized boolean respondToAllocation(String appId, String decision, String remarks) {
        // Candidate Accept or Reject response handling
    }
}
```

### Full Source Code for Project Report
```java
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class AdmissionSystem {

    private ArrayList<Student> studentList;
    private ArrayList<Admin> adminList;
    private ArrayList<Application> applicationList;

    private String studentsFilePath;
    private String adminFilePath;
    private String applicationsFilePath;
    private String documentsDirPath;

    public AdmissionSystem() {
        studentList = new ArrayList<>();
        adminList = new ArrayList<>();
        applicationList = new ArrayList<>();

        resolveFilePaths();

        loadAdminsFromFile();
        loadStudentsFromFile();
        loadApplicationsFromFile();

        System.out.println("[AdmissionSystem] System initialized successfully.");
        System.out.println("[AdmissionSystem] Loaded " + studentList.size() + " students, " +
                           adminList.size() + " admins, " +
                           applicationList.size() + " applications.");
    }

    private void resolveFilePaths() {
        String[] candidates = {
            "data",
            "CollegeAdmissionPortal/data",
            "../data",
            "../../data"
        };

        File resolvedDataDir = null;
        for (String c : candidates) {
            File dir = new File(c);
            if (new File(dir, "students.txt").exists() || new File(dir, "admin.txt").exists()) {
                resolvedDataDir = dir;
                break;
            }
        }

        if (resolvedDataDir == null) {
            for (String c : candidates) {
                File dir = new File(c);
                if (dir.exists() && dir.isDirectory()) {
                    resolvedDataDir = dir;
                    break;
                }
            }
        }

        if (resolvedDataDir == null) {
            try {
                File codeLoc = new File(AdmissionSystem.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                File parent = codeLoc.isDirectory() ? codeLoc : codeLoc.getParentFile();
                File d1 = new File(parent, "data");
                if (d1.exists()) {
                    resolvedDataDir = d1;
                } else if (parent.getParentFile() != null) {
                    File d2 = new File(parent.getParentFile(), "data");
                    if (d2.exists()) {
                        resolvedDataDir = d2;
                    } else {
                        File d3 = new File(parent.getParentFile(), "CollegeAdmissionPortal/data");
                        if (d3.exists()) {
                            resolvedDataDir = d3;
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }

        if (resolvedDataDir == null) {
            resolvedDataDir = new File("data");
        }

        studentsFilePath = new File(resolvedDataDir, "students.txt").getPath();
        adminFilePath = new File(resolvedDataDir, "admin.txt").getPath();
        applicationsFilePath = new File(resolvedDataDir, "applications.txt").getPath();
        documentsDirPath = new File(resolvedDataDir, "documents").getPath();

        File docDir = new File(documentsDirPath);
        if (!docDir.exists()) {
            docDir.mkdirs();
        }
    }

    public void loadStudentsFromFile() {
        studentList.clear();
        File file = new File(studentsFilePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Student student = Student.fromDataString(line);
                if (student != null) {
                    studentList.add(student);
                }
            }
        } catch (IOException e) {
            System.err.println("[AdmissionSystem] Error reading students file: " + e.getMessage());
        }
    }

    public void loadAdminsFromFile() {
        adminList.clear();
        File file = new File(adminFilePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Admin admin = Admin.fromDataString(line);
                if (admin != null) {
                    adminList.add(admin);
                }
            }
        } catch (IOException e) {
            System.err.println("[AdmissionSystem] Error reading admin file: " + e.getMessage());
        }
    }

    public void loadApplicationsFromFile() {
        applicationList.clear();
        File file = new File(applicationsFilePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Application app = Application.fromDataString(line);
                if (app != null) {
                    applicationList.add(app);
                }
            }
        } catch (IOException e) {
            System.err.println("[AdmissionSystem] Error reading applications file: " + e.getMessage());
        }
    }

    public synchronized void saveStudentsToFile() {
        try {
            File file = new File(studentsFilePath);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                writer.write("# Format: StudentID|FullName|Email|Password|PhoneNumber");
                writer.newLine();

                for (Student student : studentList) {
                    writer.write(student.toDataString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("[AdmissionSystem] Error saving students: " + e.getMessage());
        }
    }

    public synchronized void saveApplicationsToFile() {
        try {
            File file = new File(applicationsFilePath);
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
                writer.write("# Format: ApplicationID|StudentEmail|StudentName|SSCMarks|HSCMarks|CETScore|CourseChoice|Category|Status|Remarks|BranchPreferences|AllocatedBranch|AllocationStatus|VerificationStatus|UploadedDocuments|AllocationRemarks");
                writer.newLine();

                for (Application app : applicationList) {
                    writer.write(app.toDataString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("[AdmissionSystem] Error saving applications: " + e.getMessage());
        }
    }

    public synchronized boolean registerStudent(String name, String email, String password, String phone) {
        for (Student s : studentList) {
            if (s.getEmail().equalsIgnoreCase(email.trim())) {
                System.out.println("[AdmissionSystem] Registration failed: Email already exists -> " + email);
                return false;
            }
        }

        String newId = "STU" + (100 + studentList.size() + 1);
        Student newStudent = new Student(newId, name.trim(), email.trim(), password.trim(), phone.trim());
        studentList.add(newStudent);
        saveStudentsToFile();

        System.out.println("[AdmissionSystem] Student registered successfully: " + newStudent.getName() + " (" + newId + ")");
        return true;
    }

    public Student authenticateStudent(String email, String password) {
        if (email == null || password == null) return null;
        for (Student s : studentList) {
            if (s.getEmail().equalsIgnoreCase(email.trim())) {
                if (s.getPassword().equals(password.trim())) {
                    return s;
                }
            }
        }
        return null;
    }

    public Admin authenticateAdmin(String email, String password) {
        if (email == null || password == null) return null;
        for (Admin a : adminList) {
            if (a.getEmail().equalsIgnoreCase(email.trim())) {
                if (a.getPassword().equals(password.trim())) {
                    return a;
                }
            }
        }
        return null;
    }

    public Student getStudentByEmail(String email) {
        if (email == null) return null;
        for (Student s : studentList) {
            if (s.getEmail().equalsIgnoreCase(email.trim())) {
                return s;
            }
        }
        return null;
    }

    public synchronized Application submitApplication(String studentEmail, String studentName,
                                                     double sscMarks, double hscMarks, double cetScore,
                                                     String courseChoice, String category) {
        List<String> prefs = new ArrayList<>();
        if (courseChoice != null && !courseChoice.trim().isEmpty()) {
            prefs.add(courseChoice.trim());
        }
        return submitApplication(studentEmail, studentName, sscMarks, hscMarks, cetScore,
                                 courseChoice, category, prefs);
    }

    public synchronized Application submitApplication(String studentEmail, String studentName,
                                                     double sscMarks, double hscMarks, double cetScore,
                                                     String courseChoice, String category,
                                                     List<String> branchPreferences) {
        if ((courseChoice == null || courseChoice.trim().isEmpty()) && branchPreferences != null && !branchPreferences.isEmpty()) {
            courseChoice = branchPreferences.get(0);
        }

        for (Application existing : applicationList) {
            if (existing.getStudentEmail().equalsIgnoreCase(studentEmail.trim())) {
                existing.setStudentName(studentName.trim());
                existing.setSscMarks(sscMarks);
                existing.setHscMarks(hscMarks);
                existing.setCetScore(cetScore);
                existing.setCourseChoice(courseChoice != null ? courseChoice.trim() : "");
                existing.setCategory(category.trim());
                if (branchPreferences != null && !branchPreferences.isEmpty()) {
                    existing.setBranchPreferences(new ArrayList<>(branchPreferences));
                }
                existing.setStatus("Pending");
                existing.setAllocationStatus("Pending Allocation");
                existing.setVerificationStatus("Pending Verification");
                existing.setRemarks("Application submitted. Scrutiny and verification in progress.");
                saveApplicationsToFile();
                return existing;
            }
        }

        String newAppId = "APP" + (1000 + applicationList.size() + 1);

        List<String> prefs = (branchPreferences != null && !branchPreferences.isEmpty())
                             ? new ArrayList<>(branchPreferences)
                             : new ArrayList<>();
        if (prefs.isEmpty() && courseChoice != null && !courseChoice.trim().isEmpty()) {
            prefs.add(courseChoice.trim());
        }

        Application newApp = new Application(newAppId, studentEmail.trim(), studentName.trim(),
                                             sscMarks, hscMarks, cetScore,
                                             courseChoice != null ? courseChoice.trim() : "",
                                             category.trim(), "Pending",
                                             "Documents under scrutiny by CET committee.",
                                             prefs, "", "Pending Allocation",
                                             "Pending Verification", null, "");

        applicationList.add(newApp);
        saveApplicationsToFile();

        System.out.println("[AdmissionSystem] New Application submitted: " + newAppId + " for " + studentEmail);
        return newApp;
    }

    public synchronized boolean saveUploadedDocument(String appId, String docType,
                                                    String originalFileName, byte[] fileBytes) {
        Application app = getApplicationById(appId);
        if (app == null) {
            app = getApplicationByEmail(appId);
        }
        if (app == null) {
            System.err.println("[AdmissionSystem] Document upload failed: Application not found -> " + appId);
            return false;
        }

        if (fileBytes == null || fileBytes.length == 0) {
            System.err.println("[AdmissionSystem] Document upload failed: Empty file payload");
            return false;
        }

        if (fileBytes.length > 5 * 1024 * 1024) {
            System.err.println("[AdmissionSystem] Document upload failed: File exceeds 5MB limit");
            return false;
        }

        String cleanName = new File(originalFileName).getName();
        String ext = "";
        int dotIdx = cleanName.lastIndexOf('.');
        if (dotIdx >= 0) {
            ext = cleanName.substring(dotIdx).toLowerCase();
        }
        if (!ext.equals(".pdf") && !ext.equals(".jpg") && !ext.equals(".jpeg") && !ext.equals(".png")) {
            System.err.println("[AdmissionSystem] Document upload failed: Unsupported file extension -> " + ext);
            return false;
        }

        File appDocDir = new File(documentsDirPath, app.getApplicationId());
        if (!appDocDir.exists()) {
            appDocDir.mkdirs();
        }

        String storedFileName = docType.trim().toLowerCase() + "_" + cleanName.replaceAll("[^a-zA-Z0-9._-]", "_");
        File targetFile = new File(appDocDir, storedFileName);

        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.write(fileBytes);
        } catch (IOException e) {
            System.err.println("[AdmissionSystem] Error saving document to disk: " + e.getMessage());
            return false;
        }

        app.addUploadedDocument(docType.trim().toLowerCase(), storedFileName);
        saveApplicationsToFile();

        System.out.println("[AdmissionSystem] Saved document: " + docType + " (" + cleanName + ") for " + app.getApplicationId());
        return true;
    }

    public File getDocumentFile(String appId, String docType) {
        Application app = getApplicationById(appId);
        if (app == null) {
            app = getApplicationByEmail(appId);
        }
        if (app == null || docType == null) return null;

        String storedName = app.getUploadedDocument(docType);
        if (storedName == null || storedName.isEmpty()) return null;

        File docFile = new File(new File(documentsDirPath, app.getApplicationId()), storedName);
        if (docFile.exists() && docFile.isFile()) {
            return docFile;
        }
        return null;
    }

    public synchronized boolean verifyApplicationDocuments(String appId, String verificationStatus, String remarks) {
        Application app = getApplicationById(appId);
        if (app != null) {
            app.setVerificationStatus(verificationStatus.trim());
            if (remarks != null && !remarks.trim().isEmpty()) {
                app.setRemarks(remarks.trim());
            }
            saveApplicationsToFile();
            System.out.println("[AdmissionSystem] Application " + appId + " verification status: " + verificationStatus);
            return true;
        }
        return false;
    }

    public synchronized boolean allocateBranch(String appId, String branch, String remarks) {
        Application app = getApplicationById(appId);
        if (app != null) {
            app.setAllocatedBranch(branch.trim());
            app.setAllocationStatus("Awaiting Student Acceptance");
            app.setStatus("Branch Allocated");
            if (remarks != null && !remarks.trim().isEmpty()) {
                app.setAllocationRemarks(remarks.trim());
                app.setRemarks(remarks.trim());
            } else {
                app.setAllocationRemarks("Seat allotted in " + branch.trim() + ". Awaiting student acceptance.");
                app.setRemarks("Seat allotted in " + branch.trim() + ". Awaiting student acceptance.");
            }
            saveApplicationsToFile();
            System.out.println("[AdmissionSystem] Branch allocated for " + appId + ": " + branch);
            return true;
        }
        return false;
    }

    public synchronized boolean respondToAllocation(String appId, String decision, String studentRemarks) {
        Application app = getApplicationById(appId);
        if (app == null) {
            app = getApplicationByEmail(appId);
        }
        if (app != null) {
            if ("Accept".equalsIgnoreCase(decision)) {
                app.setAllocationStatus("Accepted");
                app.setStatus("Admission Confirmed");
                String note = "Candidate accepted the allocated branch (" + app.getAllocatedBranch() + "). Admission confirmed!";
                app.setRemarks(note);
            } else if ("Reject".equalsIgnoreCase(decision)) {
                app.setAllocationStatus("Allocation Rejected by Student");
                app.setStatus("Allocation Rejected - Pending Reallocation");
                String reason = (studentRemarks != null && !studentRemarks.trim().isEmpty())
                                ? studentRemarks.trim()
                                : "Seeking reallocation in subsequent round";
                app.setRemarks("Allocation rejected by candidate: " + reason + ". Returned to Admission Committee for review.");
            } else {
                return false;
            }

            saveApplicationsToFile();
            System.out.println("[AdmissionSystem] Student response for " + app.getApplicationId() + ": " + decision);
            return true;
        }
        return false;
    }

    public Application getApplicationByEmail(String email) {
        if (email == null) return null;
        for (Application app : applicationList) {
            if (app.getStudentEmail().equalsIgnoreCase(email.trim())) {
                return app;
            }
        }
        return null;
    }

    public Application getApplicationById(String appId) {
        if (appId == null) return null;
        for (Application app : applicationList) {
            if (app.getApplicationId().equalsIgnoreCase(appId.trim())) {
                return app;
            }
        }
        return null;
    }

    public ArrayList<Application> getAllApplications() {
        return applicationList;
    }

    public synchronized boolean updateApplicationStatus(String appId, String newStatus, String remarks) {
        Application app = getApplicationById(appId);
        if (app != null) {
            app.setStatus(newStatus.trim());
            if ("Approved".equalsIgnoreCase(newStatus)) {
                app.setAllocationStatus("Accepted");
                if (app.getAllocatedBranch() == null || app.getAllocatedBranch().isEmpty()) {
                    app.setAllocatedBranch(app.getCourseChoice());
                }
            } else if ("Rejected".equalsIgnoreCase(newStatus)) {
                app.setAllocationStatus("Rejected");
            }
            if (remarks != null && !remarks.trim().isEmpty()) {
                app.setRemarks(remarks.trim());
            }
            saveApplicationsToFile();
            System.out.println("[AdmissionSystem] Application " + appId + " status changed to: " + newStatus);
            return true;
        }
        return false;
    }

    public ArrayList<Student> getStudentList() {
        return studentList;
    }

    public ArrayList<Admin> getAdminList() {
        return adminList;
    }
}
```

---

## 8. Class 7: Main.java (Entry Point & Embedded HTTP Server)

* **Type:** `public class Main`
* **OOP Concepts:** **Static Nested Classes**, **Interface Implementation (`implements HttpHandler`)**, **Driver Pattern**.
* **Role:** System bootstrapper. Starts native `HttpServer` on port 8080 and hosts all REST APIs and static file routes.

### Code for PPT Presentation (Key Highlights)
```java
public class Main {
    public static void main(String[] args) throws Exception {
        AdmissionSystem admissionSystem = new AdmissionSystem();
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new StaticFileHandler());
        server.createContext("/api/register", new RegisterHandler());
        server.createContext("/api/apply", new ApplyHandler());
        server.createContext("/api/admin/allocate-branch", new AdminAllocateBranchHandler());

        server.start();
        System.out.println("Server running at http://localhost:8080/");
    }
}
```

### Full Source Code for Project Report
```java
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
    private static AdmissionSystem admissionSystem;
    private static String frontendPath;

    public static void main(String[] args) {
        try {
            admissionSystem = new AdmissionSystem();
            locateFrontendFolder();

            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            server.createContext("/", new StaticFileHandler());
            server.createContext("/api/register", new RegisterHandler());
            server.createContext("/api/login", new LoginHandler());
            server.createContext("/api/apply", new ApplyHandler());
            server.createContext("/api/status", new StatusHandler());
            server.createContext("/api/upload-document", new UploadDocumentHandler());
            server.createContext("/api/document/view", new DocumentViewHandler(false));
            server.createContext("/api/document/download", new DocumentViewHandler(true));
            server.createContext("/api/student/allocation-response", new StudentAllocationResponseHandler());
            server.createContext("/api/admin-login", new AdminLoginHandler());
            server.createContext("/api/admin/applications", new AdminApplicationsHandler());
            server.createContext("/api/admin/action", new AdminActionHandler());
            server.createContext("/api/admin/verify-documents", new AdminVerifyDocumentsHandler());
            server.createContext("/api/admin/allocate-branch", new AdminAllocateBranchHandler());

            server.setExecutor(null);
            server.start();

            System.out.println("Web server started at http://localhost:" + PORT + "/");

            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void locateFrontendFolder() {
        String[] candidates = {
            "frontend",
            "CollegeAdmissionPortal/frontend",
            "../frontend",
            "../../frontend"
        };

        for (String candidate : candidates) {
            File dir = new File(candidate);
            if (new File(dir, "index.html").exists()) {
                frontendPath = dir.getAbsolutePath();
                return;
            }
        }

        try {
            File codeLoc = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File parent = codeLoc.isDirectory() ? codeLoc : codeLoc.getParentFile();
            File f1 = new File(parent, "frontend");
            if (new File(f1, "index.html").exists()) {
                frontendPath = f1.getAbsolutePath();
                return;
            }
            if (parent.getParentFile() != null) {
                File f2 = new File(parent.getParentFile(), "frontend");
                if (new File(f2, "index.html").exists()) {
                    frontendPath = f2.getAbsolutePath();
                    return;
                }
                File f3 = new File(parent.getParentFile(), "CollegeAdmissionPortal/frontend");
                if (new File(f3, "index.html").exists()) {
                    frontendPath = f3.getAbsolutePath();
                    return;
                }
            }
        } catch (Exception ignored) {
        }

        frontendPath = "frontend";
    }

    static class StaticFileHandler implements HttpHandler {
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
                String fileName = file.getName().toLowerCase();
                String contentType = "text/plain";
                if (fileName.endsWith(".html")) {
                    contentType = "text/html; charset=UTF-8";
                } else if (fileName.endsWith(".css")) {
                    contentType = "text/css; charset=UTF-8";
                } else if (fileName.endsWith(".js")) {
                    contentType = "application/javascript; charset=UTF-8";
                } else if (fileName.endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (fileName.endsWith(".pdf")) {
                    contentType = "application/pdf";
                } else if (fileName.endsWith(".svg")) {
                    contentType = "image/svg+xml";
                } else if (fileName.endsWith(".ico")) {
                    contentType = "image/x-icon";
                } else if (fileName.endsWith(".json")) {
                    contentType = "application/json; charset=UTF-8";
                }

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
    }

    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String name = params.getOrDefault("name", "");
                String email = params.getOrDefault("email", "");
                String password = params.getOrDefault("password", "");
                String phone = params.getOrDefault("phone", "");

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"All fields are required!\"}");
                    return;
                }

                boolean success = admissionSystem.registerStudent(name, email, password, phone);
                if (success) {
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Registration successful! You can now login.\"}");
                } else {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"This email is already registered! Please login.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String email = params.getOrDefault("email", "");
                String password = params.getOrDefault("password", "");

                Student student = admissionSystem.authenticateStudent(email, password);
                if (student != null) {
                    String json = String.format(
                        "{\"success\":true,\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"message\":\"Login successful!\"}",
                        escapeJson(student.getId()),
                        escapeJson(student.getName()),
                        escapeJson(student.getEmail()),
                        escapeJson(student.getPhoneNumber())
                    );
                    sendJsonResponse(exchange, 200, json);
                } else {
                    sendJsonResponse(exchange, 401, "{\"success\":false,\"message\":\"Invalid email or password! Please check credentials.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    static class ApplyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String email = params.getOrDefault("email", "");
                String name = params.getOrDefault("name", "");
                String sscStr = params.getOrDefault("sscMarks", "0");
                String hscStr = params.getOrDefault("hscMarks", "0");
                String cetStr = params.getOrDefault("cetScore", "0");
                String course = params.getOrDefault("course", "");
                String category = params.getOrDefault("category", "General");
                String preferencesRaw = params.getOrDefault("preferences", "");

                double ssc = 0.0, hsc = 0.0, cet = 0.0;
                try {
                    ssc = Double.parseDouble(sscStr);
                    hsc = Double.parseDouble(hscStr);
                    cet = Double.parseDouble(cetStr);
                } catch (NumberFormatException e) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Please enter valid numerical marks!\"}");
                    return;
                }

                List<String> branchPreferences = new ArrayList<>();
                if (!preferencesRaw.isEmpty()) {
                    String[] items = preferencesRaw.split("[,;]");
                    for (String item : items) {
                        String clean = item.trim();
                        if (!clean.isEmpty() && !branchPreferences.contains(clean)) {
                            branchPreferences.add(clean);
                        }
                    }
                }
                if (branchPreferences.isEmpty() && !course.isEmpty()) {
                    branchPreferences.add(course.trim());
                }

                Application app = admissionSystem.submitApplication(
                    email, name, ssc, hsc, cet, course, category, branchPreferences
                );

                String json = String.format(
                    "{\"success\":true,\"appId\":\"%s\",\"message\":\"Application submitted successfully!\"}",
                    escapeJson(app.getApplicationId())
                );
                sendJsonResponse(exchange, 200, json);
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    static class UploadDocumentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> queryParams = parseQueryString(exchange.getRequestURI().getQuery());

                String appId = queryParams.getOrDefault("appId", "");
                if (appId.isEmpty()) {
                    appId = queryParams.getOrDefault("email", "");
                }
                String docType = queryParams.getOrDefault("docType", "");
                String fileName = queryParams.getOrDefault("fileName", "");

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
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Missing required parameters: appId, docType, fileName\"}");
                    return;
                }

                byte[] fileBytes = readAllBytes(exchange.getRequestBody());

                if (fileBytes.length == 0) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Uploaded file is empty!\"}");
                    return;
                }

                boolean saved = admissionSystem.saveUploadedDocument(appId, docType, fileName, fileBytes);
                if (saved) {
                    sendJsonResponse(exchange, 200, String.format(
                        "{\"success\":true,\"message\":\"Document '%s' uploaded successfully!\",\"docType\":\"%s\",\"fileName\":\"%s\"}",
                        escapeJson(Document.getTitleForType(docType)),
                        escapeJson(docType),
                        escapeJson(fileName)
                    ));
                } else {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Failed to upload document. Verify application ID and allowed formats (PDF, JPG, PNG under 5MB).\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    static class DocumentViewHandler implements HttpHandler {
        private final boolean isDownload;

        public DocumentViewHandler(boolean isDownload) {
            this.isDownload = isDownload;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> queryParams = parseQueryString(exchange.getRequestURI().getQuery());
                String appId = queryParams.getOrDefault("appId", "");
                if (appId.isEmpty()) {
                    appId = queryParams.getOrDefault("email", "");
                }
                String docType = queryParams.getOrDefault("docType", "");

                File docFile = admissionSystem.getDocumentFile(appId, docType);
                if (docFile != null && docFile.exists()) {
                    String name = docFile.getName();
                    String contentType = "application/octet-stream";
                    if (name.toLowerCase().endsWith(".pdf")) {
                        contentType = "application/pdf";
                    } else if (name.toLowerCase().endsWith(".png")) {
                        contentType = "image/png";
                    } else if (name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg")) {
                        contentType = "image/jpeg";
                    }

                    String disposition = isDownload ? "attachment" : "inline";
                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    exchange.getResponseHeaders().set("Content-Disposition", disposition + "; filename=\"" + name + "\"");

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
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    static class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> queryParams = parseQueryString(exchange.getRequestURI().getQuery());
                String email = queryParams.getOrDefault("email", "");
                String appId = queryParams.getOrDefault("appId", "");

                Application app = null;
                if (!appId.isEmpty()) {
                    app = admissionSystem.getApplicationById(appId);
                }
                if (app == null && !email.isEmpty()) {
                    app = admissionSystem.getApplicationByEmail(email);
                }

                if (app != null) {
                    Student student = admissionSystem.getStudentByEmail(app.getStudentEmail());
                    String phone = student != null ? student.getPhoneNumber() : "";

                    String json = formatApplicationJson(app, phone);
                    sendJsonResponse(exchange, 200, json);
                } else {
                    sendJsonResponse(exchange, 200, "{\"found\":false,\"message\":\"No application found for this candidate.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    static class StudentAllocationResponseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String appId = params.getOrDefault("appId", "");
                String decision = params.getOrDefault("action", "");
                String remarks = params.getOrDefault("remarks", "");

                if (appId.isEmpty() || decision.isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Application ID and action are required!\"}");
                    return;
                }

                boolean success = admissionSystem.respondToAllocation(appId, decision, remarks);
                if (success) {
                    if ("Accept".equalsIgnoreCase(decision)) {
                        sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Congratulations! You have accepted the allocated branch. Your admission is CONFIRMED.\"}");
                    } else {
                        sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Allocation rejected. Your application has been returned to the Admission Committee for review and reallocation.\"}");
                    }
                } else {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Failed to process allocation decision. Application not found.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    static class AdminLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String email = params.getOrDefault("email", "");
                String password = params.getOrDefault("password", "");

                Admin admin = admissionSystem.authenticateAdmin(email, password);
                if (admin != null) {
                    String json = String.format(
                        "{\"success\":true,\"id\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"message\":\"Admin authentication successful!\"}",
                        escapeJson(admin.getId()),
                        escapeJson(admin.getName()),
                        escapeJson(admin.getEmail()),
                        escapeJson(admin.getRoleDescription())
                    );
                    sendJsonResponse(exchange, 200, json);
                } else {
                    sendJsonResponse(exchange, 401, "{\"success\":false,\"message\":\"Invalid Admin credentials! Access Denied.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    static class AdminApplicationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                ArrayList<Application> allApps = admissionSystem.getAllApplications();

                StringBuilder sb = new StringBuilder();
                sb.append("[");
                for (int i = 0; i < allApps.size(); i++) {
                    Application a = allApps.get(i);
                    Student s = admissionSystem.getStudentByEmail(a.getStudentEmail());
                    String phone = s != null ? s.getPhoneNumber() : "";

                    sb.append(formatApplicationJson(a, phone));
                    if (i < allApps.size() - 1) {
                        sb.append(",");
                    }
                }
                sb.append("]");

                sendJsonResponse(exchange, 200, sb.toString());
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    static class AdminActionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String appId = params.getOrDefault("appId", "");
                String action = params.getOrDefault("action", "");
                String remarks = params.getOrDefault("remarks", "");

                boolean success = admissionSystem.updateApplicationStatus(appId, action, remarks);
                if (success) {
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Application status updated to " + escapeJson(action) + "!\"}");
                } else {
                    sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"Application ID not found.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    static class AdminVerifyDocumentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String appId = params.getOrDefault("appId", "");
                String status = params.getOrDefault("status", "Documents Verified");
                String remarks = params.getOrDefault("remarks", "");

                boolean success = admissionSystem.verifyApplicationDocuments(appId, status, remarks);
                if (success) {
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Document verification updated to " + escapeJson(status) + "!\"}");
                } else {
                    sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"Application ID not found.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    static class AdminAllocateBranchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> params = parseRequestBody(exchange);

                String appId = params.getOrDefault("appId", "");
                String branch = params.getOrDefault("branch", "");
                String remarks = params.getOrDefault("remarks", "");

                if (appId.isEmpty() || branch.isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"Application ID and branch must be provided!\"}");
                    return;
                }

                boolean success = admissionSystem.allocateBranch(appId, branch, remarks);
                if (success) {
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Branch '" + escapeJson(branch) + "' allocated successfully! Decision sent to candidate.\"}");
                } else {
                    sendJsonResponse(exchange, 404, "{\"success\":false,\"message\":\"Application ID not found.\"}");
                }
            } else {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method not allowed\"}");
            }
        }
    }

    private static String formatApplicationJson(Application app, String phone) {
        StringBuilder prefsJson = new StringBuilder();
        prefsJson.append("[");
        List<String> prefs = app.getBranchPreferences();
        for (int i = 0; i < prefs.size(); i++) {
            prefsJson.append("\"").append(escapeJson(prefs.get(i))).append("\"");
            if (i < prefs.size() - 1) prefsJson.append(",");
        }
        prefsJson.append("]");

        StringBuilder docsJson = new StringBuilder();
        docsJson.append("{");
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
            escapeJson(app.getApplicationId()),
            escapeJson(app.getStudentName()),
            escapeJson(app.getStudentEmail()),
            escapeJson(phone),
            app.getSscMarks(),
            app.getHscMarks(),
            app.getCetScore(),
            escapeJson(app.getCourseChoice()),
            escapeJson(app.getCategory()),
            escapeJson(app.getStatus()),
            escapeJson(app.getRemarks()),
            prefsJson.toString(),
            escapeJson(app.getAllocatedBranch()),
            escapeJson(app.getAllocationStatus()),
            escapeJson(app.getVerificationStatus()),
            docsJson.toString(),
            escapeJson(app.getAllocationRemarks())
        );
    }

    private static Map<String, String> parseRequestBody(HttpExchange exchange) throws IOException {
        Map<String, String> params = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            String[] pairs = body.toString().split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length >= 2) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
                    String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.name());
                    params.put(key, value);
                } else if (keyValue.length == 1) {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8.name());
                    params.put(key, "");
                }
            }
        }
        return params;
    }

    private static Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length >= 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            } else if (keyValue.length == 1) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                params.put(key, "");
            }
        }
        return params;
    }

    private static byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
```
