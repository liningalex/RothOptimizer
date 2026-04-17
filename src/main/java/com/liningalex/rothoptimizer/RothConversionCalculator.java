package com.liningalex.rothoptimizer;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

public class RothConversionCalculator {

    public final double[][] fedTaxBracket = {
            {
                    10, 0, 0
            },
            {
                    12, 23850, 11925
            },
            {
                    22, 96950, 48475
            },
            {
                    24, 206700, 103350
            },
            {
                    32, 394600, 197300
            },
            {
                    35, 501050, 250525
            },
            {
                    37, 751600, 626350
            }
    };

    public final double[][] irmaaTbl = {
            {218000, 109000, 284, 40},
            {274000, 137000, 405, 40 + 14.50},
            {342000, 171000, 527, 40 + 37.50},
            {410000, 205000, 649.90, 40 + 60.40},
            {750000, 500000, 689.90, 40 + 83.30},
            {Double.MAX_VALUE, Double.MAX_VALUE, 628.90, 40 + 91}
    };

    public final double[][] calTaxBracket = {
            {
                    1, 0, 0
            },
            {
                    2, 21512, 10757
            },
            {
                    4, 50999, 25499
            },
            {
                    6, 80491, 40245
            },
            {
                    8, 111733, 55866
            },
            {
                    9.3, 141213, 70606
            },
            {10.3, 721319, 360659
            },
            {
                    11.3, 865575, 432787
            },
            {
                    12.3, 1442628, 721314
            }
    };

    public final double[] rmdTable = {
            // 73, 74,  75,   76,   77,   78,   79,   80,   81,   82,   83,   84,   85,   86,   87,   88,   89,   90,   91
            26.5, 25.5, 24.6, 23.7, 22.9, 22.0, 21.1, 20.2, 19.4, 18.5, 17.7, 16.8, 16.0, 15.2, 14.4, 13.7, 12.9, 12.2, 11.5,
            //92, 93,   94,  95,  96,  97,  98,  99,  100, 101, 102, 103
            10.8, 10.1, 9.5, 8.9, 8.4, 7.8, 7.3, 6.8, 6.4, 6.0, 5.6, 5.2

    };
    public final long fedDeductionDefault = 31500;
    public final long calDeductionDefault = 11080;
    final int[] rmdAge = new int[2];
    final int[] born;
    public final int[] life = {88, 89};
    final double[] iraBegin;
    final double[] brokBegin;
    final double spending;
    final double inflation;
    final double investRtn;
    final double[] ssnIncome;
    final int[] ssnAge;
    final int yearBegin;
    final int propertyTax;
    final int mortgage;
    final int donation;

    long fedDeduction(int[] age, double income, boolean calTax, double[] ssnIncome) {
        long stdAmount = this.fedDeductionDefault;
        boolean isJoint = true;
        for (int person = 0; person < 2; person++) {
            if (age[person] >= 65) {
                stdAmount += 1600;
                if (income < 150000)
                    stdAmount += 6000;
            }
            if (age[person] > life[person]) {
                isJoint = false;
            }
        }
        long itemized = 0;
        long localTax = 0;
        if (income < 505000) {
            if (calTax)
                localTax = taxAmount(income - calDeduction(age, ssnIncome), calTaxBracket, isJoint);
            itemized = Math.min(localTax + mortgage + propertyTax + donation, 40400);
        } else {
            if (calTax)
                localTax = taxAmount(income - calDeduction(age, ssnIncome), calTaxBracket, isJoint);
            itemized = (long) Math.min(localTax + mortgage + propertyTax + donation, 40400 - (income - 505000) * 0.3);
        }
        double ssnDeduction = ssnIncome(age, 0, ssnIncome) + ssnIncome(age, 1, ssnIncome);
        if (income > 44000)
            ssnDeduction *= 0.15;
        else if (income > 32000)
            ssnDeduction *= 0.5;

        return Math.max(stdAmount, itemized) + (long) ssnDeduction;
    }

    long calDeduction(int[] age, double[] ssnIncome) {
        double ssnInc = ssnIncome(age, 0, ssnIncome) + ssnIncome(age, 1, ssnIncome);
        return Math.max(calDeductionDefault, donation + mortgage + propertyTax) + (long) ssnInc;
    }

