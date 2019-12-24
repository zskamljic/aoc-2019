import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

public class Day24 {
    private static final int ITERATIONS = 200;

    public static void main(String[] args) throws IOException {
        part01();
        part02();
    }

    private static void part01() throws IOException {
        var grid = loadGrid();
        var seen = new HashSet<String>();

        while (true) {
            grid = updateStep(grid);
            var stringValue = createGrid(grid);
            if (seen.contains(stringValue)) {
                System.out.println(calculateBiodiversity(stringValue));
                break;
            }
            seen.add(stringValue);
        }
    }

    private static void part02() throws IOException {
        var levels = new HashMap<Integer, char[][]>();
        levels.put(0, loadGrid());

        for (var i = 0; i < ITERATIONS; i++) {
            var nextLevels = new HashMap<Integer, char[][]>();
            final HashMap<Integer, char[][]> finalLevels = levels;
            levels.forEach((key, value) -> doGridStep(finalLevels, nextLevels, key, value));
            levels = nextLevels;
        }
        var result = levels.values().stream()
                .map(Day24::createGrid)
                .flatMapToInt(String::chars)
                .filter(value -> value == '#')
                .count();
        System.out.println(result);
    }

    private static void doGridStep(HashMap<Integer, char[][]> levels, HashMap<Integer, char[][]> nextLevels, int key, char[][] currentLevel) {
        var previousLevel = levels.getOrDefault(key - 1, emptyGrid());
        var nextLevel = levels.getOrDefault(key + 1, emptyGrid());
        var output = new char[5][5];
        for (var y = 0; y < 5; y++) {
            for (var x = 0; x < 5; x++) {
                if (x == 2 && y == 2) continue;

                var surrounding = 0;
                surrounding += calculateLeft(x, y, currentLevel, previousLevel, nextLevel);
                surrounding += calculateRight(x, y, currentLevel, previousLevel, nextLevel);
                surrounding += calculateTop(x, y, currentLevel, previousLevel, nextLevel);
                surrounding += calculateBottom(x, y, currentLevel, previousLevel, nextLevel);

                if (currentLevel[y][x] == '#' && surrounding != 1) {
                    output[y][x] = '.';
                } else if (currentLevel[y][x] == '.' && shouldSpawnOnEmpty(surrounding)) {
                    output[y][x] = '#';
                } else {
                    output[y][x] = currentLevel[y][x];
                }
            }
        }
        nextLevels.put(key, output);
        if (!levels.containsKey(key - 1)) {
            updateClearPrevious(previousLevel, currentLevel);
            nextLevels.put(key - 1, previousLevel);
        }
        if (!levels.containsKey(key + 1)) {
            updateClearNext(nextLevel, currentLevel);
            nextLevels.put(key + 1, nextLevel);
        }
    }

    private static void updateClearPrevious(char[][] previousLevel, char[][] currentLevel) {
        var surrounding = calculateRight(1, 2, null, null, currentLevel);
        if (shouldSpawnOnEmpty(surrounding)) {
            previousLevel[2][1] = '#';
        }
        surrounding = calculateLeft(3, 2, null, null, currentLevel);
        if (shouldSpawnOnEmpty(surrounding)) {
            previousLevel[2][3] = '#';
        }
        surrounding = calculateBottom(2, 1, null, null, currentLevel);
        if (shouldSpawnOnEmpty(surrounding)) {
            previousLevel[1][2] = '#';
        }
        surrounding = calculateTop(2, 3, null, null, currentLevel);
        if (shouldSpawnOnEmpty(surrounding)) {
            previousLevel[3][2] = '#';
        }
    }

    private static void updateClearNext(char[][] nextLevel, char[][] currentLevel) {
        var grid = new int[5][5];
        for (var i = 0; i < 5; i++) {
            grid[0][i] += calculateTop(i, 0, null, currentLevel, null);
            grid[4][i] += calculateBottom(i, 4, null, currentLevel, null);
            grid[i][0] += calculateLeft(0, i, null, currentLevel, null);
            grid[i][4] += calculateRight(4, i, null, currentLevel, null);
        }
        for (var i = 0; i < 5; i++) {
            if (shouldSpawnOnEmpty(grid[0][i])) {
                nextLevel[0][i] = '#';
            }
            if (shouldSpawnOnEmpty(grid[4][i])) {
                nextLevel[4][i] = '#';
            }
            if (shouldSpawnOnEmpty(grid[i][0])) {
                nextLevel[i][0] = '#';
            }
            if (shouldSpawnOnEmpty(grid[i][4])) {
                nextLevel[i][4] = '#';
            }
        }
    }

