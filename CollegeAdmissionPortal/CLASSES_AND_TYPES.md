# College Admission Portal - Pure JDK Classes and Types

This reference lists all 8 pure Core Java classes that power the **College Admission Portal** (Orchid College of Engineering & Technology).

---

## Classes and Types Reference

| # | Class Name | Category | Primary Responsibility | OOP Concepts Demonstrated |
|---|---|---|---|---|
| 1 | `User.java` | Abstract Class | Base model for user authentication with ID, name, email, and password. | **Abstraction, Encapsulation, Inheritance** |
| 2 | `Student.java` | Concrete Subclass | Represents applicant students with phone number and serialization. | **Inheritance, Polymorphism** (`getRoleDescription()`) |
| 3 | `Admin.java` | Concrete Subclass | Represents admission officers with system authority. | **Inheritance, Polymorphism** (`getRoleDescription()`) |
| 4 | `Document.java` | Helper Entity | Represents uploaded marksheet and certificate metadata. | **Encapsulation** |
| 5 | `Application.java` | Domain Entity | Encapsulates admission applications, academic marks, branch preferences, and allocation status. | **Encapsulation, State Management** |
| 6 | `FileStorage.java` | Persistence Manager | Reads and writes plain-text records for students, admins, and applications using standard `BufferedReader` / `BufferedWriter`. | **Encapsulation, Resource Management** |
| 7 | `AdmissionSystem.java` | Central Controller | Coordinates admission business rules: student registration, credential verification, application processing, branch allocation, and document verification. | **Separation of Concerns, High Cohesion** |
| 8 | `Main.java` | Web Server Entry Point | Pure JDK HTTP server using `com.sun.net.httpserver.HttpServer`. Serves frontend static web files and routes form submissions directly. | **Entry Point, Pure JDK Web Routing** |

---

## Technology Stack
- **Language**: Pure Core Java (JDK 8+)
- **HTTP Web Server**: Built-in `com.sun.net.httpserver.HttpServer`
- **Data Persistence**: Plain text flat files (`data/students.txt`, `data/admin.txt`, `data/applications.txt`)
- **Frontend**: Vanilla HTML5, CSS3, JavaScript (Fetch API)
- **External Dependencies**: **None** (Zero third-party JARs, zero external frameworks)
