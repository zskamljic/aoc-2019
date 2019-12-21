import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Day21 {
    private static final char[] SPRING_SCRIPT_PART_1 = """
            OR A T
            AND B T
            AND C T
            NOT T J
            AND D J
            WALK
            """.toCharArray();
    private static final char[] SPRING_SCRIPT_PART_2 = """
            OR A T
            AND B T
            AND C T
            NOT T J
            AND D J
            OR E T
            OR H T
            AND T J
            RUN
            """.toCharArray();

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input21.txt")).trim();
        var instructions = Arrays.stream(input.split(","))
                .mapToLong(Long::parseLong)
                .toArray();

        part01(instructions);
        part02(instructions);
    }

    private static void part01(long[] instructions) {
        var executor = new AsciiIntcodeExecutor(instructions, SPRING_SCRIPT_PART_1);
        executor.execute();
        System.out.println(executor.getOutput());
    }

    private static void part02(long[] instructions) {
        var executor = new AsciiIntcodeExecutor(instructions, SPRING_SCRIPT_PART_2);
        executor.execute();
        System.out.println(executor.getOutput());
    }

    static class AsciiIntcodeExecutor extends Day17.IntcodeExecutor {
        AsciiIntcodeExecutor(long[] numbers, char[] asciiProgram) {
            super(numbers, asciiProgram);
        }
    }
}
