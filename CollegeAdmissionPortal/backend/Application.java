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
            if (count > 0)
                docsSb.append(";");
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
        if (uploadedDocuments == null || docType == null)
            return null;
        return uploadedDocuments.get(docType.trim().toLowerCase());
    }

    public boolean hasDocument(String docType) {
        if (uploadedDocuments == null || docType == null)
            return false;

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
