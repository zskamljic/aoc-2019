import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day05 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input05.txt")).trim();

        var numbers = Stream.of(input.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        var executor = new IntcodeExecutor(numbers);
        executor.execute();
    }

    static class IntcodeExecutor {
        private static final int ADD = 1;
        private static final int MUL = 2;
        private static final int INPUT = 3;
        private static final int OUTPUT = 4;
        private static final int JUMP_TRUE = 5;
        private static final int JUMP_FALSE = 6;
        private static final int LESS_THAN = 7;
        private static final int EQUALS = 8;
        private static final int HALT = 99;

        private final int[] numbers;
        private final Scanner scanner = new Scanner(System.in);

        private boolean failed;
        private boolean halted;

        IntcodeExecutor(int[] numbers) {
            this.numbers = Arrays.copyOf(numbers, numbers.length);
        }

        public void execute() {
            var currentIndex = 0;

            while (!failed && !halted) {
                var instruction = numbers[currentIndex];

                currentIndex += processInstruction(instruction, currentIndex);
            }
        }

        private int processInstruction(int instruction, int currentIndex) {
            var opCode = instruction % 100;
            var modes = instruction / 100;

            return switch (opCode) {
                case ADD -> add(modes, currentIndex);
                case MUL -> multiply(modes, currentIndex);
                case INPUT -> input(currentIndex);
                case OUTPUT -> output(modes, currentIndex);
                case JUMP_TRUE -> jumpIfTrue(modes, currentIndex);
                case JUMP_FALSE -> jumpIfFalse(modes, currentIndex);
                case LESS_THAN -> lessThan(modes, currentIndex);
                case EQUALS -> equals(modes, currentIndex);
                case HALT -> halt();
                default -> fail();
            };
        }

        private int add(int modes, int currentIndex) {
            var inputA = getInput(modes, 0, currentIndex + 1);
            var inputB = getInput(modes, 1, currentIndex + 2);
            var output = numbers[currentIndex + 3];

            numbers[output] = inputA + inputB;
            return 4;
        }

        private int multiply(int modes, int currentIndex) {
            var inputA = getInput(modes, 0, currentIndex + 1);
            var inputB = getInput(modes, 1, currentIndex + 2);
            var output = numbers[currentIndex + 3];

            numbers[output] = inputA * inputB;
            return 4;
        }

        private int input(int currentIndex) {
            numbers[numbers[currentIndex + 1]] = acceptInt();
            return 2;
        }

        private int output(int modes, int currentIndex) {
            produceInt(getInput(modes, 0, currentIndex + 1));
            return 2;
        }

        private int jumpIfTrue(int modes, int currentIndex) {
            var input = getInput(modes, 0, currentIndex + 1);
            if (input == 0) {
                return 3;
            }

            return -currentIndex + getInput(modes, 1, currentIndex + 2);
        }

        private int jumpIfFalse(int modes, int currentIndex) {
            var input = getInput(modes, 0, currentIndex + 1);
            if (input != 0) {
                return 3;
            }

            return -currentIndex + getInput(modes, 1, currentIndex + 2);
        }

        private int lessThan(int modes, int currentIndex) {
            var inputA = getInput(modes, 0, currentIndex + 1);
            var inputB = getInput(modes, 1, currentIndex + 2);
            var output = numbers[currentIndex + 3];

            numbers[output] = inputA < inputB ? 1 : 0;
            return 4;
        }

        private int equals(int modes, int currentIndex) {
            var inputA = getInput(modes, 0, currentIndex + 1);
            var inputB = getInput(modes, 1, currentIndex + 2);
            var output = numbers[currentIndex + 3];

            numbers[output] = inputA == inputB ? 1 : 0;
            return 4;
        }

        private int getInput(int modes, int modeIndex, int numbersIndex) {
            for (int i = 0; i < modeIndex; i++) {
                modes /= 10;
            }
            var mode = modes % 10;

            if (mode == 0) return numbers[numbers[numbersIndex]];
            return numbers[numbersIndex];
        }

        private int halt() {
            halted = true;
            return 1;
        }

        private int fail() {
            failed = true;
            return 0;
        }

        protected int acceptInt() {
            return scanner.nextInt();
        }

        protected void produceInt(int value) {
            System.out.println(value);
        }
    }
}
