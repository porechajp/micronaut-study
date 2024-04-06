package com.rudra.calculation.parsers;

import com.rudra.calculation.Statistics;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class RandomAccessParser extends ParserBase {

    private final String fileName;
    private final int bufLength;

    private final long parseLimit;
    private final long seekStart;


    public RandomAccessParser(String fileName) {
        this(fileName, 8192, 0, new File(fileName).length());
    }

    public RandomAccessParser(String fileName, int bufLength) {
        this(fileName, bufLength, 0, new File(fileName).length());
    }


    public RandomAccessParser(String fileName, int bufLength, long seekStart, long parseLimit) {
        this.fileName = fileName;
        this.bufLength = bufLength;
        this.seekStart = seekStart;
        this.parseLimit = parseLimit;
    }

    @Override
    public Map<String, Statistics> parse() {

        Map<String, Statistics> observations = new HashMap<>(1000);

        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {

            raf.seek(seekStart);

            byte[] buf = new byte[bufLength];
            int readLength, dataLength, off = 0;

            int bufReadLength = 0;

            bufReadLength = calculateBufReadLength(off, raf);

            while (bufReadLength > 0 && (readLength = raf.read(buf, off, bufReadLength)) != -1) {

                dataLength = readLength + off;

                int incompleteDataIndex = parseBuffer(buf, dataLength, observations);

                if (incompleteDataIndex != -1) {
                    System.arraycopy(buf, incompleteDataIndex, buf, 0, dataLength - incompleteDataIndex);
                    off = dataLength - incompleteDataIndex;
                }
                else {
                    off=0;
                }



                bufReadLength = calculateBufReadLength(off, raf);

            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return observations;

    }

    private int calculateBufReadLength(int off, RandomAccessFile raf) throws IOException {

        long fileReadLimit = parseLimit - raf.getFilePointer();

        int bufReadLength = bufLength - off;

        if(fileReadLimit < bufReadLength)
            return (int)fileReadLimit;

        return bufReadLength;
    }
}
