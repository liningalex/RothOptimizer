import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;


public class IraRothPlanner {
    public static void main(String[] args) {
        int[] age;
        double[] ira;
        double fixIncome;
        double[] ssnIncome;
        boolean payTaxInIra;
        double investRtn;
        int yearBegin;

        Options options = new Options();
        options.addOption("i", "ira", true, "ira balance");
        options.addOption("a", "age", true, "ages");
        options.addOption("f", "fixIncome", true, "fix income");
        options.addOption("s", "ssnIncome", true, "ssn income at age 67");
        options.addOption("y", "yearBegin", true, "year begin to convert");
        options.addOption("p", "payTaxInIra", true, "pay tax from ira account");
        options.addOption("r", "investRtn", true, "investment return");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            age = parseArrayInt(cmd.getOptionValue("age", "{64,62}"));
            ira = parseArrayDouble(cmd.getOptionValue("ira", "{100000,180000}"));
            fixIncome = Double.parseDouble(cmd.getOptionValue("fixIncome", "11000"));
            ssnIncome = parseArrayDouble(cmd.getOptionValue("ssnIncome", "{18000, 28000}"));
            payTaxInIra = Boolean.parseBoolean(cmd.getOptionValue("payTaxInIra", "True"));
            investRtn = Double.parseDouble(cmd.getOptionValue("investRtn", "0.05"));
            yearBegin = Integer.parseInt(cmd.getOptionValue("yearBegin", "2025"));

            RothConversionCalculator myCal = new RothConversionCalculator(fixIncome, investRtn, age, ira, ssnIncome, yearBegin, payTaxInIra);
            myCal.rothBalance(fixIncome, false);
            System.out.println(myCal.getDetails());
            myCal.rothBalance(fixIncome, true);
            System.out.println(myCal.getDetails());
            double maxRoth = Double.MIN_VALUE;
            StringBuffer best = null;
            for (double i = 0; i < ira[0] + ira[1]; i += 1000) {
                double roth = myCal.rothBalance(fixIncome + i, true);
                if (roth > maxRoth) {
                    maxRoth = roth;
                    best = myCal.getDetails();
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
