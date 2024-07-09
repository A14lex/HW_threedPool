import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws InterruptedException {
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
        List<Thread> threads = new ArrayList<>();


        for (String text : texts) {
            Runnable logic = () -> {
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
                System.out.println(text.substring(0, 100) + " -> " + maxSize);
            };
            Thread myThread = new Thread(logic);
            myThread.start();
            threads.add(myThread);
        }
        for (Thread thread : threads) {
            thread.join(); // зависаем, ждём когда поток объект которого лежит в threads завершится
        }


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