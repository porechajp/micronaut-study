package com.rudra;

import com.rudra.calculation.MultiThreaded;
import com.rudra.calculation.RandomSeekThreaded;
import com.rudra.calculation.Simple;
import com.rudra.calculation.StreamBased;
import com.rudra.calculation.parsers.RandomAccessParser;

import java.time.Duration;
import java.time.Instant;

public class Main {
    public static void main(String[] args) throws Exception {


      //  CreateMeasurements.generate(1000_000_000);


        var start = Instant.now();

        (new RandomSeekThreaded()).run(args.length > 0 ? args[0] : "./measurements.txt"); // , true // RandomSeekThreaded


        System.out.println(Duration.between(start, Instant.now()).toMillis()/ 1000.f);

    }
}