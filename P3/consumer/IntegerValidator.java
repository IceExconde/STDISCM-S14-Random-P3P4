package consumer;

public class IntegerValidator {
    public static boolean isValidPositiveInteger(String input) {
        try {
            int value = Integer.parseInt(input);
            return value >= 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean exceedsIntegerLimit(String input) {
        try {
            long value = Long.parseLong(input);
            return value > Integer.MAX_VALUE;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
