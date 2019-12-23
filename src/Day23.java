import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Day23 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input23.txt")).trim();
        var instructions = Arrays.stream(input.split(","))
                .mapToLong(Long::parseLong)
                .toArray();

        execute(instructions, Part01::new);
        execute(instructions, Part02::new);
    }

    private static void execute(long[] instructions, Function<HashMap<Long, Computer>, Consumer<Packet>> handlerProducer) {
        var computers = new HashMap<Long, Computer>();
        var threads = new ArrayList<Thread>();
        var packetHandler = handlerProducer.apply(computers);

        for (var i = 0L; i < 50; i++) {
            var computer = new Computer(instructions, packetHandler);
            computers.put(i, computer);
            computer.inputQueue.offer(Packet.singleValue(i));
            threads.add(new Thread(computer::execute));
        }
        threads.forEach(Thread::start);
    }

    static class Computer extends Day09.IntcodeExecutor {
        private final Queue<Packet> inputQueue = new ArrayDeque<>();
        private final Consumer<Packet> consumer;
        boolean wasLastOperationPoll;

        private Packet packet;

        Computer(long[] numbers, Consumer<Packet> consumer) {
            super(numbers);
            this.consumer = consumer;
        }

        @Override
        protected long acceptLong() {
            var candidate = inputQueue.peek();
            wasLastOperationPoll = true;
            if (candidate == null) {
                return -1;
            }
            if (candidate.x != null) {
                var value = candidate.x;
                candidate.x = null;
                return value;
            }
            inputQueue.poll();
            return candidate.y;
        }

        @Override
        protected void produceLong(long value) {
            wasLastOperationPoll = false;
            if (packet == null) {
                packet = new Packet(value);
            } else if (packet.x == null) {
                packet.x = value;
            } else {
                packet.y = value;
                consumer.accept(packet);
                packet = null;
            }
        }
    }

    static class Packet {
        long target;
        Long x;
        Long y;

        public Packet(long target) {
            this.target = target;
        }

        public static Packet singleValue(long value) {
            var packet = new Packet(0);
            packet.y = value;
            return packet;
        }
    }

    static class Part01 implements Consumer<Packet> {
        private final Map<Long, Computer> computers;

        Part01(Map<Long, Computer> computers) {
            this.computers = computers;
        }

        @Override
        public void accept(Packet packet) {
            if (packet.target == 255) {
                System.out.println(packet.y);
                computers.values().forEach(computer -> computer.halted = true);
                return;
            }

            var computer = computers.get(packet.target);
            if (computer == null) return;
            computer.inputQueue.offer(packet);
        }
    }

    static class Part02 implements Consumer<Packet>, Runnable {
        private final Map<Long, Computer> computers;
        private Packet natPacket;
        private boolean shouldRun = true;
        private long lastY = 0L;

        Part02(Map<Long, Computer> computers) {
            this.computers = computers;
            new Thread(this).start();
        }

        @Override
        public void accept(Packet packet) {
            if (packet.target == 255) {
                natPacket = packet;
                return;
            }

            var computer = computers.get(packet.target);
            if (computer == null) return;
            computer.inputQueue.offer(packet);
        }

        @Override
        public void run() {
            while (shouldRun) {
                var allComputers = computers.values().size();
                var polling = computers.values()
                        .stream()
                        .filter(computer -> computer.inputQueue.isEmpty() && computer.wasLastOperationPoll)
                        .count();
                if (allComputers == polling) {
                    if (natPacket == null) continue;
                    if (lastY == natPacket.y) {
                        System.out.println(natPacket.y);
                        stop();
                    }

                    lastY = natPacket.y;
                    computers.get(0L).inputQueue.add(natPacket);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        private void stop() {
            shouldRun = false;
            computers.values().forEach(computer -> computer.halted = true);
        }
    }
}
