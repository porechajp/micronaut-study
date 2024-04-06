package com.rudra.calculation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Simple {


    public void run(String fileName){

        Map<String, Statistics> dict = new HashMap<>(10000);


        try(var reader = new BufferedReader(new FileReader(fileName),131072 )){

            String line = null;


            while((line = reader.readLine()) != null && !line.isEmpty()){

                int ind = line.indexOf(';');
                var city = line.substring(0,ind);
                var temp = Double.parseDouble(line.substring(ind+1));

                var stats = dict.computeIfAbsent(city, k -> new Statistics());

                stats.update(temp);

            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(var entry : dict.entrySet()){

            System.out.println(entry.getValue().format(entry.getKey()));

        }

    }



}
