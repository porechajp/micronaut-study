package com.rudra.calculation.parsers;

import com.rudra.calculation.Statistics;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SerialParser extends ParserBase {

    private final String fileName;
    private final int bufLength;


    public SerialParser(String fileName) {
        this(fileName,8192);
    }
    public SerialParser(String fileName, int bufLength){
        this.fileName = fileName;
        this.bufLength = bufLength;
    }


    @Override
    public Map<String, Statistics> parse(){

        Map<String,Statistics> observations = new HashMap<>(1000);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName), bufLength)) {

            byte[] buf = new byte[bufLength];
            int readLength, dataLength, off = 0;


            while ((readLength = bis.read(buf, off, bufLength - off)) != -1) {

                dataLength = readLength + off;

                int incompleteDataIndex = parseBuffer(buf, dataLength, observations);

                if (incompleteDataIndex != -1) {
                    System.arraycopy(buf, incompleteDataIndex, buf, 0, dataLength - incompleteDataIndex);
                    off = dataLength - incompleteDataIndex;
                }
                else{
                    off = 0;
                }





            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return observations;

    }




}
