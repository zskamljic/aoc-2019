import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Day08 {
    private static final int WIDTH = 25;
    private static final int HEIGHT = 6;

    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input08.txt")).trim();

        part01(input);
        part02(input);
    }

    private static void part01(String input) {
        var currentIndex = 0;
        var minZeroes = Long.MAX_VALUE;
        var result = 0L;

        while (currentIndex + WIDTH * HEIGHT <= input.length()) {
            var currentLayer = input.substring(currentIndex, currentIndex + WIDTH * HEIGHT);
            var zeroes = currentLayer.chars()
                    .filter(value -> value == '0')
                    .count();

            if (zeroes < minZeroes) {
                minZeroes = zeroes;

                result = currentLayer.chars()
                        .filter(value -> value == '1')
                        .count() *
                        currentLayer.chars()
                                .filter(value -> value == '2')
                                .count();
            }

            currentIndex += WIDTH * HEIGHT;
        }

        System.out.println(result);
    }

    private static void part02(String input) {
        var output = new char[WIDTH * HEIGHT];

        while (input.length() > 0) {
            var currentLayer = input.substring(input.length() - WIDTH * HEIGHT);
            input = input.substring(0, input.length() - WIDTH * HEIGHT);

            for (var i = 0; i < currentLayer.length(); i++) {
                // If not transparent
                if (currentLayer.charAt(i) != '2') {
                    output[i] = currentLayer.charAt(i);
                }
            }
        }

        for (var j = 0; j < HEIGHT; j++) {
            for (var i = 0; i < WIDTH; i++) {
                System.out.print(output[i + j * WIDTH] == '1' ? '#' : ' ');
            }
            System.out.println();
        }
    }
}