    private static boolean shouldSpawnOnEmpty(int surrounding) {
        return surrounding == 1 || surrounding == 2;
    }

    private static int calculateLeft(int x, int y, char[][] currentLevel, char[][] previousLevel, char[][] nextLevel) {
        var sum = 0;
        if (x == 0) {
            sum += valueOf(previousLevel, 1, 2);
        } else if (x == 3 && y == 2) {
            for (var line = 0; line < 5; line++) {
                sum += valueOf(nextLevel, 4, line);
            }
        } else {
            sum += valueOf(currentLevel, x - 1, y);
        }
        return sum;
    }

    private static int calculateRight(int x, int y, char[][] currentLevel, char[][] previousLevel, char[][] nextLevel) {
        var sum = 0;
        if (x == 4) {
            sum += valueOf(previousLevel, 3, 2);
        } else if (x == 1 && y == 2) {
            for (var line = 0; line < 5; line++) {
                sum += valueOf(nextLevel, 0, line);
            }
        } else {
            sum += valueOf(currentLevel, x + 1, y);
        }
        return sum;
    }

    private static int calculateTop(int x, int y, char[][] currentLevel, char[][] previousLevel, char[][] nextLevel) {
        var sum = 0;
        if (y == 0) {
            sum += valueOf(previousLevel, 2, 1);
        } else if (y == 3 && x == 2) {
            for (var column = 0; column < 5; column++) {
                sum += valueOf(nextLevel, column, 4);
            }
        } else {
            sum += valueOf(currentLevel, x, y - 1);
        }
        return sum;
    }

    private static int calculateBottom(int x, int y, char[][] currentLevel, char[][] previousLevel, char[][] nextLevel) {
        var sum = 0;
        if (y == 4) {
            sum += valueOf(previousLevel, 2, 3);
        } else if (y == 1 && x == 2) {
            for (var column = 0; column < 5; column++) {
                sum += valueOf(nextLevel, column, 0);
            }
        } else {
            sum += valueOf(currentLevel, x, y + 1);
        }
        return sum;
    }

    private static int valueOf(char[][] grid, int x, int y) {
        return grid[y][x] == '#' ? 1 : 0;
    }

    private static char[][] emptyGrid() {
        var output = new char[5][5];
        for (var i = 0; i < output.length; i++) {
            for (var j = 0; j < output.length; j++) {
                output[i][j] = '.';
            }
        }
        return output;
    }

    private static int calculateBiodiversity(String stringValue) {
        var chars = stringValue.replaceAll("\n", "").toCharArray();
        var sum = 0;
        for (var i = 0; i < chars.length; i++) {
            if (chars[i] == '#') sum += Math.pow(2, i);
        }
        return sum;
    }

    private static String createGrid(char[][] grid) {
        var builder = new StringBuilder();
        for (var line : grid) {
            builder.append(new String(line)).append('\n');
        }
        return builder.toString();
    }

    private static char[][] loadGrid() throws IOException {
        var input = Files.readAllLines(Paths.get("input24.txt"));
        var grid = new char[input.size()][];
        for (var i = 0; i < input.size(); i++) {
            grid[i] = input.get(i).toCharArray();
        }
        return grid;
    }

    private static char[][] updateStep(char[][] grid) {
        var result = new char[grid.length][grid[0].length];

        for (var y = 0; y < grid.length; y++) {
            for (var x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == '#' && shouldDie(grid, x, y)) {
                    result[y][x] = '.';
                } else if (grid[y][x] == '.' && shouldInfest(grid, x, y)) {
                    result[y][x] = '#';
                } else {
                    result[y][x] = grid[y][x];
                }
            }
        }

        return result;
    }

    private static boolean shouldDie(char[][] grid, int x, int y) {
        return new Day20.Point(x, y).surrounding(grid[0].length, grid.length)
                .stream()
                .mapToInt(point -> grid[point.y][point.x] == '#' ? 1 : 0)
                .sum() != 1;
    }

    private static boolean shouldInfest(char[][] grid, int x, int y) {
        var surrounding = new Day20.Point(x, y).surrounding(grid[0].length, grid.length)
                .stream()
                .mapToInt(point -> grid[point.y][point.x] == '#' ? 1 : 0)
                .sum();
        return surrounding == 1 || surrounding == 2;
    }
}
