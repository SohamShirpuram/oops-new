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
