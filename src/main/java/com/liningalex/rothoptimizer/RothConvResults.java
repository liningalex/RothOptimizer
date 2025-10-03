package com.liningalex.rothoptimizer;

import java.util.List;

public class RothConvResults {
    List<YearConvResults> yearConvResultsList;
    double income;
    double roth;
    double rmd;
    double lastTax;
    double totalTax;

    public RothConvResults(List<YearConvResults> yearConvResultsList, double income, double roth, double rmd, double lastTax, double totalTax) {
        this.yearConvResultsList = yearConvResultsList;
        this.income = income;
        this.roth = roth;
        this.rmd = rmd;
        this.lastTax = lastTax;
        this.totalTax = totalTax;
    }

    public static class YearConvResults {
        final int year;
        final int[] age;
        final double[] roth;
        final double[] toRoth;
        final double[] rmd;
        final double[] ira;
        final double income;
        final double fedTax;
        final double calTax;
        final double[] medicare;
        final long fedDeduction;

        public YearConvResults(int year, int[] age, double[] iraBalance, double[] rothBalance, double[] rmd, double[] toRoth, double[] medicare, double income, double fedTax, double calTax, long fedDeduction) {
            this.year = year;
            this.age = age.clone();
            this.ira = iraBalance.clone();
            this.roth = rothBalance.clone();
            this.rmd = rmd.clone();
            this.toRoth = toRoth.clone();
            this.income = income;
            this.fedTax = fedTax;
            this.calTax = calTax;
            this.medicare = medicare.clone();
            this.fedDeduction = fedDeduction;
        }

        @Override
        public String toString() {
            return String.format("Year=%d, age=(%d,%d),ira=(%7.0f,%7.0f),roth=(%7.0f,%7.0f),rmd=(%6.0f,%6.0f),conv=(%6.0f,%6.0f), AGI=%.0f-%d," +
                            "medicare=%5.0f,tax=(%6.0f+%6.0f),%2.1f%%",
                    year, age[0], age[1], ira[0], ira[1], roth[0], roth[1], rmd[0], rmd[1], toRoth[0], toRoth[1], income, fedDeduction,
                    medicare[0] + medicare[1], fedTax, calTax, (fedTax + calTax) / income * 100);
        }
    }

    @Override
    public String toString() {
        return String.join("\n", yearConvResultsList.stream().map(String::valueOf).toList()) + "\n" +
                String.format("income=%.0f,roth=%.0f,rmd=%.0f,lastTax=%.0f,totalTax=%.0f",
                        income, roth, rmd, lastTax, totalTax + lastTax) + "\n";
    }
}
