// STDISCM S14 Exconde, Gomez, Maristela, Rejano
package producer;

/**
 * * IntegerValidator class that provides methods to validate integer inputs.
 * It checks if the input is a valid positive integer and if it exceeds the maximum value for an integer.
 */
public class IntegerValidator {
    /**
     * * Validates if the input is a valid positive integer.
     * @param input the input string to validate
     * @return true if the input is a valid positive integer, false otherwise
     */
    public static boolean isValidPositiveInteger(String input) {
        try {
            int value = Integer.parseInt(input);
            return value >= 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * * * Checks if the input exceeds the maximum value for an integer.
     * @param input
     * @return true if the input exceeds the maximum value for an integer, false otherwise
     */
    public static boolean exceedsIntegerLimit(String input) {
        try {
            long value = Long.parseLong(input);
            return value > Integer.MAX_VALUE;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}