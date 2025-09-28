package com.liningalex.rothoptimizer;

public class RothConversionCalculator {

    public final double[][] fedTaxRate = {
            {
                    10, 0
            },
            {
                    12, 23850
            },
            {
                    22, 96950
            },
            {
                    24, 206700
            },
            {
                    32, 394600
            },
            {
                    35, 501050
            },
            {
                    37, 751600
            }
    };

    public final double[][] irmaaTbl = {
            {212000, 185, 40},
            {266000, 259, 40 + 13.7},
            {334000, 370, 40 + 35.30},
            {400000, 480.90, 40 + 57},
            {750000, 591.90, 40 + 78.60},
            {Double.MAX_VALUE, 628.90, 40 + 85}
    };

    public final double[][] calTaxRate = {
            {
                    1, 0
            },
            {
                    2, 21512
            },
            {
                    4, 50999
            },
            {
                    6, 80491
            },
            {
                    8, 111733
            },
            {
                    9.3, 141213
            },
            {10.3, 721319
            },
            {
                    11.3, 865575
            },
            {
                    12.3, 1442628
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
    final int[] ageBegin;
    public final int[] life = {88, 89};
    final double[] iraBegin;
    final double fixIncome;
    final double investRtn;
    final double[] ssnIncome;
    final int yearBegin;
    final int propertyTax;
    final int mortgage;
    final int donation;
    boolean payTaxInIra;
    StringBuffer details;

    long fedDeduction(int[] age, double income, boolean calTax) {
        long stdAmount = this.fedDeductionDefault;
        for (int person = 0; person < 2; person++) {
            if (age[person] >= 65) {
                stdAmount += 1600;
                if (income < 150000)
                    stdAmount += 6000;
            }
        }
        long itemized = 0;
        if (income < 500000) {
            if (calTax)
                itemized += taxAmount(income - calDeduction(age), calTaxRate);
            itemized += mortgage + propertyTax + donation;
        }
        double ssnDeduction = ssnIncome(age, 0) + ssnIncome(age, 1);
        if (income > 44000)
            ssnDeduction *= 0.15;
        else if (income > 32000)
            ssnDeduction *= 0.5;

        return Math.max(stdAmount, Math.min(40000, itemized)) + (long) ssnDeduction;
    }

    long calDeduction(int[] age) {
        double ssnIncome = ssnIncome(age, 0) + ssnIncome(age, 1);
        return Math.max(calDeductionDefault, donation + mortgage + propertyTax) + (long) ssnIncome;
    }

    public RothConversionCalculator(double fixIncome, double investRtn, int[] age, double[] ira, double[] ssnIncome,
                                    int yearBegin, boolean paytaxInIra, int[] born, int propertyTax, int mortgage, int donation) {
        this.fixIncome = fixIncome;
        this.investRtn = investRtn;
        this.ageBegin = age.clone();
        this.iraBegin = ira.clone();
        this.ssnIncome = ssnIncome;
        this.yearBegin = yearBegin;
        this.payTaxInIra = paytaxInIra;
        for (int person = 0; person < 2; person++) {
            this.rmdAge[person] = rmdAge(born[person]);
        }
        this.propertyTax = propertyTax;
        this.mortgage = mortgage;
        this.donation = donation;
    }

    StringBuffer getDetails() {
        return details;
    }

    void setPayTaxInIra(boolean payTaxInIra) {
        this.payTaxInIra = payTaxInIra;
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

    double ssnIncome(int[] age, int person) {
        if (age[person] >= 67) {
            return ssnIncome[person];
        } else
            return 0;
    }

    double[] rothBalance(double goalIncome, boolean calTax) {
        details = new StringBuffer();
        int[] age = ageBegin.clone();
        double[] iraBalance = iraBegin.clone();
        double totalTax = 0;
        double[] rothBalance = new double[2];
        double[] rmdBalance = new double[2];
        double[] rmd = new double[2];

        for (int year = yearBegin; (age[0] < life[0]) || (age[1] < life[1]); year++) {
            double income = fixIncome;
            double[] toRoth = new double[2];
            double[] medicareOrig = new double[2];
            double[] medicare = new double[2];
            for (int person = 0; person < 2; person++) {
                if (age[person] >= life[person]) {
                    continue;
                }
                // social security income
                income += ssnIncome(age, person);

                rmd[person] = rmdAmount(age, iraBalance, person);
                iraBalance[person] -= rmd[person];
                income += rmd[person];
                medicareOrig[person] = medicarePreminus(age, income, person);
            }

            // original tax amount.
            double taxOrig = taxAmount(income - fedDeduction(age, income, calTax), fedTaxRate);
            if (calTax) {
                taxOrig += taxAmount(income - calDeduction(age), calTaxRate);
            }

            // amount to convert is max of ira balance.
            double convertAmount = Math.min(goalIncome - income, Math.max(0, iraBalance[0] + iraBalance[1]));
            double[] convRatio = convRatio(iraBalance, age, life);
            for (int person = 0; person < 2; person++) {
                if (convertAmount > 0) {
                    if (iraBalance[person] > 0) {
                        double conv = convRatio[person] * convertAmount;
                        conv = Math.min(iraBalance[person], conv);
                        iraBalance[person] -= conv;
                        toRoth[person] += conv;
                        income += conv;
                    }
                }
            }

            // tax amount with updated income.
            double tax = taxAmount(income - fedDeduction(age, income, calTax), fedTaxRate);
            if (calTax) {
                tax += taxAmount(income - calDeduction(age), calTaxRate);
            }

            for (int person = 0; person < 2; person++) {
                medicare[person] = medicarePreminus(age, income, person);
            }

            for (int person = 0; person < 2; person++) {
                // rmd amount can't be converted to Roth, keet it separated.
                rmdBalance[person] += rmd[person];
                // amount to convert to Roth.
                rothBalance[person] += toRoth[person];
                // now additional money is needed for paying tax caused by conversion.
                if (payTaxInIra) {
                    double amountPayTax = (tax - taxOrig) * convRatio[person];
                    amountPayTax += (tax - taxOrig) * convRatio[person] * tax / income;

                    amountPayTax += medicare[person] - medicareOrig[person];
                    amountPayTax += (medicare[person] - medicareOrig[person]) * convRatio[person] * tax/income;
                    
                    income += amountPayTax;
                    // pay the amount in rmd account.
                    rmdBalance[person] -= amountPayTax;
                }
                if (rmdBalance[person] < 0) {
                    // pay the amount in ira account.
                    iraBalance[person] += rmdBalance[person];
                    rmdBalance[person] = 0;
                    if (iraBalance[person] < 0) {
                        // pay the amount in roth account.
                        rothBalance[person] += iraBalance[person];
                        toRoth[person] += iraBalance[person];
                        // this amount is tax free, so reduce income.
                        income += iraBalance[person];
                        iraBalance[person] = 0;
                    }
                }
            }
            // tax with final updated income.
            tax = taxAmount(income - fedDeduction(age, income, calTax), fedTaxRate);
            if (calTax) {
                tax += taxAmount(income - calDeduction(age), calTaxRate);
            }
            totalTax += tax;
            // medicare with the final income.
            for (int person = 0; person < 2; person++) {
                medicare[person] = medicarePreminus(age, income, person);
            }


            details.append(String.format("Year=%d, age=(%d,%d),ira=(%.0f,%.0f),roth=(%.0f,%.0f),rmd=(%.0f,%.0f),conv=(%.0f,%.0f), income=%.0f," +
                            "medicare=%.0f,tax=%.0f,taxRate=%.1f",
                    year, age[0], age[1], iraBalance[0], iraBalance[1], rothBalance[0], rothBalance[1], rmd[0], rmd[1], toRoth[0], toRoth[1], income,
                    medicare[0] + medicare[1], tax, tax / income * 100)).append("\n");
            // investment return;
            for (int person = 0; person < 2; person++) {
                age[person]++;
                iraBalance[person] *= (1 + investRtn);
                rothBalance[person] *= (1 + investRtn);
                rmdBalance[person] *= (1 + investRtn);
            }

        }
        double lastTax = taxAmount(iraBalance[0] + iraBalance[1] - fedDeductionDefault, fedTaxRate);
        if (calTax) {
            lastTax += taxAmount(iraBalance[0] + iraBalance[1] - calDeduction(age), calTaxRate);
        }
        double[] convRatio = convRatio(iraBalance, age, life);
        for (int person = 0; person < 2; person++) {
            iraBalance[person] -= lastTax * convRatio[person];
            rothBalance[person] += iraBalance[person];
            iraBalance[person] = 0;
        }

        details.append(String.format("income=%.0f,roth=%.0f,rmd=%.0f,lastTax=%.0f,totalTax=%.0f",
                goalIncome, rothBalance[0] + rothBalance[1], rmdBalance[0] + rmdBalance[1], lastTax, totalTax + lastTax)).append("\n");
        double[] rtn = new double[3];
        rtn[0] = rothBalance[0] + rothBalance[1];
        rtn[1] = rmdBalance[0] + rmdBalance[1];
        rtn[2] = totalTax + lastTax;
        return rtn;
    }

    StringBuffer optimalConversion(boolean calTax) {
        double maxRoth = Double.MIN_VALUE;
        StringBuffer best = null;
        for (double i = 0; i < iraBegin[0] + iraBegin[1]; i += 100) {
            double[] roth = rothBalance(fixIncome + i, calTax);
            if ((roth[0] + roth[1]) > maxRoth) {
                maxRoth = roth[0] + roth[1];
                best = getDetails();
            }
        }
        return best;
    }

    double[] convRatio(double[] iraBalance, int[] age, int[] life) {
        double[] ratio = {0.5, 0.5};
        if (iraBalance[0] + iraBalance[1] > 0) {
            if (age[0] <= life[0] && age[1] <= life[1]) {
                double[] ave = new double[2];
                ave[0] = iraBalance[0] / ((age[0] < rmdAge[0] ? rmdAge[0] : life[0]) - age[0] + 1);
                ave[1] = iraBalance[1] / ((age[1] < rmdAge[1] ? rmdAge[1] : life[1]) - age[1] + 1);

                ratio[0] = ave[0] / (ave[0] + ave[1]);
                ratio[1] = ave[1] / (ave[0] + ave[1]);
            } else if (age[0] <= life[0]) {
                ratio[0] = 1;
                ratio[1] = 0;
            } else {
                ratio[0] = 0;
                ratio[1] = 1;
            }
        } else if (iraBalance[0] > 0) {
            ratio[0] = 1;
            ratio[1] = 0;
        }
        return ratio;

    }

    long rmdAmount(int[] age, double[] ira, int person) {
        double rmd = 0;
        if (age[person] >= rmdAge[person] && ira[person] > 0) {
            rmd = ira[person] / rmdTable[age[person] - rmdAge[person]];
            ira[person] -= rmd;
        }
        return (long) rmd;
    }

    long medicarePreminus(int[] age, double income, int person) {
        double preminus = 0;
        if (age[person] >= 65) {
            for (int i = 0; i < irmaaTbl.length; i++) {
                if (income <= irmaaTbl[i][0]) {
                    return (long) (irmaaTbl[i][1] + irmaaTbl[i][2]) * 12;
                }
            }
        }
        return (long) preminus;
    }

    long taxAmount(double income, double[][] taxRate) {
        double tax = 0;
        for (int i = 0; i < taxRate.length; i++) {
            if (income > taxRate[i][1]) {
                if (i < taxRate.length - 1 && income > taxRate[i + 1][1]) {
                    tax += (taxRate[i + 1][1] - taxRate[i][1]) * 0.01 * taxRate[i][0];
                } else {
                    tax += (income - taxRate[i][1]) * 0.01 * taxRate[i][0];
                }
            }
        }
        if (tax < 0) {
            tax = 0;
        }
        return (long) tax;
    }
}