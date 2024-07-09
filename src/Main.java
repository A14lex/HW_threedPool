import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        List<Thread> firstThreads = new ArrayList<>();
        for (int i = 0; i < texts.length; i++) {
            int j = i;
            Runnable newThread = () -> {
                texts[j] = generateText("aab", 30_000);
            };
            Thread thread = new Thread(newThread);
            thread.start();
            firstThreads.add(thread);

        }
        for (Thread thread : firstThreads) {
            thread.join();
        }

        long startTs = System.currentTimeMillis(); // start time
        //List<Thread> threads = new ArrayList<>();
        List<Future<String>> tasks = new ArrayList<>();
        /*
        Заведите пул потоков. Вместо списка из потоков сделайте список из Future - сделали.
В цикле отправьте в пул потоков задачи на исполнение,
получив в ответ на каждую отправку Future, которые войдут в список.
После цикла с отправкой задач на исполнение пройдитесь циклом по Future и у каждого вызовите get для ожидания и получения результата,
который вы обработаете для получения ответа на задачу.
         */
        final ExecutorService threadPool = Executors.newFixedThreadPool(texts.length);

        for (String text : texts) {
            Callable <String> myCallable = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    int maxSize = 0;
                    for (int i = 0; i < text.length(); i++) {
                        for (int j = 0; j < text.length(); j++) {
                            if (i >= j) {
                                continue;
                            }
                            boolean bFound = false;
                            for (int k = i; k < j; k++) {
                                if (text.charAt(k) == 'b') {
                                    bFound = true;
                                    break;
                                }
                            }
                            if (!bFound && maxSize < j - i) {
                                maxSize = j - i;
                            }
                        }
                    }
                    String result = text.substring(0, 100) + " -> " + maxSize;
                    return result;
                }
            };
            final Future<String> task = threadPool.submit(myCallable);
            tasks.add(task);


        }
        for (Future<String> future : tasks) {
           final String resultOfTasks = future.get();
            System.out.println(resultOfTasks);
        }
        threadPool.shutdown();


        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {

            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}