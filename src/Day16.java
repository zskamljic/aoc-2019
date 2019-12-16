import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day16 {
    private static final int OUTPUT_LENGTH = 8;
    private static final int ITERATIONS = 100;
    private static final int INPUT_REPEATS = 10_000;

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input16.txt")).trim();

        part01(input);
        part02(input);
    }

    private static void part01(String input) {
        var list = Stream.of(input.split(""))
                .mapToInt(Integer::parseInt)
                .toArray();

        var pattern = new ArrayList<>(List.of(
                0, 1, 0, -1
        ));

        for (var iteration = 0; iteration < ITERATIONS; iteration++) {
            for (var listIndex = 0; listIndex < list.length; listIndex++) {
                var newPattern = new ArrayList<Integer>();

                // Extend for nth element
                for (var element : pattern) {
                    for (var repeats = 0; repeats < listIndex + 1; repeats++) {
                        newPattern.add(element);
                    }
                }

                // Summation with the pattern
                var sum = 0;
                for (var index = 0; index < list.length; index++) {
                    sum += list[index] * newPattern.get((index + 1) % newPattern.size());
                }
                list[listIndex] = Math.abs(sum) % 10;
            }
        }
        for (var i = 0; i < OUTPUT_LENGTH; i++) {
            System.out.print(list[i]);
        }
        System.out.println();
    }

    private static void part02(String input) {
        var list = new int[input.length() * INPUT_REPEATS];
        var inputInts = Stream.of(input.split("")).mapToInt(Integer::parseInt).toArray();

        for (var i = 0; i < INPUT_REPEATS; i++) {
            System.arraycopy(inputInts, 0, list, i * inputInts.length, inputInts.length);
        }

        var offsetBuilder = new StringBuilder();
        for (var i = 0; i < 7; i++) {
            offsetBuilder.append(list[i]);
        }
        var offset = Integer.parseInt(offsetBuilder.toString());

        for (var i = 0; i < ITERATIONS; i++) {
            for (var j = list.length - 1; j > offset - 1; j--) {
                list[j - 1] += list[j];
                list[j - 1] %= 10;
            }
        }
        for (var i = 0; i < OUTPUT_LENGTH; i++) {
            System.out.print(list[offset + i]);
        }
        System.out.println();
    }
}
