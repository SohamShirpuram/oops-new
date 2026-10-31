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
