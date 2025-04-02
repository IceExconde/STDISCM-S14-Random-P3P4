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
}