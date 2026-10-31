# College Admission Portal - Classes and Types Specification

This document details all **20 Java classes** implemented in the project, categorized by their **OOP type**, **architectural role**, **inheritance relationships**, and **primary responsibilities**.

---

## 1. Quick Summary Table

| Class Name | Enclosing File | Java Class Type | Architectural Role | Interfaces / Inheritance |
| :--- | :--- | :--- | :--- | :--- |
| **`User`** | `User.java` | `public abstract class` | Base Entity / Superclass | `Object` |
| **`Student`** | `Student.java` | `public class` | Concrete Domain Model | `extends User` |
| **`Admin`** | `Admin.java` | `public class` | Concrete Domain Model | `extends User` |
| **`Document`** | `Document.java` | `public class` | Entity / Metadata Model | `Object` |
| **`Application`** | `Application.java` | `public class` | Core Domain Entity | `Object` |
| **`AdmissionSystem`** | `AdmissionSystem.java` | `public final class` | Controller & Storage Manager | `Object` |
| **`Main`** | `Main.java` | `public class` | Driver & HTTP Web Server | `Object` |
| **`StaticFileHandler`** | `Main.java` | `static class` (Nested) | Static Asset Router | `implements HttpHandler` |
| **`RegisterHandler`** | `Main.java` | `static class` (Nested) | REST API Controller | `implements HttpHandler` |
| **`LoginHandler`** | `Main.java` | `static class` (Nested) | REST API Controller | `implements HttpHandler` |
| **`ApplyHandler`** | `Main.java` | `static class` (Nested) | REST API Controller | `implements HttpHandler` |
| **`UploadDocumentHandler`** | `Main.java` | `static class` (Nested) | Binary Upload Controller | `implements HttpHandler` |
| **`DocumentViewHandler`** | `Main.java` | `static class` (Nested) | Streaming Asset Controller | `implements HttpHandler` |
| **`StatusHandler`** | `Main.java` | `static class` (Nested) | REST API Controller | `implements HttpHandler` |
| **`StudentAllocationResponseHandler`** | `Main.java` | `static class` (Nested) | REST API Controller | `implements HttpHandler` |
| **`AdminLoginHandler`** | `Main.java` | `static class` (Nested) | REST API Controller | `implements HttpHandler` |
| **`AdminApplicationsHandler`** | `Main.java` | `static class` (Nested) | REST API Controller | `implements HttpHandler` |
| **`AdminActionHandler`** | `Main.java` | `static class` (Nested) | REST API Controller | `implements HttpHandler` |
| **`AdminVerifyDocumentsHandler`** | `Main.java` | `static class` (Nested) | REST API Controller | `implements HttpHandler` |
| **`AdminAllocateBranchHandler`** | `Main.java` | `static class` (Nested) | REST API Controller | `implements HttpHandler` |

---

## 2. Top-Level Classes (Core OOP Domain)

