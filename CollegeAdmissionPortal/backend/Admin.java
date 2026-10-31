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
