import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day22 {
    private static final int DECK_SIZE_PART_1 = 10_007;
    private static final BigInteger DECK_SIZE_PART_2 = new BigInteger(Long.toString(119315717514047L));
    private static final BigInteger REPEAT_COUNT = new BigInteger(Long.toString(101741582076661L));

    public static void main(String[] args) throws IOException {
        var instructions = Files.readAllLines(Paths.get("input22.txt"));

        part1(instructions);
        part2(instructions);
    }

    private static void part1(List<String> instructions) {
        var deck = new int[DECK_SIZE_PART_1];
        for (var i = 0; i < deck.length; i++) {
            deck[i] = i;
        }

        for (var instruction : instructions) {
            deck = apply(instruction, deck);
        }

        for (var i = 0; i < deck.length; i++) {
            if (deck[i] == 2019) {
                System.out.println(i);
                break;
            }
        }
    }

    private static void part2(List<String> instructions) {
        var factors = new BigInteger[]{BigInteger.ONE, BigInteger.ZERO};
        for (var i = instructions.size() - 1; i >= 0; i--) {
            var instruction = instructions.get(i);
            apply(instruction, factors);
            for (var j = 0; j < factors.length; j++) {
                factors[j] = factors[j].mod(DECK_SIZE_PART_2);
            }
        }
        var power = factors[0].modPow(REPEAT_COUNT, DECK_SIZE_PART_2);
        var result = power.multiply(new BigInteger("2020"))
                .add(factors[1].multiply(power.add(DECK_SIZE_PART_2)
                        .subtract(BigInteger.ONE))
                        .multiply(factors[0].subtract(BigInteger.ONE)
                                .modPow(DECK_SIZE_PART_2.subtract(BigInteger.TWO), DECK_SIZE_PART_2)))
                .mod(DECK_SIZE_PART_2);
        System.out.println(result);
    }

    private static void apply(String instruction, BigInteger[] factors) {
        if ("deal into new stack".equals(instruction)) {
            factors[0] = factors[0].multiply(new BigInteger("-1"));
            factors[1] = factors[1].add(BigInteger.ONE).multiply(new BigInteger("-1"));
        } else if (instruction.startsWith("cut")) {
            var value = instruction.substring(4);
            factors[1] = factors[1].add(new BigInteger(value));
        } else {
            var value = instruction.substring(20);
            var modPower = new BigInteger(value)
                    .modPow(DECK_SIZE_PART_2.subtract(BigInteger.TWO), DECK_SIZE_PART_2);
            for (var i = 0; i < factors.length; i++) {
                factors[i] = factors[i].multiply(modPower);
            }
        }
    }

    private static int[] apply(String instruction, int[] deck) {
        if ("deal into new stack".equals(instruction)) {
            return reverseArray(deck);
        } else if (instruction.startsWith("cut")) {
            return cut(Integer.parseInt(instruction.substring(4)), deck);
        } else {
            var parts = instruction.split(" ");
            var increment = Integer.parseInt(parts[parts.length - 1]);
            return dealWithIncrement(increment, deck);
        }
    }

    private static int[] reverseArray(int[] deck) {
        var output = new int[deck.length];
        for (var i = 0; i < deck.length; i++) {
            output[i] = deck[deck.length - i - 1];
        }
        return output;
    }

    private static int[] cut(int amount, int[] deck) {
        var output = new int[deck.length];
        if (amount == 0) {
            return deck;
        } else if (amount > 0) {
            System.arraycopy(deck, amount, output, 0, deck.length - amount);
            System.arraycopy(deck, 0, output, output.length - amount, amount);
        } else {
            amount = Math.abs(amount);
            System.arraycopy(deck, deck.length - amount, output, 0, amount);
            System.arraycopy(deck, 0, output, amount, deck.length - amount);
        }
        return output;
    }

    private static int[] dealWithIncrement(int increment, int[] deck) {
        var output = new int[deck.length];
        for (var i = 0; i < deck.length; i++) {
            var targetIndex = (i * increment) % deck.length;
            output[targetIndex] = deck[i];
        }
        return output;
    }
}
