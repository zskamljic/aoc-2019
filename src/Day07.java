import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.ToIntBiFunction;
import java.util.stream.Stream;

public class Day07 {
    public static void main(String[] args) throws IOException {
        var input = Files.readString(Paths.get("input07.txt")).trim();
        var instructions = Stream.of(input.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();

        allPermutations(instructions, new int[]{0, 1, 2, 3, 4}, Day07::part01Step);
        allPermutations(instructions, new int[]{5, 6, 7, 8, 9}, Day07::part02Step);
    }

    private static void allPermutations(int[] instructions, int[] start, ToIntBiFunction<int[], int[]> operator) {
        var permutations = new ArrayList<int[]>();
        permute(start, permutations);

        permutations.parallelStream()
                .mapToInt(settings -> operator.applyAsInt(instructions, settings))
                .max()
                .ifPresent(System.out::println);
    }

    private static int part01Step(int[] instructions, int[] phaseSettings) {
        var ampInput = 0;
        for (var setting : phaseSettings) {
            var amplifier = new IntcodeExecutor(instructions);
            amplifier.pushInput(setting);
            amplifier.pushInput(ampInput);
            amplifier.execute();
            ampInput = amplifier.getOutput();
        }
        return ampInput;
    }

    private static int part02Step(int[] instructions, int[] phaseSettings) {
        var amplifiers = new IntcodeExecutor[phaseSettings.length];
        var threads = new Thread[amplifiers.length];

        // Tie inputs and outputs together to avoid manual passing
        for (var i = 0; i < amplifiers.length; i++) {
            amplifiers[i] = new IntcodeExecutor(instructions);
            threads[i] = new Thread(amplifiers[i]::execute);

            if (i != 0) {
                amplifiers[i - 1].outputQueue = amplifiers[i].inputQueue;
            }
        }
        amplifiers[amplifiers.length - 1].outputQueue = amplifiers[0].inputQueue;

        // Push settings and start
        for (var i = 0; i < phaseSettings.length; i++) {
            amplifiers[i].pushInput(phaseSettings[i]);
            threads[i].start();
        }
        // Push start value to kickoff the execution
        amplifiers[0].pushInput(0);

        // Wait for all amplifiers to finish
        for (var thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread interrupted!");
            }
        }

        // Obtain final value
        return amplifiers[amplifiers.length - 1].getOutput();
    }

    public static void permute(int[] arr, List<int[]> permutations) {
        permuteStep(arr, 0, permutations);
    }

    private static void permuteStep(int[] array, int index, List<int[]> permutations) {
        if (index >= array.length - 1) {
            permutations.add(Arrays.copyOf(array, array.length));
            return;
        }

        for (int i = index; i < array.length; i++) {
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;

            permuteStep(array, index + 1, permutations);

            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    static class IntcodeExecutor extends Day05.IntcodeExecutor {
        private ArrayBlockingQueue<Integer> inputQueue = new ArrayBlockingQueue<>(100);
        private ArrayBlockingQueue<Integer> outputQueue = new ArrayBlockingQueue<>(100);

        IntcodeExecutor(int[] numbers) {
            super(numbers);
        }

        public void pushInput(int value) {
            inputQueue.add(value);
        }

        public int getOutput() {
            try {
                return outputQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException("Unable to take");
            }
        }

        @Override
        protected int acceptInt() {
            try {
                return inputQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException("Unable to take");
            }
        }

        @Override
        protected void produceInt(int value) {
            try {
                outputQueue.put(value);
            } catch (InterruptedException e) {
                throw new RuntimeException("Unable to put");
            }
        }
    }
}
