package presentation;

import services.RegistrationService;
import core.entities.StatisticalInfo;
import core.entities.Student;
import helpers.ConsoleInput;
import helpers.Validator;
import java.text.DecimalFormat;
import java.util.List;

public class Menu {

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0");

    private static String inputValidStudentId(RegistrationService management) {
        while (true) {
            String id = ConsoleInput.getString("Student ID: ");
            if (!Validator.isValidStudentId(id)) {
                System.out.println("Invalid student ID format.");
                continue;
            }
            if (management.getStudentById(id) != null) {
                System.out.println("Student ID already exists.");
                continue;
            }
            return id;
        }
    }

    private static String inputValidName() {
        while (true) {
            String name = ConsoleInput.getString("Name: ");
            if (Validator.isValidStudentName(name)) {
                return name;
            }
            System.out.println("Name must contain 2-20 characters.");
        }
    }

    private static String inputValidPhone() {
        while (true) {
            String phone = ConsoleInput.getString("Phone: ");
            if (Validator.isValidPhone(phone)) {
                return phone;
            }
            System.out.println("Phone number must contain exactly 10 digits.");
        }
    }

    private static String inputValidEmail() {
        while (true) {
            String email = ConsoleInput.getString("Email: ");
            if (Validator.isValidEmail(email)) {
                return email;
            }
            System.out.println("Invalid email format.");
        }
    }

    private static String inputValidMountainCode(RegistrationService management) {
        while (true) {
            String mountainCode = ConsoleInput.getString("Mountain code: ");
            if (management.getMountainDAO().isValidMountainCode(mountainCode)) {
                return mountainCode;
            }
            System.out.println("Mountain code does not exist.");
        }
    }

    public static void print(String str) {
        String[] menuItems = str.split("\\|");
        for (int i = 0; i < menuItems.length; i++) {
            if (menuItems[i].equalsIgnoreCase("Select")) {
                System.out.print(menuItems[i]);
            } else {
                System.out.println(menuItems[i]);
            }
        }
    }