### 2.1 `User`
* **Declaration:** `public abstract class User`
* **File:** [`backend/User.java`](file:///c:/Projects/oops%20project/CollegeAdmissionPortal/backend/User.java)
* **Type:** Abstract Base Class
* **OOP Characteristics:**
  * **Abstraction:** Defines essential user properties without allowing direct instantiation via `new User(...)`.
  * **Encapsulation:** Protects all instance fields (`id`, `name`, `email`, `password`, `phoneNumber`, `role`) with `private` access modifiers and exposed `public` getters and setters.
  * **Polymorphism:** Declares the abstract method `public abstract String getRoleDescription();` which enforces dynamic runtime method dispatching in child classes.
* **Key Methods:**
  * `getRoleDescription()` (abstract)
  * `displayUserDetails()` (concrete polymorphic display method)

---

### 2.2 `Student`
* **Declaration:** `public class Student extends User`
* **File:** [`backend/Student.java`](file:///c:/Projects/oops%20project/CollegeAdmissionPortal/backend/Student.java)
* **Type:** Concrete Derived Class (Subclass)
* **OOP Characteristics:**
  * **Inheritance:** Inherits all credential and identity fields from `User`.
  * **Constructor Chaining:** Invokes `super(id, name, email, password, phoneNumber, "STUDENT")`.
  * **Run-time Polymorphism:** Overrides `getRoleDescription()` to return candidate-specific role text.
* **Key Methods:**
  * `toDataString()`: Serializes student instance into a pipe-delimited line (`StudentID|Name|Email|Password|Phone`).
  * `fromDataString(String line)` (static): Factory method to parse stored text into a `Student` object.

---

### 2.3 `Admin`
* **Declaration:** `public class Admin extends User`
* **File:** [`backend/Admin.java`](file:///c:/Projects/oops%20project/CollegeAdmissionPortal/backend/Admin.java)
* **Type:** Concrete Derived Class (Subclass)
* **OOP Characteristics:**
  * **Inheritance:** Inherits all credential and identity fields from `User`.
  * **Constructor Chaining:** Invokes `super(id, name, email, password, phoneNumber, "ADMIN")`.
  * **Run-time Polymorphism:** Overrides `getRoleDescription()` to return administrator-specific authority description.
* **Key Methods:**
  * `toDataString()`: Serializes admin instance into a pipe-delimited line (`AdminID|Name|Email|Password|Phone`).
  * `fromDataString(String line)` (static): Factory method to reconstruct an `Admin` object from text storage.

---

### 2.4 `Document`
* **Declaration:** `public class Document`
* **File:** [`backend/Document.java`](file:///c:/Projects/oops%20project/CollegeAdmissionPortal/backend/Document.java)
* **Type:** Entity / POJO (Plain Old Java Object) Class
* **OOP Characteristics:**
  * **Encapsulation:** Holds metadata for uploaded credential proofs (`docType`, `docTitle`, `originalFileName`, `storedFileName`, `fileSize`, `fileType`).
* **Key Methods:**
  * `getTitleForType(String docType)` (static): Maps document keys (`aadhaar`, `ssc`, `hsc`, `cet`, `other`) to official titles.
  * `getFormattedFileSize()`: Converts byte counts into human-readable strings (e.g. `1.2 MB`, `450 KB`).

---

### 2.5 `Application`
* **Declaration:** `public class Application`
* **File:** [`backend/Application.java`](file:///c:/Projects/oops%20project/CollegeAdmissionPortal/backend/Application.java)
* **Type:** Core Domain Entity / Aggregate Model Class
* **OOP Characteristics:**
  * **Compile-time Polymorphism (Constructor Overloading):**
    * `Application()`: Default constructor initializing empty preferences and document maps.
    * `Application(...)`: 10-parameter constructor for backward compatibility.
    * `Application(...)`: 16-parameter constructor supporting preferences, allocations, and document mappings.
  * **Composition:** Contains a `List<String> branchPreferences` and a `Map<String, String> uploadedDocuments`.
* **Key Methods:**
  * `toDataString()`: Serializes all 16 application attributes into pipe-delimited storage.
  * `fromDataString(String line)` (static): Deserializes stored text, supporting both 10-column and 16-column formats.
  * `addUploadedDocument(String docType, String fileName)`
  * `getUploadedDocument(String docType)`
  * `hasDocument(String docType)`

---

### 2.6 `AdmissionSystem`
* **Declaration:** `public final class AdmissionSystem`
* **File:** [`backend/AdmissionSystem.java`](file:///c:/Projects/oops%20project/CollegeAdmissionPortal/backend/AdmissionSystem.java)
* **Type:** Final Service Controller & Storage Manager Class
* **OOP Characteristics:**
  * **Final Class:** Sealed from subclassing to prevent constructor `[this-escape]` warnings when loading records during instantiation.
  * **Encapsulation of Collections:** Stores private `ArrayList<Student>`, `ArrayList<Admin>`, and `ArrayList<Application>`.
  * **Thread Safety:** Methods performing I/O and state mutations are declared `synchronized`.
* **Key Methods:**
  * `resolveFilePaths()`: Searches multiple paths to bind `data/` and `documents/` directories.
  * `loadStudentsFromFile()`, `loadAdminsFromFile()`, `loadApplicationsFromFile()`
  * `saveStudentsToFile()`, `saveApplicationsToFile()`
  * `registerStudent(...)`, `authenticateStudent(...)`, `authenticateAdmin(...)`
  * `submitApplication(...)` (Overloaded for single course or multiple preferences)
  * `saveUploadedDocument(String appId, String docType, String originalName, byte[] bytes)`
  * `getDocumentFile(String appId, String docType)`
  * `verifyApplicationDocuments(...)`, `allocateBranch(...)`, `respondToAllocation(...)`

---

### 2.7 `Main`
* **Declaration:** `public class Main`
* **File:** [`backend/Main.java`](file:///c:/Projects/oops%20project/CollegeAdmissionPortal/backend/Main.java)
* **Type:** Driver / Application Entry Point Class
* **Key Characteristics:**
  * Contains `public static void main(String[] args)`.
  * Initializes `AdmissionSystem` and creates the `HttpServer` listening on port `8080`.
  * Registers all REST API contexts and the root static file handler.
  * Contains helper utilities: `locateFrontendFolder()`, `formatApplicationJson()`, `parseRequestBody()`, `parseQueryString()`, `readAllBytes()`, `sendJsonResponse()`, `escapeJson()`.

---

## 3. Static Nested Classes (`Main.*` HTTP Handlers)

All 13 handler classes are defined as `static class` inside `Main.java` and implement `com.sun.net.httpserver.HttpHandler`.

| Nested Class Name | Context Route | HTTP Method | Role & Functionality |
| :--- | :--- | :---: | :--- |
| **`StaticFileHandler`** | `/` | `GET` | Serves HTML, CSS, JS, images, and PDFs. Resolves clean routes without `.html`, handles trailing slashes, and serves `/index.html`. |
| **`RegisterHandler`** | `/api/register` | `POST` | Processes candidate registration and creates a new `Student` account. |
| **`LoginHandler`** | `/api/login` | `POST` | Authenticates candidate credentials against `studentList`. |
| **`ApplyHandler`** | `/api/apply` | `POST` | Validates marks (SSC, HSC, CET) and submits ordered branch preferences. |
| **`UploadDocumentHandler`** | `/api/upload-document` | `POST` | Validates file size (under 5 MB) and extensions, writing binary bytes to `data/documents/<appId>/`. |
| **`DocumentViewHandler`** | `/api/document/view`<br>`/api/document/download` | `GET` | Streams stored documents with `inline` (preview) or `attachment` (download) headers. |
| **`StatusHandler`** | `/api/status` | `GET` | Queries application verification, allocation, and uploaded documents for a candidate. |
| **`StudentAllocationResponseHandler`** | `/api/student/allocation-response` | `POST` | Handles candidate decision (`Accept` or `Reject`) on provisional seat allotment. |
| **`AdminLoginHandler`** | `/api/admin-login` | `POST` | Validates admin officer credentials against `adminList`. |
| **`AdminApplicationsHandler`** | `/api/admin/applications` | `GET` | Serializes and returns all submitted applications in JSON format to the admin scrutiny dashboard. |
| **`AdminActionHandler`** | `/api/admin/action` | `POST` | Performs general application approval or rejection. |
| **`AdminVerifyDocumentsHandler`** | `/api/admin/verify-documents` | `POST` | Updates candidate document verification status (`Documents Verified` / `Verification Rejected`). |
| **`AdminAllocateBranchHandler`** | `/api/admin/allocate-branch` | `POST` | Allocates an engineering branch based on candidate merit and preference rankings. |

