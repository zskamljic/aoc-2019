import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input11.txt")).trim();
        var instructions = Stream.of(input.split(","))
                .mapToLong(Long::parseLong)
                .toArray();

        part01(instructions);
        part02(instructions);
    }

    private static void part01(long[] instructions) {
        var drawMap = new HashMap<Point, Integer>();

        var program = new PaintRobot(drawMap, instructions);
        program.execute();
        System.out.println(drawMap.size());
    }

    private static void part02(long[] instructions) {
        var drawMap = new HashMap<Point, Integer>();
        drawMap.put(new Point(0, 0), 1);

        var program = new PaintRobot(drawMap, instructions);
        program.execute();

        var keys = drawMap.keySet().stream().filter(key -> drawMap.get(key) == 1).collect(Collectors.toSet());
        var minX = keys.stream().mapToInt(key -> key.x).min().orElseThrow();
        var minY = keys.stream().mapToInt(key -> key.y).min().orElseThrow();
        var maxX = keys.stream().mapToInt(key -> key.x).max().orElseThrow();
        var maxY = keys.stream().mapToInt(key -> key.y).max().orElseThrow();

        for (var j = maxY; j >= minY; j--) {
            for (var i = minX; i <= maxX; i++) {
                var value = drawMap.get(new Point(i, j));
                if (value == 0) {
                    System.out.print(" ");
                } else {
                    System.out.print("#");
                }
            }
            System.out.println();
        }
    }

    static class PaintRobot extends Day09.IntcodeExecutor {
        private static final int[][] DIRECTIONS = {
                {0, 1}, // UP
                {1, 0}, // RIGHT
                {0, -1}, // DOWN
                {-1, 0} // LEFT
        };

        private final HashMap<Point, Integer> drawMap;
        private Point location = new Point(0, 0);
        boolean expectsDirection;
        int currentDirection = 0;

        PaintRobot(HashMap<Point, Integer> drawMap, long[] numbers) {
            super(numbers);
            this.drawMap = drawMap;
        }

        @Override
        protected long acceptLong() {
            return drawMap.getOrDefault(location, 0);
        }

        @Override
        protected void produceLong(long value) {
            if (expectsDirection) {
                if (value == 0) {
                    currentDirection--;
                } else {
                    currentDirection++;
                }
                if (currentDirection < 0) {
                    currentDirection = DIRECTIONS.length - 1;
                }
                if (currentDirection == DIRECTIONS.length) {
                    currentDirection = 0;
                }
                location.move(DIRECTIONS[currentDirection]);
                expectsDirection = false;
            } else {
                drawMap.put(new Point(location), (int) value);
                expectsDirection = true;
            }
        }
    }

    static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point(Point location) {
            x = location.x;
            y = location.y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point)) {
                return false;
            }

            var other = (Point) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        public void move(int[] direction) {
            x += direction[0];
            y += direction[1];
        }
    }
}
