import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Day13 {
    private static final long BLOCK = 2;
    private static final long PADDLE = 3;
    private static final long BALL = 4;

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input13.txt")).trim();
        var numbers = Stream.of(input.split(","))
                .mapToLong(Long::parseLong)
                .toArray();

        part01(numbers);
        part02(numbers);
    }

    private static void part01(long[] numbers) {
        var executor = new Arcade(numbers);
        executor.execute();

        var screen = new HashMap<Point, Long>();

        var iterator = executor.outputs.iterator();
        while (iterator.hasNext()) {
            var x = iterator.next();
            var y = iterator.next();
            var type = iterator.next();

            screen.put(new Point(x, y), type);
        }

        var count = screen.values().stream()
                .filter(value -> value == BLOCK)
                .count();

        System.out.println(count);
    }

    private static void part02(long[] numbers) {
        numbers[0] = 2; // Play for free
        var executor = new InteractiveArcade(numbers);
        var thread = new Thread(executor::execute);
        thread.start();
    }

    static class Point {
        long x;
        long y;

        public Point(long x, long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;

            var other = (Point) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    static class Arcade extends Day09.IntcodeExecutor {
        List<Long> outputs = new ArrayList<>();

        Arcade(long[] numbers) {
            super(numbers);
        }

        @Override
        protected void produceLong(long value) {
            outputs.add(value);
        }
    }

    static class InteractiveArcade extends Day09.IntcodeExecutor {
        List<Long> output = new ArrayList<>();
        Point paddle;
        Point ball;

        InteractiveArcade(long[] numbers) {
            super(numbers);
        }

        @Override
        protected long acceptLong() {
            if (ball == null || paddle == null) {
                return 0;
            }
            return (long) Math.signum(ball.x - paddle.x);
        }

        @Override
        protected void produceLong(long value) {
            output.add(value);
            if (output.size() == 3) {
                if (output.get(0) == -1 && output.get(1) == 0) {
                    System.out.println("Score: " + output.get(2));
                } else if (output.get(2) == PADDLE) {
                    paddle = new Point(output.get(0), output.get(1));
                } else if (output.get(2) == BALL) {
                    ball = new Point(output.get(0), output.get(1));
                }
                output.clear();
            }
        }
    }
}
