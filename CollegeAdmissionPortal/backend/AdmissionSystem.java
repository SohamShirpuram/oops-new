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
