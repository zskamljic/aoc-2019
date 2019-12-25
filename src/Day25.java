import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Scanner;

public class Day25 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input25.txt")).trim();
        var instructions = Arrays.stream(input.split(","))
                .mapToLong(Long::parseLong)
                .toArray();

        var computer = new AsciiComputer(instructions);
        new Thread(computer::execute).start();
    }

    static class AsciiComputer extends Day09.IntcodeExecutor {
        private final Scanner scanner = new Scanner(System.in);
        private final Deque<Long> inputQueue = new ArrayDeque<>();

        AsciiComputer(long[] numbers) {
            super(numbers);
        }

        @Override
        protected long acceptLong() {
            if (inputQueue.isEmpty()) {
                var input = scanner.nextLine();
                input.chars().mapToLong(value -> value)
                        .forEach(inputQueue::add);
                inputQueue.add((long) '\n');
            }
            return inputQueue.pollFirst();
        }

        @Override
        protected void produceLong(long value) {
            System.out.print((char) value);
        }
    }
}
