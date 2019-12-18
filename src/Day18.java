import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day18 {
    public static void main(String[] args) throws IOException {
        part01();
        part02();
    }

    private static List<Character> mapKeysFromMap(List<String> lines) {
        return lines.stream()
                .flatMapToInt(Day18::interestingValues)
                .mapToObj(i -> (char) i)
                .collect(Collectors.toList());
    }

    private static IntStream interestingValues(String line) {
        return line.chars()
                .filter(Character::isLowerCase);
    }

    private static void part01() throws IOException {
        var lines = Files.readAllLines(Paths.get("input18.txt"));
        var keys = mapKeysFromMap(lines);

        var keysFromPoint = new HashMap<Point, List<ReachableKey>>();
        var startPosition = findPoint('@', lines);
        keysFromPoint.put(startPosition, findReachableKeys(lines, startPosition));

        for (var k : keys) {
            var position = findPoint(k, lines);
            keysFromPoint.put(position, findReachableKeys(lines, position));
        }

        var minimumSteps = findBestWalk(lines, keysFromPoint, getKeyPositions(lines, keys), new int[]{'@'});
        System.out.println(minimumSteps);
    }

    public static void part02() throws IOException {
        var lines = Files.readAllLines(Paths.get("input18b.txt"));
        var keys = mapKeysFromMap(lines);

        var dictionary = new HashMap<Point, List<ReachableKey>>();

        for (var i = '1'; i <= '4'; i++) {
            dictionary.put(findPoint(i, lines), findReachableKeys(lines, findPoint(i, lines)));
        }

        for (var k : keys) {
            dictionary.put(findPoint(k, lines), findReachableKeys(lines, findPoint(k, lines)));
        }

        var minimumSteps = findBestWalk(lines, dictionary, getKeyPositions(lines, keys), new int[]{'1', '2', '3', '4'});

        System.out.println(minimumSteps);
    }

    private static Point findPoint(int character, List<String> map) {
        for (var y = 0; y < map.size(); y++) {
            var line = map.get(y);
            var x = line.indexOf(character);
            if (x != -1) {
                return new Point(x, y);
            }
        }
        throw new IllegalArgumentException("Map does not contain the value");
    }

    private static List<ReachableKey> findReachableKeys(List<String> map, Point start) {
        var list = new ArrayList<ReachableKey>();
        var visited = new HashSet<Point>();

        var queue = new ArrayDeque<Point>();
        queue.add(start);

        while (queue.size() > 0) {
            var point = queue.pollFirst();

            if (visited.contains(point)) continue;

            visited.add(point);
            var currentValue = map.get(point.y).charAt(point.x);
            if (currentValue == '#') continue;

            var surrounding = point.surrounding();
            queue.addAll(surrounding);

            if (Character.isLowerCase(currentValue)) {
                list.add(new ReachableKey(point.distance, currentValue, point.gates));
            } else if (Character.isUpperCase(currentValue)) {
                for (var p : surrounding) {
                    p.gates = point.gates |= 1 << (currentValue - 'A');
                }
            }
        }

        return list;
    }

    private static int findBestWalk(List<String> map, Map<Point, List<ReachableKey>> keyPaths, Map<Character, Point> positions, int[] robots) {
        var robotPositions = Arrays.stream(robots).mapToObj(c -> findPoint(c, map)).collect(Collectors.toList());
        var currentMinimum = Integer.MAX_VALUE;

        var startingSet = new PointSet();
        for (var index = 0; index < robotPositions.size(); index++) {
            var point = robotPositions.get(index);
            startingSet.set(index, point);
        }

        var queue = new ArrayDeque<State>();
        queue.add(new State(startingSet, 0));

        var visited = new HashMap<PointSetInt, Integer>();
        var allKeys = Math.pow(2, positions.size()) - 1;

        while (queue.size() > 0) {
            var state = queue.pollFirst();

            var stateKey = new PointSetInt(state.positions, state.keyChain);
            if (visited.containsKey(stateKey)) {
                var steps = visited.get(stateKey);
                if (steps <= state.steps) continue;
            }
            visited.put(stateKey, state.steps);

            if (state.keyChain == allKeys) {
                currentMinimum = Math.min(currentMinimum, state.steps);
                continue;
            }

            for (int i = 0; i < robots.length; i++) {
                for (var key : keyPaths.get(state.positions.get(i))) {
                    var keyIndex = 1 << (key.value - 'a');
                    if (state.hasKey(keyIndex) || state.isMissingRequiredKey(key)) continue;

                    var newOwned = state.keyChain | keyIndex;
                    var newPositions = state.positions.copy();
                    newPositions.set(i, positions.get(key.value));
                    queue.add(new State(newPositions, newOwned, state.steps + key.distance));
                }
            }
        }

        return currentMinimum;
    }

    private static Map<Character, Point> getKeyPositions(List<String> map, List<Character> keys) {
        return keys.stream()
                .collect(Collectors.toMap(key -> key, key -> findPoint(key, map)));
    }

    private static class PointSetInt {
        private final PointSet pointSet;
        private final int ownedKeys;

        public PointSetInt(PointSet positions, int ownedKeys) {
            pointSet = positions;
            this.ownedKeys = ownedKeys;
        }

        @Override
        public int hashCode() {
            return Objects.hash(pointSet, ownedKeys);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PointSetInt)) return false;

            var other = (PointSetInt) obj;
            return pointSet.equals(other.pointSet) && ownedKeys == other.ownedKeys;
        }
    }

    private static class State {
        public PointSet positions;
        public int keyChain;
        public int steps;

        public State(PointSet startingSet, int keyChain) {
            this.positions = startingSet;
            this.keyChain = keyChain;
        }

        public State(PointSet newPos, int newOwned, int steps) {
            positions = newPos;
            keyChain = newOwned;
            this.steps = steps;
        }

        public boolean hasKey(int keyIndex) {
            return (keyChain & keyIndex) == keyIndex;
        }

        public boolean isMissingRequiredKey(ReachableKey key) {
            return (key.obstacles & keyChain) != key.obstacles;
        }
    }

    // TODO: get rid of this
    private static class PointSet {
        public Point[] points;

        public PointSet() {
            points = new Point[4];
        }

        public PointSet(Point... points) {
            this.points = points;
        }

        public Point get(int index) {
            return points[index];
        }

        public void set(int index, Point value) {
            points[index] = value;
        }

        public PointSet copy() {
            var output = new Point[points.length];
            for (var i = 0; i < output.length; i++) {
                if (points[i] != null) {
                    output[i] = points[i].copy();
                }
            }
            return new PointSet(output);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PointSet)) return false;

            var other = (PointSet) obj;
            return Arrays.equals(points, other.points);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(points);
        }
    }

    private static class Point {
        public int x;
        public int y;
        public int distance;
        public int gates;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point(int x, int y, int distance, int gates) {
            this.x = x;
            this.y = y;
            this.distance = distance;
            this.gates = gates;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point)) return false;

            var other = (Point) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        public List<Point> surrounding() {
            return List.of(
                    new Point(x, y - 1, distance + 1, gates),
                    new Point(x - 1, y, distance + 1, gates),
                    new Point(x + 1, y, distance + 1, gates),
                    new Point(x, y + 1, distance + 1, gates)
            );
        }

        public Point copy() {
            return new Point(x, y);
        }
    }

    private static class ReachableKey {
        public char value;
        public int distance;
        public int obstacles;

        public ReachableKey(int distance, char value, int obstacles) {
            this.value = value;
            this.distance = distance;
            this.obstacles = obstacles;
        }
    }
}
