package com.rudra.calculation;

import com.rudra.calculation.parsers.RandomAccessParser;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RandomSeekThreaded {

    private final int noOfBuckets = 100;


    public Map<String, Statistics> parse(String file){

        return new RandomAccessParser(file).parse();

    }

    public void run(String file) {


        // determineBuckets(file).forEach(range -> System.out.println(range[0] + " - " + range[1]));


//        var data = new RandomAccessParser(file,32768).parse();

//        data.forEach((k,v) -> System.out.println(v.format(k)));


        List<Future<Map<String, Statistics>>> futures = new ArrayList<>(noOfBuckets);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (var bucket : determineBuckets(file)) {

                futures.add(executor.submit(() -> new RandomAccessParser(file, 32768, bucket[0], bucket[1]).parse())); //processBucket(file, bucket[0], bucket[1])

            }

        }

        try {
            printSummary(futures);

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }


    private Map<String, Statistics> processBucket(String file, long start, long end) {

        Map<String, Statistics> data = new HashMap<>(10000);

        try (var rf = new RandomAccessFile(file, "r")) {

            rf.seek(start);

            do {

                ByteBuffer bf;
                //bf.slice().toString()


                var line = rf.readLine();

                int ind = line.indexOf(';');
                var city = line.substring(0, ind);
                var temp = Double.parseDouble(line.substring(ind + 1));

                var stats = data.computeIfAbsent(city, k -> new Statistics());

                stats.update(temp);

            } while (rf.getFilePointer() < end);


            return data;


        } catch (IOException e) {
            throw new RuntimeException(e);
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

    private List<long[]> determineBuckets(String file) {


        List<long[]> buckets = new ArrayList<>(noOfBuckets);

        try (var rf = new RandomAccessFile(file, "r")) {

            long bucketLength = rf.length() / noOfBuckets;

            System.out.println("File length : " + rf.length() + " | Bucket Length : " + bucketLength);

            while (true) {

                long[] bucketBoundary = new long[2];
                bucketBoundary[0] = rf.getFilePointer();

                if ((bucketBoundary[0] + bucketLength) < rf.length())
                    rf.seek(bucketBoundary[0] + bucketLength);
                else {

                    bucketBoundary[1] = rf.length();
                    buckets.add(bucketBoundary);
                    break;

                }


                int ch = rf.read();

                while (ch != -1 && ch != '\n') {
                    ch = rf.read();

                }

                bucketBoundary[1] = rf.getFilePointer() - 1;

                buckets.add(bucketBoundary);

                if (ch == -1)
                    break;
            }


            return buckets;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
