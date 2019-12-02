import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class Day02 {
    private static final int QUERIED_INDEX = 0;
    private static final int TARGET_VALUE = 19690720;
    private static final int ADD = 1;
    private static final int MUL = 2;
    private static final int HALT = 99;

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input02.txt")).trim();

        var numbers = Stream.of(input.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        // Part 01
        System.out.println(execute(copyOf(numbers), 12, 2));

        part02(numbers);
    }

    private static void part02(int[] numbers) {
        for (int noun = 0; noun <= 99; noun++) {
            for (int verb = 0; verb <= 99; verb++) {
                var result = execute(copyOf(numbers), noun, verb);

                if (result == TARGET_VALUE) {
                    System.out.println(100 * noun + verb);
                    return;
                }
            }
        }
    }

    private static int execute(int[] numbers, int noun, int verb) {
        var currentIndex = 0;
        numbers[1] = noun;
        numbers[2] = verb;

        while (true) {
            var instruction = numbers[currentIndex];

            switch (instruction) {
                case ADD:
                    add(numbers, numbers[currentIndex + 1], numbers[currentIndex + 2], numbers[currentIndex + 3]);
                    break;
                case MUL:
                    multiply(numbers, numbers[currentIndex + 1], numbers[currentIndex + 2], numbers[currentIndex + 3]);
                    break;
                case HALT:
                    return numbers[QUERIED_INDEX];
                default:
                    return -1;
            }
            currentIndex += 4;
        }
    }

    private static void add(int[] memory, int a, int b, int target) {
        memory[target] = memory[a] + memory[b];
    }

    private static void multiply(int[] memory, int a, int b, int target) {
        memory[target] = memory[a] * memory[b];
    }

    private static int[] copyOf(int[] numbers) {
        return Arrays.copyOf(numbers, numbers.length);
    }
}
