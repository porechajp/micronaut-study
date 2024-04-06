package com.rudra.calculation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MultiThreaded {


    public void run(String file) {

        final int size = 25_000_000;

        try (ExecutorService service = new ThreadPoolExecutor(8, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(10))) {


            List<Future<Map<String, Statistics>>> tasks = new ArrayList<>(1000);

            try (var br = new BufferedReader(new FileReader(file), 1024 * 1024 * 1024)) {

                String line = null;

                List<String> job = new ArrayList<>(size);

                Semaphore semaphore = new Semaphore(10);


                while ((line = br.readLine()) != null) {

                    job.add(line);

                    if (job.size() == size) {


                       // System.out.println(semaphore.availablePermits());
                        semaphore.acquire();

                        tasks.add(service.submit(new Processor(job,semaphore)));

                        job = new ArrayList<>(size);
                    }

                }

                printSummary(tasks);


            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }

    private void printSummary(List<Future<Map<String, Statistics>>> tasks) throws ExecutionException, InterruptedException {

        Map<String, Statistics> result = new HashMap<>(10000);

        for (var task : tasks) {

            for (var entry : task.get().entrySet()) {

                result.compute(entry.getKey(), (k, v) -> v == null ? entry.getValue() : v.combine(entry.getValue()));

            }

        }

        for (var entry : result.entrySet()) {

            System.out.println(entry.getValue().format(entry.getKey()));

        }


    }


    public static class Processor implements Callable<Map<String, Statistics>> {

        private final List<String> lines;
        private final Semaphore semaphore;

        public Processor(List<String> lines, Semaphore semaphore) {
            this.lines = lines;
            this.semaphore = semaphore;
        }

        @Override
        public Map<String, Statistics> call() throws Exception {


            semaphore.release();
        //    System.out.println("After release : " + semaphore.availablePermits());

            Map<String, Statistics> dict = new HashMap<>(10000);

            for (String line : lines) {
                int ind = line.indexOf(';');
                var city = line.substring(0, ind);
                var temp = Double.parseDouble(line.substring(ind + 1));

                var stats = dict.computeIfAbsent(city, k -> new Statistics());

                stats.update(temp);

            }

            return dict;

        }
    }
}
