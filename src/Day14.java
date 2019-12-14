import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day14 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input14.txt"));
        var reactions = lines.stream()
                .map(Reaction::new)
                .collect(Collectors.toMap(reaction -> reaction.output.name, reaction -> reaction));

        System.out.println(part01(reactions, "FUEL", 1, new HashMap<>()));
        part02(reactions);
    }

    private static void part02(Map<String, Reaction> reactions) {
        var limit = 1_000_000_000_000L;
        var amount = 1L;

        while (true) {
            var oreAmount = part01(reactions, "FUEL", amount * 10, new HashMap<>());
            if (oreAmount < limit) {
                amount *= 10;
            } else {
                break;
            }
        }
        var increment = amount;
        while (increment != 0) {
            var oreAmount = part01(reactions, "FUEL", amount + increment, new HashMap<>());
            if (oreAmount < limit) {
                amount += increment;
            } else {
                increment /= 10;
            }
        }
        System.out.println(amount);
    }


    static long part01(Map<String, Reaction> reactions, String item, long quantity, Map<String, Long> remaining) {
        if ("ORE".equals(item)) {
            return quantity;
        }
        quantity -= remaining.getOrDefault(item, 0L);
        remaining.put(item, 0L);

        var reaction = reactions.get(item);
        var multiplier = (int) Math.ceil(quantity / (float) reaction.output.count);
        var remainder = multiplier * reaction.output.count - quantity;

        var sum = 0L;
        for (var input : reaction.inputs) {
            sum += part01(reactions, input.name, input.count * multiplier, remaining);
        }
        remaining.put(item, remainder);
        return sum;
    }

    private static boolean hasNonOreAndSufficient(Map<String, Long> requirements) {
        return requirements.entrySet().stream()
                .anyMatch(Day14::notOreAndSufficient);
    }

    private static boolean notOreAndSufficient(Map.Entry<String, Long> element) {
        return !"ORE".equals(element.getKey()) && element.getValue() > 0;
    }

    static class Reaction {
        private final List<Element> inputs;
        private final Element output;

        Reaction(String reaction) {
            var inputsOutputs = reaction.split(" => ");
            var inputs = inputsOutputs[0].split(", ");
            this.inputs = Stream.of(inputs).map(Element::new).collect(Collectors.toList());

            output = new Element(inputsOutputs[1]);
        }
    }

    static class Element {
        private final String name;
        private final long count;

        Element(String element) {
            var values = element.split(" ");
            count = Integer.parseInt(values[0]);
            name = values[1];
        }

        public Element(String name, int count) {
            this.name = name;
            this.count = count;
        }
    }
}