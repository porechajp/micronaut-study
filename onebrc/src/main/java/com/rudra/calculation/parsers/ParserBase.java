package com.rudra.calculation.parsers;

import com.rudra.calculation.Statistics;

import java.util.Map;

public abstract class ParserBase {


    public abstract Map<String, Statistics> parse();

    protected int parseBuffer(byte[] buf, int dataLength, Map<String, Statistics> observations) {

        int ind = 0, sepInd = 0;

        while (ind < dataLength) {

            sepInd = findSeparator(buf, ind, dataLength);

            // no separator found
            if (sepInd == -1)
                return ind;

            final double[] parsedObservs = parseObservation(buf, sepInd + 1, dataLength);

            // no new line found
            if (parsedObservs[1] == -1)
                return ind;

            var key = new String(buf, ind, sepInd - ind);

            var stats = observations.computeIfAbsent(key, k -> new Statistics());
            stats.update(parsedObservs[0]);

            //observations.compute(new String(buf, ind, sepInd - ind), (k, v) -> v == null ? new Statistics().update(parsedObservs[0]) : v.update(parsedObservs[0]));

            ind = (int) parsedObservs[1] + 1;


        }

        return -1;

    }

    protected int findSeparator(byte[] data, int startInd, int length) {
        int i = startInd;

        while (i < length) {

            if (data[i] == ';') {
                return i;
            }

            i++;

        }
        return -1;
    }

    protected double[] parseObservation(byte[] buf, int offset, int length) {
        int i = offset;
        double result = 0.0;
        double divisor = 0;

        double multiplier = 1;

        while (i < length) {


            if (buf[i] == '\n') {

                return new double[]{multiplier * result / (divisor == 0 ? 1 : divisor), i};

            }


            if (buf[i] == '.') {
                divisor = 1;
            } else if (buf[i] == '-') {
                multiplier = -1.0;
            } else {

                result = result * 10 + (buf[i] - 48);

                if (divisor > 0)
                    divisor = divisor * 10;

            }

            i++;
        }

        return new double[]{0, -1};

    }
}
