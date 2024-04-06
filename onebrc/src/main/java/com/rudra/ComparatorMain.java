package com.rudra;

import com.rudra.calculation.RandomSeekThreaded;
import com.rudra.calculation.StreamBased;

import java.time.Duration;
import java.time.Instant;

public class ComparatorMain {

    public static void main(String[] args) throws Exception {


        //  CreateMeasurements.generate(1000_000_000);


        var seekData = (new RandomSeekThreaded()).parse("./mini_measurements.txt"); // , true // RandomSeekThreaded

        var streamData = (new StreamBased()).parse("./mini_measurements.txt");


        seekData.forEach((k, v) -> {

                    var streamStats = streamData.get(k);

                    if (streamStats == null)
                        System.out.println(k + " not found in stream");
                    else if (!streamStats.equals(v)) {
                        System.out.println("Difference Found : " + v.format(k) + " vs " + streamStats.format(k));
                    }

                }
        );


        streamData.forEach((k, v) -> {

                    var seekStats = seekData.get(k);

                    if (seekStats == null)
                        System.out.println(k + " not found in seek");
                    else if (!seekStats.equals(v)) {
                        System.out.println("Difference Found : " + v.format(k) + " vs " + seekStats.format(k));
                    }

                }
        );

    }
}