    public RothConversionCalculator(double fixIncome, double investRtn, double[] ira, double[] brok, double[] ssnIncome, int[] ssnAge,
                                    int yearBegin, int[] born, int propertyTax, int mortgage, int donation, double inflation) {
        this.spending = fixIncome;
        this.investRtn = investRtn;
        this.iraBegin = ira.clone();
        this.brokBegin = brok.clone();
        this.ssnIncome = ssnIncome;
        this.ssnAge = ssnAge;
        this.inflation = inflation;
        this.yearBegin = yearBegin;
        this.born = born;
        for (int person = 0; person < 2; person++) {
            this.rmdAge[person] = rmdAge(born[person]);
        }
        this.propertyTax = propertyTax;
        this.mortgage = mortgage;
        this.donation = donation;
    }


    int rmdAge(int born) {
        if (born < 1949)
            return 70;
        else if (born < 1951)
            return 71;
        else if (born < 1960)
            return 73;
        else
            return 75;
    }

    double ssnIncome(int[] age, int person, double[] ssnIncome) {
        if (age[person] >= ssnAge[person] && age[person] <= life[person]) {
            return ssnIncome[person];
        } else
            return 0;
    }

    double[] withDrawBrokerage(double[] brokBalance, double amount) {
        double[] rtn = {0, 0};
        if (amount > 0 && brokBalance[1] > 0) {
            rtn[0] = Math.min(amount, brokBalance[1]);
            rtn[1] = rtn[0] * (brokBalance[1] - brokBalance[0]) / brokBalance[1];
            brokBalance[0] -= rtn[0] * brokBalance[0] / brokBalance[1];
            brokBalance[1] -= rtn[0];
        } else if (amount < 0) {
            brokBalance[0] -= amount;
            brokBalance[1] -= amount;
        }
        return rtn;
    }

    double withDraw(double amount, double[] brokBalance, double[] iraBalance, double[] rothBalance) {
        double[] amtAndGain = withDrawBrokerage(brokBalance, amount);
        double taxableIncome = amtAndGain[1];
        int[] age = {60, 60};
        if ((amount - amtAndGain[0]) > 0) {
            taxableIncome += amount - amtAndGain[0];
            double[] convRatio = convRatio(iraBalance, age, amount - amtAndGain[0]);
            for (int person = 0; person < 2; person++) {
                double amt = convRatio[person] * (amount - amtAndGain[0]);
                iraBalance[person] -= amt;
                if (iraBalance[person] < 0) {
                    rothBalance[person] += iraBalance[person];
                    taxableIncome += iraBalance[person];
                    iraBalance[person] = 0;
                }
            }
            if (rothBalance[0] < 0) {
                rothBalance[1] += rothBalance[0];
                rothBalance[0] = 0;
            } else if (rothBalance[1] < 0) {
                rothBalance[0] += rothBalance[1];
                rothBalance[1] = 0;
            }
        }
        return taxableIncome;
    }

