package com.rudra.calculation;

public class Statistics {

    private double min;
    private double max;

    private int count;

    private double sum;


    public void update(double observation) {
        if (min > observation)
            min = observation;

        if (max < observation)
            max = observation;

        count++;

        sum += observation;

    }


    public String format(String city) {

        return String.format("%s=%.1f/%.1f/%.1f", city, min, (sum / count), max);

    }

    public Statistics combine(Statistics valB) {

        if (this.min > valB.min)
            this.min = valB.min;


        if (this.max < valB.max)
            this.max = valB.max;

        this.sum += valB.sum;
        this.count += valB.count;

        return this;

    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Statistics that) {

            return this.count == that.count && this.sum == that.sum && this.min == that.min && this.max == that.max;

        } else
            return false;

    }
}
