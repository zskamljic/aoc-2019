import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Day10 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input10.txt"));
        var asteroids = toAsteroids(input);

        updateVisibility(asteroids);
        var selectedAsteroid = asteroids.stream()
                .max(Comparator.comparing(Asteroid::getVisibility))
                .orElseThrow();

        System.out.println(selectedAsteroid.getVisibility());
        part02(asteroids, selectedAsteroid);
    }

    private static void part02(List<Asteroid> asteroids, Asteroid selectedAsteroid) {
        var field = new Field(asteroids);
        field.fireLasers(selectedAsteroid);
    }

    private static List<Asteroid> toAsteroids(String input) {
        var result = new ArrayList<Asteroid>();

        var lines = input.split("\n");
        for (var j = 0; j < lines.length; j++) {
            var chars = lines[j].toCharArray();
            for (var i = 0; i < chars.length; i++) {
                if (chars[i] == '#') {
                    result.add(new Asteroid(i, j));
                }
            }
        }

        return result;
    }

    private static void updateVisibility(List<Asteroid> asteroids) {
        var field = new Field(asteroids);
        for (var first : asteroids) {
            for (var second : asteroids) {
                if (first == second) continue;

                var xDiff = second.x - first.x;
                var yDiff = second.y - first.y;

                if (xDiff == 0) {
                    yDiff = (int) Math.signum(yDiff);
                } else if (yDiff == 0) {
                    xDiff = (int) Math.signum(xDiff);
                } else {
                    // Min integer step
                    var gcd = gcd(Math.abs(xDiff), Math.abs(yDiff));

                    if (gcd == 1) {
                        first.addVisible(second);
                        second.addVisible(first);
                        continue;
                    }

                    xDiff /= gcd;
                    yDiff /= gcd;
                }

                // Check LoS
                if (field.iterateLine(first.x, first.y, xDiff, yDiff, second)) {
                    first.addVisible(second);
                    second.addVisible(first);
                }
            }
        }
    }

    private static int gcd(int a, int b) {
        while (b != 0) {
            var remainder = a % b;
            a = b;
            b = remainder;
        }
        return a;
    }

    static class Field {
        private final List<Asteroid> asteroids;
        private final int width;
        private final int height;

        public Field(List<Asteroid> asteroids) {
            this.asteroids = asteroids;
            width = asteroids.stream().mapToInt(asteroid -> asteroid.x).max().orElseThrow();
            height = asteroids.stream().mapToInt(asteroid -> asteroid.y).max().orElseThrow();
        }

        private boolean iterateLine(int x, int y, int xDiff, int yDiff, Asteroid goal) {
            var currentX = x + xDiff;
            var currentY = y + yDiff;

            while (currentX >= 0 && currentY >= 0 &&
                    currentX <= width && currentY <= height &&
                    !(currentX == goal.x && currentY == goal.y)) {
                if (asteroids.contains(new Asteroid(currentX, currentY))) {
                    return false;
                }
                currentX += xDiff;
                currentY += yDiff;
            }
            return true;
        }

        public void fireLasers(Asteroid source) {
            asteroids.remove(source);
            var queues = asteroids.stream()
                    .map(asteroid -> new Target(asteroid, source))
                    .collect(Collectors.toMap(target -> target.angle,
                            Field::asPriorityQueue,
                            Field::mergeQueues));

            var removed = 0;
            while (removed < 200) {
                var keys = queues.keySet()
                        .stream()
                        .sorted(Comparator.<Double>comparingDouble(value -> value).reversed())
                        .collect(Collectors.toList());

                for (var key : keys) {
                    var queue = queues.get(key);
                    var target = queue.poll();
                    removed++;
                    if (queue.isEmpty()) {
                        queues.remove(key);
                    }
                    if (removed == 200) {
                        System.out.println(target.x * 100 + target.y);
                        return;
                    }
                }
            }
        }

        static PriorityQueue<Target> asPriorityQueue(Target target) {
            var queue = new PriorityQueue<Target>(Comparator.comparing(t -> t.distance));
            queue.add(target);
            return queue;
        }

        private static PriorityQueue<Target> mergeQueues(PriorityQueue<Target> first, PriorityQueue<Target> second) {
            first.addAll(second);
            return first;
        }
    }

    static class Asteroid {
        final int x;
        final int y;
        private final Set<Asteroid> visibleAsteroids = new HashSet<>();

        public Asteroid(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void addVisible(Asteroid asteroid) {
            if (asteroid != this) {
                visibleAsteroids.add(asteroid);
            }
        }

        public int getVisibility() {
            return visibleAsteroids.size();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Asteroid)) return false;
            var other = (Asteroid) obj;
            return x == other.x && y == other.y;
        }
    }

    static class Target extends Asteroid {
        private final double angle;
        private final double distance;

        public Target(Asteroid asteroid, Asteroid station) {
            super(asteroid.x, asteroid.y);
            angle = Math.atan2(asteroid.x - station.x, asteroid.y - station.y);
            distance = Math.hypot(asteroid.x - station.x, asteroid.y - station.y);
        }
    }
}
