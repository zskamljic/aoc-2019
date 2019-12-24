import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Day03 {
    public static void main(String[] args) throws IOException {
        var inputs = Files.readAllLines(Paths.get("input03.txt"));

        var lineA = toPoints(inputs.get(0));
        var lineB = toPoints(inputs.get(1));

        lineA.retainAll(lineB);

        // Part 01
        lineA.stream()
                .mapToInt(Point::getManhattanDistance)
                .min()
                .ifPresent(System.out::println);

        // Part 02
        lineA.stream()
                .mapToInt(pointA -> {
                    var pointB = lineB.get(lineB.indexOf(pointA));
                    return pointA.getPathLength() + pointB.getPathLength();
                }).min()
                .ifPresent(System.out::println);
    }

    private static List<Point> toPoints(String line) {
        var parts = line.split(",");
        var points = new ArrayList<Point>();

        var x = 0;
        var y = 0;
        var pathLength = 0;

        for (var part : parts) {
            var direction = part.charAt(0);
            var amount = Integer.parseInt(part.substring(1));

            for (var i = 0; i < amount; i++) {
                var point = switch (direction) {
                    case 'R' -> new Point(++x, y, ++pathLength);
                    case 'L' -> new Point(--x, y, ++pathLength);
                    case 'U' -> new Point(x, ++y, ++pathLength);
                    default -> new Point(x, --y, ++pathLength);
                };
                points.add(point);
            }
        }
        return points;
    }

    static class Point {
        int x;
        int y;
        int pathLength;

        public Point(int x, int y, int pathLength) {
            this.x = x;
            this.y = y;
            this.pathLength = pathLength;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point)) return false;

            var other = (Point) obj;
            return other.x == x && other.y == y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        int getManhattanDistance() {
            return Math.abs(x) + Math.abs(y);
        }

        int getPathLength() {
            return pathLength;
        }
    }
}

