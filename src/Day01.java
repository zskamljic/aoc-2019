import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.IntUnaryOperator;

public class Day01 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input01.txt"));

        part01(lines);
        part02(lines);
    }

    private static void part01(List<String> lines) {
        commonPart(lines, Day01::calculateFuelForMass);
    }

    private static void part02(List<String> lines) {
        commonPart(lines, Day01::calculateTotaFuelForMass);
    }

    private static void commonPart(List<String> lines, IntUnaryOperator mapper) {
        var sumFuel = lines.parallelStream()
                .mapToInt(Integer::parseInt)
                .map(mapper)
                .sum();
        System.out.println(sumFuel);
    }

    private static int calculateFuelForMass(int mass) {
        return mass / 3 - 2;
    }

    private static int calculateTotaFuelForMass(int mass) {
        var requiredFuel = 0;

        var addedFuel = calculateFuelForMass(mass);
        while (addedFuel > 0) {
            requiredFuel += addedFuel;
            addedFuel = calculateFuelForMass(addedFuel);
        }
        return requiredFuel;
    }
}
