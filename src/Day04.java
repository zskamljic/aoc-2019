public class Day04 {
    private static final String INPUT = "254032-789860";

    public static void main(String[] args) {
        var rangeStrings = INPUT.split("-");

        var rangeMin = Integer.parseInt(rangeStrings[0]);
        var rangeMax = Integer.parseInt(rangeStrings[1]);

        var validPart01 = 0;
        var validPart02 = 0;
        for (var i = rangeMin; i < rangeMax; i++) {
            var stringValue = Integer.toString(i);
            if (isValidPasswordPart01(stringValue)) {
                validPart01++;
                if (isValidPasswordPart02(stringValue)) {
                    validPart02++;
                }
            }
        }

        System.out.println(validPart01);
        System.out.println(validPart02);
    }

    private static boolean isValidPasswordPart01(String value) {
        var chars = value.toCharArray();
        var sorted = value.chars().sorted().toArray();

        var hasDouble = false;
        for (var i = 0; i < chars.length; i++) {
            if (chars[i] != sorted[i]) return false;

            if (i != 0 && chars[i] == chars[i - 1]) {
                hasDouble = true;
            }
        }

        return hasDouble;
    }

    private static boolean isValidPasswordPart02(String value) {
        var chars = value.toCharArray();

        var lastChar = -1;
        var matchLength = 0;
        var hasDouble = false;
        for (char single : chars) {
            if (single == lastChar) {
                matchLength++;
            } else {
                if (matchLength == 2) {
                    hasDouble = true;
                }
                matchLength = 1;
            }
            lastChar = single;
        }
        if (matchLength == 2) {
            hasDouble = true;
        }

        return hasDouble;
    }
}
