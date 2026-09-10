package presentation;

import services.RegistrationService;

public class Program {

    public static void main(String[] args) {
        try {
            RegistrationService studentManagement = new RegistrationService();
            studentManagement.loadData();
            Menu.launchRegistrationMenu(studentManagement);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
