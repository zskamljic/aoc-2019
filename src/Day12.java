import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Day12 {
    public static void main(String[] args) throws IOException {
        var input = Files.readAllLines(Paths.get("input12.txt"));

        var moons = input.stream().map(Moon::new)
                .collect(Collectors.toList());
        part01(moons);

        moons = input.stream().map(Moon::new)
                .collect(Collectors.toList());
        part02(moons);
    }

    private static void part01(List<Moon> moons) {
        for (var i = 0; i < 1000; i++) {
            gravityStep(moons);
            velocityStep(moons);
        }
        var energy = moons.stream()
                .mapToInt(Moon::getEnergy)
                .sum();
        System.out.println(energy);
    }

    private static void part02(List<Moon> moons) {
        var tickX = 0L;
        var tickY = 0L;
        var tickZ = 0L;

        var positionsX = new HashSet<List<Integer>>();
        var positionsY = new HashSet<List<Integer>>();
        var positionsZ = new HashSet<List<Integer>>();

        long tick = 0;
        while (tickX == 0 || tickY == 0 || tickZ == 0) {
            var currentX = new ArrayList<Integer>();
            var currentY = new ArrayList<Integer>();
            var currentZ = new ArrayList<Integer>();

            for (Moon moon : moons) {
                currentX.add(moon.position.x);
                currentX.add(moon.speed.x);
                currentY.add(moon.position.y);
                currentY.add(moon.speed.y);
                currentZ.add(moon.position.z);
                currentZ.add(moon.speed.z);
            }
            if (tickX == 0 && positionsX.contains(currentX)) {
                tickX = tick;
            }
            if (tickY == 0 && positionsY.contains(currentY)) {
                tickY = tick;
            }
            if (tickZ == 0 && positionsZ.contains(currentZ)) {
                tickZ = tick;
            }

            positionsX.add(currentX);
            positionsY.add(currentY);
            positionsZ.add(currentZ);

            gravityStep(moons);
            velocityStep(moons);

            tick++;
        }

        System.out.println(leastCommonMultiplier(tickX, tickY, tickZ));
    }

    private static long leastCommonMultiplier(long a, long b, long c) {
        long abLcm = (a * b) / greatestCommonDivisor(a, b);
        return (abLcm * c) / greatestCommonDivisor(abLcm, c);
    }

    private static long greatestCommonDivisor(long a, long b) {
        while (b != 0) {
            var remainder = a % b;
            a = b;
            b = remainder;
        }
        return a;
    }

    private static void gravityStep(List<Moon> moons) {
        for (var i = 0; i < moons.size(); i++) {
            var moonA = moons.get(i);
            for (var j = i + 1; j < moons.size(); j++) {
                var moonB = moons.get(j);
                if (moonA.position.x < moonB.position.x) {
                    moonA.speed.x++;
                    moonB.speed.x--;
                } else if (moonA.position.x > moonB.position.x) {
                    moonA.speed.x--;
                    moonB.speed.x++;
                }
                if (moonA.position.y < moonB.position.y) {
                    moonA.speed.y++;
                    moonB.speed.y--;
                } else if (moonA.position.y > moonB.position.y) {
                    moonA.speed.y--;
                    moonB.speed.y++;
                }
                if (moonA.position.z < moonB.position.z) {
                    moonA.speed.z++;
                    moonB.speed.z--;
                } else if (moonA.position.z > moonB.position.z) {
                    moonA.speed.z--;
                    moonB.speed.z++;
                }
            }
        }
    }

    private static void velocityStep(List<Moon> moons) {
        moons.forEach(Moon::move);
    }

    private static void printStep(int step, List<Moon> moons) {
        System.out.println("After " + step + " steps:");
        moons.forEach(System.out::println);
    }

    static class Moon {
        private Vec3 position = new Vec3();
        private Vec3 speed = new Vec3();

        public Moon(String line) {
            var input = new Scanner(line.replaceAll("[^\\d -]+", ""));
            position.x = input.nextInt();
            position.y = input.nextInt();
            position.z = input.nextInt();
        }

        public void move() {
            position.x += speed.x;
            position.y += speed.y;
            position.z += speed.z;
        }

        public int getEnergy() {
            return position.getEnergy() * speed.getEnergy();
        }

        @Override
        public String toString() {
            return "Moon{" +
                    "position=" + position +
                    ", speed=" + speed +
                    ", energy=" + getEnergy() +
                    '}';
        }
    }

    static class Vec3 {
        private int x;
        private int y;
        private int z;

        public int getEnergy() {
            return Math.abs(x) +
                    Math.abs(y) +
                    Math.abs(z);
        }

        @Override
        public String toString() {
            return "Vec3{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }
}
