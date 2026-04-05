package util;

/**
 * Reusable validation helpers.
 * Week 2 – Wrapper class methods (Integer.parseInt, Double.parseDouble).
 */
public class ValidationUtil {

    private ValidationUtil() { /* utility class */ }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public static boolean isValidRoomNumber(String input) {
        try {
            // Week 2 – Wrapper method Integer.parseInt
            int n = Integer.parseInt(input);
            return n > 0 && n <= 9999;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInt(String input) {
        try {
            return Integer.parseInt(input) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String input) {
        try {
            // Week 2 – Wrapper method Double.parseDouble
            return Double.parseDouble(input) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String notBlank(String s, String fieldName) {
        if (s == null || s.isBlank())
            throw new IllegalArgumentException(fieldName + " cannot be blank.");
        return s.trim();
    }
}
