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
