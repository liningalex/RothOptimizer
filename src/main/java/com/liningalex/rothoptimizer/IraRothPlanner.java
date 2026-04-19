package com.liningalex.rothoptimizer;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;


public class IraRothPlanner {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("i", "ira", true, "ira balance");
        options.addOption("x", "inflation", true, "inflation rate");
        options.addOption("k", "brokerage", true, "brokerage balance");
        options.addOption("b", "born", true, "born year");
        options.addOption("p", "spending", true, "expense");
        options.addOption("s", "ssnIncome", true, "ssn income");
        options.addOption("a", "ssnAge", true, "age to get ssn income");
        options.addOption("y", "yearBegin", true, "year begin to convert");
        options.addOption("r", "investRtn", true, "investment return");
        options.addOption("t", "propertyTax", true, "property tax");
        options.addOption("m", "mortgage", true, "mortgage interest");
        options.addOption("d", "donation", true, "donation");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            int[] born = parseArrayInt(cmd.getOptionValue("born", "{1965,1968}"));
            double[] ira = parseArrayDouble(cmd.getOptionValue("ira", "{1250000,1250000}"));
            double[] brok = parseArrayDouble(cmd.getOptionValue("brokerage", "{35000,54380}"));
            double expense = Double.parseDouble(cmd.getOptionValue("spending", "100000"));
            double inflation = Double.parseDouble(cmd.getOptionValue("inflation", "0.025"));
            int[] ssnAge = parseArrayInt(cmd.getOptionValue("ssnAge", "{67,67}"));
            double[] ssnIncome = parseArrayDouble(cmd.getOptionValue("ssnIncome", "{48000, 48000}"));
            double ivtReturn = Double.parseDouble(cmd.getOptionValue("investRtn", "0.08"));
            int yearBegin = Integer.parseInt(cmd.getOptionValue("yearBegin", "2025"));
            int propertyTax = Integer.parseInt(cmd.getOptionValue("propertyTax", "0"));
            int mortgage = Integer.parseInt(cmd.getOptionValue("mortgage", "0"));
            int donation = Integer.parseInt(cmd.getOptionValue("donation", "0"));

            RothConversionCalculator rothConversionCalculator = new RothConversionCalculator(ivtReturn, ira, brok, ssnIncome, ssnAge,
                    yearBegin, born, propertyTax, mortgage, donation, inflation);
            System.out.println("No optmiazation, no state tax");
            //System.out.println(rothConversionCalculator.rothBalance(expense, ira[0] + ira[1], 1));
            System.out.println("No optmiazation, having state tax");
            System.out.println(rothConversionCalculator.rothBalance(expense, 0, 0));
            System.out.println("Having optmiazation, having state tax");
            System.out.println(rothConversionCalculator.optimalConversion(expense,0));
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
