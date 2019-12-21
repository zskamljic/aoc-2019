import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day20 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input20.txt"));
        var map = new GateMap(lines);
        map.part01();
        map.part02();
    }

    static class GateMap {
        private final int height;
        private final int width;
        private char[][] values;
        private Map<Point, Character> portalCharacters = new HashMap<>();
        private Map<Point, String> locationNames = new HashMap<>();
        private Map<String, List<Point>> portalEntrances = new HashMap<>();
        private Map<Point, Boolean> innerGate = new HashMap<>();

        GateMap(List<String> input) {
            height = input.size();
            width = input.get(0).length();
            values = new char[height][width];

            loadCharacters(input);
            processPortals();
        }

        public void part01() {
            var state = findPath(new State(portalEntrances.get("AA").get(0), 0, 0), false);
            System.out.println(state.distance);
        }

        public void part02() {
            var state = findPath(new State(portalEntrances.get("AA").get(0), 0, 0), true);
            System.out.println(state.distance);
        }

        private State findPath(State state, boolean recursive) {
            var queue = new ArrayDeque<State>();
            var visited = new HashSet<PointLevel>();

            queue.add(state);
            while (queue.size() > 0) {
                var current = queue.pollFirst();
                visited.add(new PointLevel(current.point, current.level));

                if (locationNames.containsKey(current.point)) {
                    if (locationNames.get(current.point).equals("ZZ") && current.level == 0) return current;

                    var gate = portalEntrances.get(locationNames.get(current.point)).stream()
                            .filter(point -> !point.equals(current.point))
                            .findFirst();
                    if (gate.isPresent()) {
                        var otherPoint = gate.get();
                        var destination = new PointLevel(otherPoint, current.level);

                        if (recursive) {
                            destination.level += innerGate.getOrDefault(current.point, false) ? 1 : -1;
                        }

                        if (!visited.contains(destination)) {
                            if (isPortalOpen(current, otherPoint, recursive)) {
                                var nextState = new State(otherPoint, current.distance + 1, destination.level);
                                nextState.portals.addAll(current.portals);
                                nextState.portals.add(new PortalLevel(locationNames.get(current.point), destination.level));
                                queue.add(nextState);
                                continue;
                            }
                        }
                    }
                }

                current.point.surrounding(width, height).stream()
                        .filter(point -> values[point.y][point.x] == '.' && !visited.contains(new PointLevel(point, current.level)))
                        .forEach(point -> queue.add(new State(point, current.distance + 1, current.level, current.portals)));
            }
            throw new IllegalStateException("No path found");
        }

        private boolean isPortalOpen(State state, Point target, boolean recursive) {
            if (!recursive) return true;

            var otherGate = locationNames.get(target);

            if ("AA".equals(otherGate) || "ZZ".equals(otherGate)) {
                return state.level == 0;
            }

            return state.level != 0 || innerGate.containsKey(state.point);
        }

        private void loadCharacters(List<String> input) {
            for (var y = 0; y < height; y++) {
                for (var x = 0; x < width; x++) {
                    var current = input.get(y).charAt(x);
                    switch (current) {
                        case ' ' -> values[y][x] = '#';
                        case '#', '.' -> values[y][x] = current;
                        default -> {
                            values[y][x] = current;
                            tryFindPortals(x, y, current);
                        }
                    }
                }
            }
        }

        private void processPortals() {
            for (var entry : portalCharacters.entrySet()) {
                var otherPoint = entry.getKey().surrounding(width, height)
                        .stream()
                        .filter(point -> Character.isUpperCase(values[point.y][point.x]))
                        .findFirst().orElseThrow();

                var difference = otherPoint.subtract(entry.getKey());
                var entrance = otherPoint.add(difference);
                if (entrance.x >= width || entrance.y >= height || values[entrance.y][entrance.x] != '.') {
                    entrance = entry.getKey().subtract(difference);
                }

                var portalName = String.valueOf(values[entry.getKey().y][entry.getKey().x]) +
                        values[otherPoint.y][otherPoint.x];
                locationNames.put(entrance, portalName);

                portalEntrances.putIfAbsent(portalName, new ArrayList<>());
                portalEntrances.get(portalName).add(entrance);

                if (entrance.y > 3 && entrance.y < height - 3 && entrance.x > 3 && entrance.x < width - 3) {
                    innerGate.put(entrance, true);
                }
            }
        }

        private void tryFindPortals(int x, int y, char current) {
            var currentPoint = new Point(x, y);
            var isNew = currentPoint.surrounding(width, height).stream()
                    .noneMatch(portalCharacters::containsKey);
            if (isNew) {
                portalCharacters.put(currentPoint, current);
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

        public List<Point> surrounding(int width, int height) {
            var result = new ArrayList<>(List.of(
                    new Point(x - 1, y),
                    new Point(x + 1, y),
                    new Point(x, y - 1),
                    new Point(x, y + 1)
            ));
            result.removeIf(point -> point.x < 0 || point.x >= width || point.y < 0 || point.y >= height);
            return result;
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

        public Point subtract(Point other) {
            return new Point(x - other.x, y - other.y);
        }

        public Point add(Point other) {
            return new Point(x + other.x, y + other.y);
        }
    }

    static class State {
        private final Point point;
        private final int distance;
        private final int level;
        private final List<PortalLevel> portals;

        public State(Point point, int distance, int level) {
            this.point = point;
            this.distance = distance;
            this.level = level;
            portals = new ArrayList<>();
        }

        public State(Point point, int distance, int level, List<PortalLevel> portals) {
            this.point = point;
            this.distance = distance;
            this.level = level;
            this.portals = portals;
        }

        @Override
        public int hashCode() {
            return Objects.hash(point, distance, level);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof State)) return false;

            var other = (State) obj;
            return Objects.equals(point, other.point) && distance == other.distance && level == other.level;
        }
    }

    static class PointLevel {
        private Point point;
        private int level;

        public PointLevel(Point point, int level) {
            this.point = point;
            this.level = level;
        }

        @Override
        public int hashCode() {
            return Objects.hash(point, level);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PointLevel)) return false;

            var other = (PointLevel) obj;
            return Objects.equals(point, other.point) && level == other.level;
        }
    }

    static class PortalLevel {
        private String name;
        private int level;

        public PortalLevel(String name, int level) {
            this.name = name;
            this.level = level;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, level);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof PortalLevel)) return false;

            var other = (PortalLevel) obj;
            return Objects.equals(name, other.name) && level == other.level;
        }
    }
}
