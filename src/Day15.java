import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 {
    private static final int NORTH = 1;
    private static final int SOUTH = 2;
    private static final int WEST = 3;
    private static final int EAST = 4;

    private static final int WALL = 0;
    private static final int MOVED = 1;
    private static final int OXYGEN = 2;

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input15.txt")).trim();
        var instructions = Stream.of(input.split(","))
                .mapToLong(Long::parseLong)
                .toArray();

        // Explore the map
        var map = new HashMap<Point, Integer>();
        map.put(new Point(0, 0), MOVED);

        var inputs = new ArrayDeque<>(List.of(
                List.of(NORTH),
                List.of(SOUTH),
                List.of(WEST),
                List.of(EAST)
        ));

        while (inputs.size() > 0) {
            var path = inputs.removeFirst();
            var executor = new IntcodeExecutor(instructions, path);
            executor.execute();
            var output = executor.lastOutput();
            map.put(Point.fromPath(path), output);
            if (output != WALL) {
                handleMoved(map, path, inputs);
            }
        }

        // Part 01
        breadthFirstSearch(map);
        fillOxygen(map);
    }

    private static void breadthFirstSearch(HashMap<Point, Integer> map) {
        var distances = new HashMap<Point, Integer>();
        var source = new Point(0, 0);
        distances.put(source, 0);

        var target = map.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == OXYGEN)
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow();

        var visited = new HashSet<Point>();
        var queue = new ArrayDeque<Point>();
        queue.add(source);

        while (queue.size() > 0) {
            var current = queue.pollFirst();
            if (current == target) break;
            visited.add(current);

            var connected = current.getNeighbours(map);
            connected.removeIf(visited::contains);
            connected.forEach(point -> distances.put(point, distances.get(current) + 1));
            queue.addAll(connected);
        }
        System.out.println(distances.get(target));
    }

    private static void fillOxygen(HashMap<Point, Integer> map) {
        var fillMap = new HashMap<>(map);

        var steps = 0;
        while (fillMap.containsValue(MOVED)) {
            var toSpread = fillMap.entrySet().stream()
                    .filter(entry -> entry.getValue() == OXYGEN)
                    .map(Map.Entry::getKey)
                    .flatMap(point -> point.getNeighbours(fillMap).stream())
                    .filter(point -> fillMap.get(point) != OXYGEN)
                    .collect(Collectors.toSet());
            toSpread.forEach(point -> fillMap.put(point, OXYGEN));
            steps++;
        }

        System.out.println(steps);
    }

    private static void handleMoved(Map<Point, Integer> visited, List<Integer> path, Deque<List<Integer>> inputs) {
        for (var i = 1; i <= 4; i++) {
            var pathCopy = new ArrayList<>(path);
            pathCopy.add(i);
            if (!visited.containsKey(Point.fromPath(pathCopy)) && !inputs.contains(pathCopy)) {
                inputs.add(pathCopy);
            }
        }
    }

    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static Point fromPath(List<Integer> path) {
            var x = 0;
            var y = 0;

            for (var direction : path) {
                switch (direction) {
                    case NORTH -> y++;
                    case SOUTH -> y--;
                    case WEST -> x--;
                    case EAST -> x++;
                }
            }

            return new Point(x, y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point)) return false;

            var other = (Point) obj;
            return other.x == x && other.y == y;
        }

        public List<Point> getNeighbours(HashMap<Point, Integer> map) {
            var candidates = new ArrayList<>(List.of(
                    new Point(x + 1, y),
                    new Point(x, y + 1),
                    new Point(x - 1, y),
                    new Point(x, y - 1)
            ));
            candidates.removeIf(point -> map.getOrDefault(point, WALL) == WALL);
            return candidates;
        }
    }

    static class IntcodeExecutor extends Day09.IntcodeExecutor {
        private final List<Integer> inputs;
        private final List<Integer> outputs = new ArrayList<>();

        IntcodeExecutor(long[] numbers, List<Integer> inputs) {
            super(numbers);
            this.inputs = new ArrayList<>(inputs);
        }

        @Override
        protected long acceptLong() {
            return inputs.remove(0);
        }

        @Override
        protected void produceLong(long value) {
            outputs.add((int) value);
            if (inputs.size() == 0) {
                halted = true;
            }
        }

        int lastOutput() {
            return outputs.get(outputs.size() - 1);
        }
    }
}
