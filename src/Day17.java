import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Day17 {
    public static void main(String[] args) throws IOException {
        var program = Stream.of(Files.readString(Paths.get("input17.txt")).trim()
                .split(","))
                .mapToLong(Long::parseLong)
                .toArray();

        part01(program);
        part02(program);
    }

    private static void part01(long[] program) {
        var executor = new IntcodeExecutor(program);
        executor.execute();

        var output = executor.builder.toString();
        var field = toField(output);
        System.out.println(intersectionSum(field));
    }

    private static void part02(long[] program) {
        program[0] = 2; // Wake the robot;

        var executor = new IntcodeExecutor(program);
        executor.execute();

        var output = executor.builder.toString();
        System.out.println(output);
    }

    private static int intersectionSum(char[][] field) {
        var sum = 0;

        // Exclude outer bounds of the array, there can be no intersections
        // and this way we don't need to check for bounds
        for (var i = 1; i < field.length - 1; i++) {
            for (var j = 1; j < field[i].length - 1; j++) {
                if (field[i][j] != '#') continue;
                if (field[i - 1][j] != '#' || field[i + 1][j] != '#') continue;
                if (field[i][j - 1] != '#' || field[i][j + 1] != '#') continue;
                sum += i * j;
            }
        }
        return sum;
    }

    private static char[][] toField(String output) {
        var lines = output.split("\n");
        var field = new char[lines.length][];

        for (var i = 0; i < lines.length; i++) {
            field[i] = lines[i].toCharArray();
        }
        return field;
    }

    static class IntcodeExecutor extends Day09.IntcodeExecutor {
        private StringBuilder builder = new StringBuilder();
        private char[] pathProgram = """
                A,A,B,C,B,C,B,C,B,A
                L,10,L,8,R,8,L,8,R,6
                R,6,R,8,R,8
                R,6,R,6,L,8,L,10
                n

                """.toCharArray();
        private int index;

        IntcodeExecutor(long[] numbers) {
            super(numbers);
        }

        @Override
        protected long acceptLong() {
            return pathProgram[index++];
        }

        @Override
        protected void produceLong(long value) {
            if (value < 127) {
                builder.append((char) value);
            } else {
                System.out.println(value);
            }
        }
    }
}
