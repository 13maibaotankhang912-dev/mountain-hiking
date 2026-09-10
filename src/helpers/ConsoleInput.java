package helpers;

import java.util.Scanner;

public class ConsoleInput {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static int getIntegerNumber(String displayMessage) throws Exception {
        System.out.print(displayMessage);
        return getIntegerNumber();
    }

    public static int getIntegerNumber() throws Exception {
        String input = SCANNER.nextLine().trim();
        if (!Validator.checkStringWithFormat(input, "\\d{1,10}")) {
            throw new Exception("Data invalid.");
        }
        return Integer.parseInt(input);
    }

    public static String getString(String displayMessage) {
        System.out.print(displayMessage);
        return getString();
    }

    public static String getString() {
        return SCANNER.nextLine();
    }

    public static boolean getYesNo(String displayMessage) {
        while (true) {
            String answer = getString(displayMessage).trim();
            if (answer.equalsIgnoreCase("Y")) {
                return true;
            }
            if (answer.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println("Please enter Y or N.");
        }
    }
}