    RothConvResults rothBalance(double spending, double taxableIncomeGoal, int noCalYears) {
        int[] age = {yearBegin - born[0], yearBegin - born[1]};
        double[] iraBalance = iraBegin.clone();
        double totalTax = 0;
        double[] rothBalance = new double[2];
        double[] brokBalance = brokBegin.clone();
        double[] rmd = new double[2];
        double[][] fedTaxBracket = SerializationUtils.clone(this.fedTaxBracket.clone());
        double[][] irmaaTbl = SerializationUtils.clone(this.irmaaTbl.clone());
        double[][] calTaxBracket = SerializationUtils.clone(this.calTaxBracket.clone());
        double[] ssnIncome = this.ssnIncome.clone();
        List<RothConvResults.YearConvResults> yearConvResultsList = new ArrayList<>();
        for (int year = yearBegin; (age[0] <= life[0]) || (age[1] <= life[1]); year++) {
            double taxableIncome = ssnIncome(age, 0, ssnIncome) + ssnIncome(age, 1, ssnIncome);
            taxableIncome += withDraw(spending - taxableIncome, brokBalance, iraBalance, rothBalance);

            double[] toRoth = new double[2];
            double[] medicareOrig = new double[2];
            double[] medicare = new double[2];
            boolean isJoint = true;
            for (int person = 0; person < 2; person++) {
                // rmd amount
                rmd[person] = rmdAmount(age, iraBalance, person);
                brokBalance[0] += rmd[person];
                brokBalance[1] += rmd[person];
                iraBalance[person] -= rmd[person];
                taxableIncome += rmd[person];
                medicareOrig[person] = medicarePreminus(irmaaTbl, age, taxableIncome, person, isJoint);
                if (age[person] > life[person]) {
                    isJoint = false;
                }
            }

            // original tax amount.
            double taxOrig = taxAmount(taxableIncome - fedDeduction(age, taxableIncome, noCalYears <= 0, ssnIncome), fedTaxBracket, isJoint);
            if (noCalYears <= 0) {
                taxOrig += taxAmount(taxableIncome - calDeduction(age, ssnIncome), calTaxBracket, isJoint);
            }

            // amount to convert is max of ira balance.
            double convertAmount = Math.min(taxableIncomeGoal - taxableIncome, iraBalance[0] + iraBalance[1]);
            double[] convRatio = convRatio(iraBalance, age, convertAmount);
            if (convertAmount > 0) {
                for (int person = 0; person < 2; person++) {
                    if (iraBalance[person] > 0) {
                        toRoth[person] = Math.min(iraBalance[person], convRatio[person] * convertAmount);
                        iraBalance[person] -= toRoth[person];
                        rothBalance[person] += toRoth[person];
                        taxableIncome += toRoth[person];
                    }
                }

                // tax amount with updated taxableIncome.
                double tax = taxAmount(taxableIncome - fedDeduction(age, taxableIncome, noCalYears > 0, ssnIncome), fedTaxBracket, isJoint);
                if (noCalYears <= 0) {
                    tax += taxAmount(taxableIncome - calDeduction(age, ssnIncome), calTaxBracket, isJoint);
                }

                // medicare diffs with updated taxableIncome.
                double medicareDiff = 0;
                for (int person = 0; person < 2; person++) {
                    medicareDiff += medicarePreminus(irmaaTbl, age, taxableIncome, person, isJoint) - medicareOrig[person];
                }

                // extra tax caused by conversion.
                double additionalTax = tax - taxOrig + medicareDiff;
                taxableIncome += withDraw(additionalTax, brokBalance, iraBalance, rothBalance);
            }

            // tax with final updated taxableIncome.
            long fedDeduction = fedDeduction(age, taxableIncome, noCalYears <= 0, ssnIncome);
            double fedTax = taxAmount(taxableIncome - fedDeduction, fedTaxBracket, isJoint);
            double calTax = 0;
            if (noCalYears <= 0) {
                calTax = taxAmount(taxableIncome - calDeduction(age, ssnIncome), calTaxBracket, isJoint);
            }
            totalTax += fedTax + calTax;
            // medicare with the final taxableIncome.
            for (int person = 0; person < 2; person++) {
                medicare[person] = medicarePreminus(irmaaTbl, age, taxableIncome, person, isJoint);
            }

            yearConvResultsList.add(new RothConvResults.YearConvResults(year, age, iraBalance, rothBalance, rmd, toRoth, medicare, taxableIncome, fedTax, calTax, fedDeduction, brokBalance));
            // investment return;
            for (int person = 0; person < 2; person++) {
                if (age[person] <= life[person]) {
                    age[person]++;
                }
                iraBalance[person] *= (1 + investRtn);
                rothBalance[person] *= (1 + investRtn);
            }
            brokBalance[1] *= (1 + investRtn);
            spending *= (1 + inflation);
            taxableIncomeGoal *= (1 + inflation);
            noCalYears--;
            for (double[] i : fedTaxBracket) {
                i[1] *= (1 + inflation);
                i[2] *= (1 + inflation);
            }
            for (double[] i : calTaxBracket) {
                i[1] *= (1 + inflation);
                i[2] *= (1 + inflation);
            }
            for (double[] i : irmaaTbl) {
                i[0] *= (1 + inflation);
                i[1] *= (1 + inflation);
            }
            for (int i = 0; i < ssnIncome.length; i++) {
                ssnIncome[i] *= (1 + inflation);
            }
            if (age[0] > life[0]) {
                iraBalance[1] += iraBalance[0];
                iraBalance[0] = 0;
            }
            if (age[1] > life[1]) {
                iraBalance[0] += iraBalance[1];
                iraBalance[1] = 0;
            }
            if (rothBalance[0] < 0 || rothBalance[1] < 0) {
                break;
            }
        }
        double lastTax = taxAmount(iraBalance[0] + iraBalance[1] - fedDeductionDefault, fedTaxBracket, false);
        if (noCalYears <= 0) {
            lastTax += taxAmount(iraBalance[0] + iraBalance[1] - calDeduction(age, ssnIncome), calTaxBracket, false);
        }
        double[] convRatio = convRatio(iraBalance, age, lastTax);
        for (int person = 0; person < 2; person++) {
            iraBalance[person] -= lastTax * convRatio[person];
            rothBalance[person] += iraBalance[person];
            iraBalance[person] = 0;
        }

        RothConvResults rothConvResults = new RothConvResults(yearConvResultsList, spending, rothBalance[0] + rothBalance[1],
                brokBalance[1], lastTax, totalTax + lastTax);

        return rothConvResults;
    }

