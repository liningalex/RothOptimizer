package com.liningalex.rothoptimizer;

import java.util.List;

public class RothConvResults {
    List<YearConvResults> yearConvResultsList;
    final double income;
    final double roth;
    final double brok;
    final double ira;
    final double totalTax;
    final double asset;
    final double totalAmd;


    public RothConvResults(List<YearConvResults> yearConvResultsList, double income, double roth, double brok, double ira, double totalTax, double asset, double totalAmd) {
        this.yearConvResultsList = yearConvResultsList;
        this.income = income;
        this.roth = roth;
        this.brok = brok;
        this.ira = ira;
        this.totalTax = totalTax;
        this.asset = asset;
        this.totalAmd = totalAmd;
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
        final double[] brok;

        public YearConvResults(int year, int[] age, double[] iraBalance, double[] rothBalance, double[] rmd, double[] toRoth, double[] medicare, double income, double fedTax, double calTax, long fedDeduction, double[] brok) {
            this.year = year;
            this.age = age.clone();
            this.ira = iraBalance.clone();
            this.roth = rothBalance.clone();
            this.rmd = rmd.clone();
            this.toRoth = toRoth.clone();
            this.brok = brok.clone();
            this.income = income;
            this.fedTax = fedTax;
            this.calTax = calTax;
            this.medicare = medicare.clone();
            this.fedDeduction = fedDeduction;
        }

        @Override
        public String toString() {
            return String.format("Year=%d, age=(%d,%d),ira=(%7.0f,%7.0f),roth=(%8.0f,%8.0f), brok" +
                            "=(%8.0f,%2.2f),rmd=(%6.0f,%6.0f),conv=(%6.0f,%6.0f), AGI=%.0f-%d," +
                            "medicare=%5.0f,tax=(%6.0f+%6.0f),%2.1f%%",
                    year, age[0], age[1], ira[0], ira[1], roth[0], roth[1], brok[1] , brok[0] > 0 ? brok[1] / brok[0] : 0, rmd[0], rmd[1], toRoth[0], toRoth[1], income, fedDeduction,
                    medicare[0] + medicare[1], fedTax, calTax, (fedTax + calTax) / income * 100);
        }
    }

    @Override
    public String toString() {
        return String.join("\n", yearConvResultsList.stream().map(String::valueOf).toList()) + "\n" +
                String.format("income=%.0f,asset=%.0f(roth=%.0f, brokerage=%.0f, ira=%.0f),totalTax=%.0f",
                        income, asset, roth, brok, ira, totalTax) + "\n";
    }
}
