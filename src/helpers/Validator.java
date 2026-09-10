package helpers;

public final class Validator {

    private static final String STUDENT_ID_PATTERN = "^(SE|HE|DE|QE|CE)\\d{6}$";
    private static final String PHONE_PATTERN = "^\\d{10}$";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String[] DISCOUNT_PREFIXES = {
        "086", "096", "097", "098", "032", "033", "034", "035", "036", "037", "038", "039",
        "088", "091", "094", "083", "084", "085", "081", "082"
    };

    private Validator() {
    }

    public static boolean checkNumberInMinMax(int number, int min, int max) {
        return number >= min && number <= max;
    }

    public static boolean checkStringEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean checkStringLengthInRange(String value, int min, int max) {
        if (!checkStringEmpty(value)) {
            return false;
        }
        int length = value.trim().length();
        return length >= min && length <= max;
    }

    public static boolean checkStringWithFormat(String value, String pattern) {
        return value != null && pattern != null && value.matches(pattern);
    }

    public static String normalizeStudentId(String studentId) {
        if (studentId == null) {
            return "";
        }
        return studentId.trim().toUpperCase();
    }

    public static boolean isValidStudentId(String studentId) {
        return normalizeStudentId(studentId).matches(STUDENT_ID_PATTERN);
    }

    public static boolean isValidStudentName(String name) {
        return name != null && name.trim().matches("^[A-Z][a-zA-Z]*(\\s[A-Z][a-zA-Z]*)*$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.trim().matches(PHONE_PATTERN);
    }

    public static boolean isDiscountPhone(String phone) {
        if (!isValidPhone(phone)) {
            return false;
        }
        String normalizedPhone = phone.trim();
        for (int i = 0; i < DISCOUNT_PREFIXES.length; i++) {
            if (normalizedPhone.startsWith(DISCOUNT_PREFIXES[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValidEmail(String email) {
        return email != null && email.trim().matches(EMAIL_PATTERN);
    }

    public static boolean isValidMountainCodeInput(String mountainCode) {
        return checkStringEmpty(mountainCode);
    }
}
