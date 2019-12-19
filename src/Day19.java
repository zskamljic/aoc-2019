import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

public class Day19 {
    public static void main(String[] args) throws IOException, InterruptedException {
        var input = Files.readString(Paths.get("input19.txt")).trim();
        var instructions = Stream.of(input.split(","))
                .mapToLong(Long::parseLong)
                .toArray();

        part01(instructions);
        part02(instructions);
    }

    private static void part01(long[] instructions) throws InterruptedException {
        var pulledCount = 0;
        for (var y = 0; y < 50; y++) {
            for (var x = 0; x < 50; x++) {
                pulledCount += getPulledResult(instructions, x, y);
            }
        }
        System.out.println(pulledCount);
    }

    private static void part02(long[] instructions) throws InterruptedException {
        var startX = 0;
        var y = 10; // Unbroken beam starts here

        while (true) {
            y++;
            while (getPulledResult(instructions, startX, y) == 0) {
                startX++;
            }
            var x = startX;
            while (getPulledResult(instructions, x + 99, y) == 1) {
                if (getPulledResult(instructions, x, y + 99) == 1) {
                    System.out.println(x * 10_000 + y);
                    return;
                }
                x++;
            }
        }
    }

    private static long getPulledResult(long[] instructions, int x, int y) throws InterruptedException {
        var program = new IntcodeExecutor(instructions);
        new Thread(program::execute).start();

        program.inputQueue.add((long) x);
        program.inputQueue.add((long) y);

        return program.outputQueue.take();
    }

    static class IntcodeExecutor extends Day09.IntcodeExecutor {
        private final BlockingQueue<Long> inputQueue = new ArrayBlockingQueue<>(10);
        private final BlockingQueue<Long> outputQueue = new ArrayBlockingQueue<>(10);

        IntcodeExecutor(long[] numbers) {
            super(numbers);
        }

        @Override
        protected long acceptLong() {
            try {
                return inputQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }

        @Override
        protected void produceLong(long value) {
            outputQueue.offer(value);
        }
    }
}