    public static int getUserChoice() {
        try {
            return ConsoleInput.getIntegerNumber();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public static void launchRegistrationMenu(RegistrationService management) {
        boolean stop = false;
        while (!stop) {
            printMainMenu();
            int choice = getUserChoice();
            switch (choice) {
                case 1:
                    newRegistration(management);
                    break;
                case 2:
                    updateRegistration(management);
                    break;
                case 3:
                    displayRegisteredList(management);
                    break;
                case 4:
                    deleteRegistration(management);
                    break;
                case 5:
                    searchParticipantsByName(management);
                    break;
                case 6:
                    filterDataByCampus(management);
                    break;
                case 7:
                    displayStatisticsByMountain(management);
                    break;
                case 8:
                    saveData(management);
                    break;
                case 9:
                    stop = exitProgram(management);
                    break;
                default:
                    System.out.println("Invalid choice. Please choose from 1 to 9.");
                    break;
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n========== Hiking Adventure Registration ==========");
        System.out.println("1. New Registration");
        System.out.println("2. Update Registration Information");
        System.out.println("3. Display Registered List");
        System.out.println("4. Delete Registration Information");
        System.out.println("5. Search Participants by Name");
        System.out.println("6. Filter Data by Campus");
        System.out.println("7. Statistics by Mountain Peak");
        System.out.println("8. Save Data to File");
        System.out.println("9. Exit Program");
        System.out.print("Select an option: ");
    }

    private static void newRegistration(RegistrationService management) {
        System.out.println("\n--- New Registration ---");

        String id = inputValidStudentId(management);
        String name = inputValidName();
        String phone = inputValidPhone();
        String email = inputValidEmail();
        String mountainCode = inputValidMountainCode(management);

        management.createStudent(id, name, phone, email, mountainCode);
        System.out.println(management.getLastMessage());
    }

    private static void updateRegistration(RegistrationService management) {
        System.out.println("\n--- Update Registration Information ---");
        String id = ConsoleInput.getString("Enter Student ID to update: ");
        Student current = management.getStudentById(id);
        if (current == null) {
            System.out.println("This student has not registered yet.");
            return;
        }

        System.out.println("Current information:");
        printStudentDetail(current);
        System.out.println("Leave a field blank to keep the old value.");
        String name = ConsoleInput.getString("New name: ");
        String phone = ConsoleInput.getString("New phone: ");
        String email = ConsoleInput.getString("New email: ");
        String mountainCode = ConsoleInput.getString("New mountain code: ");

        management.updateStudent(id, name, phone, email, mountainCode);
        System.out.println(management.getLastMessage());
    }

    private static void displayRegisteredList(RegistrationService management) {
        System.out.println("\n--- Registered List ---");
        List<Student> students = management.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students have registered yet.");
            return;
        }
        printStudentTable(students);
    }

    private static void deleteRegistration(RegistrationService management) {
        System.out.println("\n--- Delete Registration Information ---");
        String id = ConsoleInput.getString("Enter Student ID to delete: ");
        Student student = management.getStudentById(id);
        if (student == null) {
            System.out.println("This student has not registered yet.");
            return;
        }

        printStudentDetail(student);
        boolean confirm = ConsoleInput.getYesNo("Are you sure you want to delete this registration? (Y/N): ");
        if (!confirm) {
            System.out.println("Delete cancelled.");
            return;
        }
        management.deleteStudent(id);
        System.out.println(management.getLastMessage());
    }

    private static void searchParticipantsByName(RegistrationService management) {
        System.out.println("\n--- Search Participants by Name ---");
        String keyword = ConsoleInput.getString("Enter name or part of name: ");
        List<Student> result = management.searchByName(keyword);
        if (result.isEmpty()) {
            System.out.println("No one matches the search criteria.");
            return;
        }
        printStudentTable(result);
    }

    private static void filterDataByCampus(RegistrationService management) {
        System.out.println("\n--- Filter Data by Campus ---");
        String campusCode = ConsoleInput.getString("Enter campus code (SE/HE/DE/QE/CE): ");
        List<Student> result = management.filterByCampus(campusCode);
        if (result.isEmpty()) {
            System.out.println("No students have registered under this campus.");
            return;
        }
        printStudentTable(result);
    }

    private static void displayStatisticsByMountain(RegistrationService management) {
        System.out.println("\n--- Statistics by Mountain Peak ---");
        List<StatisticalInfo> statistics = management.getStatisticsByMountain();
        if (statistics.isEmpty()) {
            System.out.println("No registration data available for statistics.");
            return;
        }
        System.out.println("--------------------------------------------------");
        System.out.printf("%-15s | %-18s | %-15s%n", "Mountain Code", "Participants", "Total Cost");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < statistics.size(); i++) {
            StatisticalInfo info = statistics.get(i);
            System.out.printf("%-15s | %-18d | %15s%n",
                    info.getMountainCode(),
                    info.getNumberOfStudents(),
                    MONEY_FORMAT.format(info.getTotalCost()));
        }
        System.out.println("--------------------------------------------------");
    }

    private static void saveData(RegistrationService management) {
        management.saveData();
        System.out.println(management.getLastMessage());
    }

    private static boolean exitProgram(RegistrationService management) {
        if (!management.isSaved()) {
            boolean save = ConsoleInput.getYesNo("Data has changed. Do you want to save before exiting? (Y/N): ");
            if (save) {
                management.saveData();
                System.out.println(management.getLastMessage());
            }
        }
        System.out.println("Good bye!");
        return true;
    }

    private static void printStudentTable(List<Student> students) {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-20s | %-12s | %-25s | %-13s | %12s%n",
                "Student ID", "Name", "Phone", "Email", "Mountain", "Fee");
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            System.out.printf("%-12s | %-20s | %-12s | %-25s | %-13s | %12s%n",
                    s.getStudentId(),
                    s.getName(),
                    s.getPhone(),
                    s.getEmail(),
                    s.getMountainCode(),
                    MONEY_FORMAT.format(s.getTuitionFee()));
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
    }

    private static void printStudentDetail(Student student) {
        System.out.println("Student ID   : " + student.getStudentId());
        System.out.println("Name         : " + student.getName());
        System.out.println("Phone        : " + student.getPhone());
        System.out.println("Email        : " + student.getEmail());
        System.out.println("Mountain Code: " + student.getMountainCode());
        System.out.println("Fee          : " + MONEY_FORMAT.format(student.getTuitionFee()));
    }
}
