package com.liningalex.rothoptimizer;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;


public class IraRothPlanner {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("i", "ira", true, "ira balance");
        options.addOption("a", "age", true, "ages");
        options.addOption("b", "born", true, "born year");
        options.addOption("f", "fixIncome", true, "fix income");
        options.addOption("s", "ssnIncome", true, "ssn income at age 67");
        options.addOption("y", "yearBegin", true, "year begin to convert");
        options.addOption("p", "payTaxInIra", true, "pay tax from ira account");
        options.addOption("r", "investRtn", true, "investment return");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            int[] age = parseArrayInt(cmd.getOptionValue("age", "{64,62}"));
            int[] born = parseArrayInt(cmd.getOptionValue("born", "{1965,1968}"));
            double[] ira = parseArrayDouble(cmd.getOptionValue("ira", "{100000,180000}"));
            double fixIncome = Double.parseDouble(cmd.getOptionValue("fixIncome", "11000"));
            double[] ssnIncome = parseArrayDouble(cmd.getOptionValue("ssnIncome", "{18000, 28000}"));
            boolean payTaxInIra = Boolean.parseBoolean(cmd.getOptionValue("payTaxInIra", "True"));
            double investRtn = Double.parseDouble(cmd.getOptionValue("investRtn", "0.05"));
            int yearBegin = Integer.parseInt(cmd.getOptionValue("yearBegin", "2025"));

            RothConversionCalculator rothConversionCalculator = new RothConversionCalculator(fixIncome, investRtn, age, ira, ssnIncome, yearBegin, payTaxInIra, born);
            rothConversionCalculator.rothBalance(fixIncome, false);
            System.out.println(rothConversionCalculator.getDetails());
            rothConversionCalculator.rothBalance(fixIncome, true);
            System.out.println(rothConversionCalculator.getDetails());

            double maxRoth = Double.MIN_VALUE;
            StringBuffer best = null;
            for (double i = 0; i < ira[0] + ira[1]; i += 100) {
                double[] roth = rothConversionCalculator.rothBalance(fixIncome + i, true);
                if (roth[0] > maxRoth) {
                    maxRoth = roth[0];
                    best = rothConversionCalculator.getDetails();
                }
            }
            System.out.println(best);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    static int[] parseArrayInt(String arg) {
        String[] value = StringUtils.strip(arg, "{}").split(",");
        return Arrays.stream(value).mapToInt(Integer::parseInt).toArray();
    }

    static double[] parseArrayDouble(String arg) {
        String[] value = StringUtils.strip(arg, "{}").split(",");
        return Arrays.stream(value).mapToDouble(Double::parseDouble).toArray();
    }

}