    RothConvResults optimalConversion(int noCalYears) {
        double maxAsset = -Double.MAX_VALUE;
        RothConvResults best = null;
        for (double i = spending / 2; i < iraBegin[0] + iraBegin[1]; i += 100) {
            RothConvResults results = rothBalance(spending, i, noCalYears);
            if (results.roth + results.brok > maxAsset) {
                maxAsset = results.roth + results.brok;
                best = results;
            }
        }
        return best;
    }

    double[] convRatio(double[] iraBalance, int[] age, double amount) {
        double[] ratio = new double[2];
        double[] ave = {1, 1};
        if ((rmdAge[0] - age[0] - 1) <= 0 && iraBalance[0] > 0) {
            ave[0] = Math.min(amount, iraBalance[0]);
            ave[1] = amount - ave[0];
        } else if (rmdAge[1] - age[1] - 1 <= 0 && iraBalance[1] > 0) {
            ave[1] = Math.min(amount, iraBalance[1]);
            ave[0] = amount - ave[1];
        } else if (iraBalance[0] + iraBalance[1] > 0) {
            if (amount >= (iraBalance[0] + iraBalance[1])) {
                ave[0] = iraBalance[0];
                ave[1] = iraBalance[1];
            } else {
                ave[0] = iraBalance[0] / (rmdAge[0] - age[0] - 1);
                ave[1] = iraBalance[1] / (rmdAge[1] - age[1] - 1);
            }
        }
        ratio[0] = ave[0] / (ave[0] + ave[1]);
        ratio[1] = ave[1] / (ave[0] + ave[1]);
        return ratio;
    }

    long rmdAmount(int[] age, double[] ira, int person) {
        double rmd = 0;
        if (age[person] >= rmdAge[person] && ira[person] > 0 && age[person] <= life[person]) {
            rmd = ira[person] / rmdTable[age[person] - rmdAge[person]];
            ira[person] -= rmd;
        }
        return (long) rmd;
    }

    long medicarePreminus(double[][] irmaaTbl, int[] age, double income, int person, boolean isJoint) {
        double preminus = 0;
        int idx = isJoint ? 0 : 1;
        if (age[person] >= 65 && age[person] <= life[person]) {
            for (int i = 0; i < irmaaTbl.length; i++) {
                if (income <= irmaaTbl[i][idx]) {
                    return (long) (irmaaTbl[i][2] + irmaaTbl[i][3]) * 12;
                }
            }
        }
        return (long) preminus;
    }

    long taxAmount(double income, double[][] taxRate, boolean isJoint) {
        int idx = (isJoint ? 1 : 2);
        double tax = 0;
        for (int i = 0; i < taxRate.length; i++) {
            if (income > taxRate[i][idx]) {
                if (i < taxRate.length - 1 && income > taxRate[i + 1][idx]) {
                    tax += (taxRate[i + 1][idx] - taxRate[i][idx]) * 0.01 * taxRate[i][0];
                } else {
                    tax += (income - taxRate[i][idx]) * 0.01 * taxRate[i][0];
                }
            }
        }
        if (tax < 0) {
            tax = 0;
        }
        return (long) tax;
    }
}