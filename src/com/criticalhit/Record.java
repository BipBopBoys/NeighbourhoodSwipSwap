package com.criticalhit;
//Seattle Tupuhi 1286197
//Jesse Whitten 1811972
public class Record {
    private double impact;
    private Neighbourhood to,from;

    public Record(double impact, Neighbourhood to, Neighbourhood from) {
        this.impact = impact;
        this.to = to;
        this.from = from;
    }

    public double getImpact() {
        return impact;
    }

    public void setImpact(double impact) {
        this.impact = impact;
    }

    public Neighbourhood getTo() {
        return to;
    }

    public void setTo(Neighbourhood to) {
        this.to = to;
    }

    public Neighbourhood getFrom() {
        return from;
    }

    public void setFrom(Neighbourhood from) {
        this.from = from;
    }
}
