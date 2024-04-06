package com.rudra.calculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;

public class StreamBased {


    public Map<String, Statistics> parse(String file){
        try {
            var dataStream = Files.lines(Path.of(file));


            var result = dataStream.filter(s ->!s.isEmpty()).collect(Collector.of(StatisticsCollector::new, StatisticsCollector::update, StatisticsCollector::combine));

            return result.getData();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(String file, boolean useParallel) {

        try {
            var dataStream = Files.lines(Path.of(file));

            if(useParallel)
                dataStream = dataStream.parallel();

            var result = dataStream.filter(s ->!s.isEmpty()).collect(Collector.of(StatisticsCollector::new, StatisticsCollector::update, StatisticsCollector::combine));

            result.printSummary();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public static class StatisticsCollector {

        private Map<String, Statistics> data = new HashMap<>(10000);


        public void update(String line) {

            int ind = line.indexOf(';');
            var city = line.substring(0, ind);
            var temp = Double.parseDouble(line.substring(ind + 1));

            var stats = data.computeIfAbsent(city, k -> new Statistics());

            stats.update(temp);

        }


        public StatisticsCollector combine(StatisticsCollector stB) {


            for (var entry : stB.data.entrySet()) {

                var valB = entry.getValue();

                var valA = data.computeIfAbsent(entry.getKey(), k -> valB);

                if (valA != valB) {
                    valA.combine(valB);
                }

            }


            return this;
        }

        public Map<String, Statistics> getData() {
            return data;
        }

        public void printSummary() {

            for (var entry : data.entrySet()) {

                System.out.println(entry.getValue().format(entry.getKey()));

            }

        }
    }
}
