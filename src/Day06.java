import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day06 {
    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("input06.txt"));

        var nodes = new HashMap<String, Node>();
        lines.stream()
                .map(line -> line.split("\\)"))
                .forEach(result -> addOrbit(result, nodes));

        part01(nodes);
        part02(nodes);
    }

    private static void part01(Map<String, Node> nodes) {
        var totalOrbits = nodes.values().stream()
                .mapToInt(Node::calculateOrbits)
                .sum();

        System.out.println(totalOrbits);
    }

    private static void part02(Map<String, Node> nodes) {
        var youParent = nodes.get("YOU").parent;
        var sanParent = nodes.get("SAN").parent;

        sanParent.addSanFlag();

        var transfers = 0;
        var currentNode = youParent;
        while (currentNode != sanParent) {
            if (!currentNode.hasSanta) {
                currentNode = currentNode.parent;
            } else {
                currentNode = currentNode.findSantaNode();
            }
            transfers++;
        }
        System.out.println(transfers);
    }

    private static void addOrbit(String[] result, Map<String, Node> nodes) {
        var center = nodes.computeIfAbsent(result[0], Node::new);
        var body = nodes.computeIfAbsent(result[1], Node::new);

        body.parent = center;
        center.children.add(body);
    }

    static class Node {
        Node parent;
        List<Node> children = new ArrayList<>();
        String name;
        boolean hasSanta = false;

        public Node(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Node && ((Node) obj).name.equals(name);
        }

        int calculateOrbits() {
            var parentBody = parent;
            var totalOrbits = 0;
            while (parentBody != null) {
                parentBody = parentBody.parent;
                totalOrbits++;
            }
            return totalOrbits;
        }

        public void addSanFlag() {
            hasSanta = true;

            var parentBody = parent;
            while (parentBody != null) {
                parentBody.hasSanta = true;
                parentBody = parentBody.parent;
            }
        }

        public Node findSantaNode() {
            return children.parallelStream()
                    .filter(node -> node.hasSanta)
                    .findFirst()
                    .orElseThrow();
        }
    }
}
