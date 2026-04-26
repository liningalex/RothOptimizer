package com.liningalex.rothoptimizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RothConversionCalculatorTest {
    int yearBegin = 2026;
    int[] born = {1968, 1970};
    int[] age = {yearBegin - born[0], yearBegin - born[1]};
    double[] ira = {100000, 580000};
    double[] brok = {0, 0};
    double spending = 14000;
    double[] ssnIncome = {2000 * 12, 400 * 12};
    int[] ssnAge = {67, 67};
    int propertyTax = 20000;
    int mortgage = 0;
    int donation = 0;
    double inflation = 0.025;

    RothConversionCalculator rothConversionCalculator =
            new RothConversionCalculator(0.07, ira, brok, ssnIncome, ssnAge, yearBegin, born, propertyTax, mortgage, donation, inflation, RothConversionCalculator.EvaluateMethod.MAX_10_YEARS);

    @Test
    void rothBalanceSsnIncome1() {
        double[] ssnIncome = {1000 * 12, 1000 * 12};
        int[] born = {1936, 1931};
        double[] ira = {0, 0};
        double[] brok = {0, 0};
        RothConversionCalculator rothConversionCalculator =
                new RothConversionCalculator(0.07, ira, brok, ssnIncome, ssnAge, yearBegin, born, propertyTax, mortgage, donation, inflation, RothConversionCalculator.EvaluateMethod.MAX_10_YEARS);
        RothConvResults a1 = rothConversionCalculator.rothBalance(12000, 0, 100);
        assertEquals((long) a1.roth, 0);
        assertEquals((long) a1.brok, 12840);
        assertEquals((long) a1.totalTax, 0);
    }

    void rothBalanceSsnIncome2() {

        double[] ssnIncome = {1000 * 12, 1000 * 12};
        int[] born = {1937, 1932};
        RothConversionCalculator rothConversionCalculator =
                new RothConversionCalculator(0.07, ira, brok, ssnIncome, ssnAge, yearBegin, born, propertyTax, mortgage, donation, inflation, RothConversionCalculator.EvaluateMethod.MAX_10_YEARS);
        RothConvResults a1 = rothConversionCalculator.rothBalance(12000, 0, 100);
        assertEquals((long) a1.roth, 0);
        assertEquals((long) a1.brok, 26899);
        assertEquals((long) a1.totalTax, 0);

    }

    @Test
    void brokerageBalanceBrokerageIncome1() {
        double[] brok = {40000, 40000};
        int[] born = {1936, 1931};
        double[] ssnIncome = {0, 0};
        double[] ira = {0, 0};
        RothConversionCalculator rothConversionCalculator =
                new RothConversionCalculator(0.07, ira, brok, ssnIncome, ssnAge, yearBegin, born, propertyTax, mortgage, donation, inflation, RothConversionCalculator.EvaluateMethod.MAX_10_YEARS);
        RothConvResults a1 = rothConversionCalculator.rothBalance(20000, 0, 100);
        assertEquals((long) a1.roth, 0);
        assertEquals((long) a1.brok, 21400);
        assertEquals((long) a1.totalTax, 0);
    }


    @Test
    void brokerageBalanceBrokerageIncome2() {
        double[] brok = {40000, 40000};
        int[] born = {1937, 1932};
        double[] ssnIncome = {0, 0};
        double[] ira = {0, 0};
        RothConversionCalculator rothConversionCalculator =
                new RothConversionCalculator(0.0, ira, brok, ssnIncome, ssnAge, yearBegin, born, propertyTax, mortgage, donation, 0, RothConversionCalculator.EvaluateMethod.MAX_10_YEARS);
        RothConvResults a1 = rothConversionCalculator.rothBalance(20000, 0, 100);
        assertEquals((long) a1.roth, 0);
        assertEquals((long) a1.brok, 0);
        assertEquals((long) a1.totalTax, 0);
    }

    @Test
    void iraBalanceBrokerageIncome1() {
        {
            double[] brok = {0, 0};
            int[] born = {1936, 1931};
            double[] ssnIncome = {0, 0};
            double[] ira = {40000, 40000};
            RothConversionCalculator rothConversionCalculator =
                    new RothConversionCalculator(0.0, ira, brok, ssnIncome, ssnAge, yearBegin, born, propertyTax, mortgage, donation, 0, RothConversionCalculator.EvaluateMethod.MAX_10_YEARS);
            RothConvResults a1 = rothConversionCalculator.rothBalance(60000, 0, 100);
            assertEquals((long) a1.roth, 0);
            assertEquals((long) a1.brok, 0);
            assertEquals((long) a1.ira, 9893);
            assertEquals((long) a1.yearConvResultsList.get(0).medicare[0] + a1.yearConvResultsList.get(0).medicare[1], 7776.0);
            assertEquals((long) a1.totalTax, 2331);
        }
    }


    @Test
    void iraBalanceBrokerageIncome2() {
        {
            double[] brok = {0, 0};
            int[] born = {1937, 1932};
            double[] ssnIncome = {0, 0};
            double[] ira = {20000, 20000};
            RothConversionCalculator rothConversionCalculator =
                    new RothConversionCalculator(0.0, ira, brok, ssnIncome, ssnAge, yearBegin, born, propertyTax, mortgage, donation, 0, RothConversionCalculator.EvaluateMethod.MAX_10_YEARS);
            RothConvResults a1 = rothConversionCalculator.rothBalance(20000, 0, 100);
            assertEquals((long) a1.roth, 0);
            assertEquals((long) a1.brok, 0);
            assertEquals((long) a1.ira, 0);
            assertEquals((long) a1.totalTax, 0);
        }
    }

    @Test
    void iraBalanceBrokerageIncome3() {
        {
            double[] brok = {0, 0};
            int[] born = {1936, 1931};
            double[] ssnIncome = {0, 0};
            double[] ira = {80000, 80000};
            RothConversionCalculator rothConversionCalculator =
                    new RothConversionCalculator(0.0, ira, brok, ssnIncome, ssnAge, yearBegin, born, propertyTax, mortgage, donation, 0, RothConversionCalculator.EvaluateMethod.MAX_10_YEARS);
            RothConvResults a1 = rothConversionCalculator.rothBalance(60000, 72000, 100);
            assertEquals((long) a1.roth, 12000);
            assertEquals((long) a1.brok, 0);
            assertEquals((long) a1.ira, 76259);
            assertEquals((long) a1.yearConvResultsList.get(0).medicare[0] + a1.yearConvResultsList.get(0).medicare[1], 7776.0);
            assertEquals((long) a1.totalTax, 3965);
        }
    }

    @Test
    void iraBalanceBrokerageIncome4() {
        {
            double[] brok = {0, 0};
            int[] born = {1937, 1932};
            double[] ssnIncome = {0, 0};
            double[] ira = {80000, 80000};
            RothConversionCalculator rothConversionCalculator =
                    new RothConversionCalculator(0.0, ira, brok, ssnIncome, ssnAge, yearBegin, born, propertyTax, mortgage, donation, 0, RothConversionCalculator.EvaluateMethod.MAX_10_YEARS);
            RothConvResults a1 = rothConversionCalculator.rothBalance(60000, 72000, 100);
            assertEquals((long) a1.roth, 17413);
            assertEquals((long) a1.brok, 0);
            assertEquals((long) a1.ira, 0);
            assertEquals((long) a1.totalTax, 7035);
        }
    }

    @Test
    void convRatio1() {
        int[] age = {60, 60};
        double[] ira = {10, 10};
        double[] a = rothConversionCalculator.convRatio(ira, age, 10);
        assertEquals(a[0], 0.5);
        assertEquals(a[1], 0.5);
    }

    @Test
    void convRatio2() {
        int[] age = {60, 60};
        double[] ira = {10, 20};
        double[] a = rothConversionCalculator.convRatio(ira, age, 20);
        assertEquals(a[0], 0.33333333333333337);
        assertEquals(a[1], 0.6666666666666667);
    }

    @Test
    void convRatio3() {
        int[] age = {74, 60};
        double[] ira = {10, 10};
        double[] a = rothConversionCalculator.convRatio(ira, age, 10);
        assertEquals(a[0], 1.0);
        assertEquals(a[1], 0.0);
    }

    @Test
    void convRatio4() {
        int[] age = {74, 60};
        double[] ira = {10, 10};
        double[] a = rothConversionCalculator.convRatio(ira, age, 15);
        assertEquals(a[0], 0.6666666666666666);
        assertEquals(a[1], 0.3333333333333333);
    }

    @Test
    void convRatio5() {
        int[] age = {70, 60};
        double[] ira = {10, 10};
        double[] a = rothConversionCalculator.convRatio(ira, age, 25);
        assertEquals(a[0], 0.5);
        assertEquals(a[1], 0.5);

    }

    @Test
    void rmdAmount() {
        int[] age = {75, 0};
        double[] ira = {1000000, 0};
        long a = rothConversionCalculator.rmdAmount(age, ira, 0);
        assertEquals(a, 37735);
        assertEquals((long) ira[0], 1000000);
    }

    @Test
    void medicarePremiums1() {
        int[] age = {65, 0};
        long a = rothConversionCalculator.medicarePreminus(rothConversionCalculator.irmaaTbl, age, 300000, 0, true, 0);
        assertEquals(a, 7248);
        a = rothConversionCalculator.medicarePreminus(rothConversionCalculator.irmaaTbl, age, 300000, 0, false, 0);
        assertEquals(a, 9756);
    }

    @Test
    void medicarePremiums2() {
        int[] age = {65, 0};
        long a = rothConversionCalculator.medicarePreminus(rothConversionCalculator.irmaaTbl, age, 300000, 0, true, 1);
        assertEquals(a, 7429);
        a = rothConversionCalculator.medicarePreminus(rothConversionCalculator.irmaaTbl, age, 300000, 0, false, 2);
        assertEquals(a, 10249);
    }

    @Test
    void withDrawBrokerage() {
        double[] brok = {1, 3};
        double[] a = rothConversionCalculator.withDrawBrokerage(brok, 2);
        assertEquals(a[0], 2);
        assertEquals(a[1], 1.3333333333333333);
        assertEquals(brok[0], 0.33333333333333337);
        assertEquals(brok[1], 1.0);
        a = rothConversionCalculator.withDrawBrokerage(brok, 2);
        assertEquals(a[0], 1);
        assertEquals(a[1], 0.6666666666666666);
        assertEquals(brok[0], 0.0);
        assertEquals(brok[1], 0.0);
        a = rothConversionCalculator.withDrawBrokerage(brok, -2);
        assertEquals(a[0], 0);
        assertEquals(a[1], 0);
        assertEquals(brok[0], 2.0);
        assertEquals(brok[1], 2.0);
    }

    @Test
    void withDraw1() {
        double[] brok = {1, 3};
        double[] ira = {5, 5};
        double[] roth = {10, 10};
        double a = rothConversionCalculator.withDraw(20, brok, ira, roth);
        assertEquals(a, 12.0);
        assertEquals(brok[0], 0.0);
        assertEquals(brok[1], 0.0);
        assertEquals(ira[0], 0.0);
        assertEquals(ira[1], 0.0);
        assertEquals(roth[0], 6.5);
        assertEquals(roth[1], 6.5);
    }

    @Test
    void withDraw2() {
        double[] brok = {1, 3};
        double[] ira = {15, 5};
        double[] roth = {10, 10};
        double a = rothConversionCalculator.withDraw(20, brok, ira, roth);
        assertEquals(a, 19);
        assertEquals(brok[0], 0.0);
        assertEquals(brok[1], 0.0);
        assertEquals(ira[0], 2.25);
        assertEquals(ira[1], 0.75);
        assertEquals(roth[0], 10.0);
        assertEquals(roth[1], 10.0);
    }

    @Test
    void withDraw3() {
        double[] brok = {1, 3};
        double[] ira = {15, 5};
        double[] roth = {10, 10};
        double a = rothConversionCalculator.withDraw(25, brok, ira, roth);
        assertEquals(a, 22);
        assertEquals(brok[0], 0.0);
        assertEquals(brok[1], 0.0);
        assertEquals(ira[0], 0.0);
        assertEquals(ira[1], 0.0);
        assertEquals(roth[0], 8.5);
        assertEquals(roth[1], 9.5);
    }

    @Test
    void withDraw4() {
        double[] brok = {1, 3};
        double[] ira = {15, 5};
        double[] roth = {10, 10};
        double a = rothConversionCalculator.withDraw(-25, brok, ira, roth);
        assertEquals(a, 0);
        assertEquals(brok[0], 26.0);
        assertEquals(brok[1], 28.0);
        assertEquals(ira[0], 15.0);
        assertEquals(ira[1], 5.0);
        assertEquals(roth[0], 10.0);
        assertEquals(roth[1], 10.0);
    }

    @Test
    void taxAmount1() {
        double f1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.fedDeduction(age, 100000, true, 0, true), rothConversionCalculator.fedTaxBracket, true, 0);
        assertEquals(f1, 7743);
        double f2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.fedDeduction(age, 1000000, true, 0, true), rothConversionCalculator.fedTaxBracket, true, 0);
        assertEquals(f2, 285601.0);
        double c1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.calDeduction(age, 0, true), rothConversionCalculator.calTaxBracket, true, 0);
        assertEquals(c1, 1964);
        double c2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.calDeduction(age, 0, true), rothConversionCalculator.calTaxBracket, true, 0);
        assertEquals(c2, 87952.0);
        f1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.fedDeduction(age, 100000, true, 0, true), rothConversionCalculator.fedTaxBracket, false, 0);
        assertEquals(f1, 9983.0);
        f2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.fedDeduction(age, 1000000, true, 0, true), rothConversionCalculator.fedTaxBracket, false, 0);
        assertEquals(f2, 316960.0);
        f2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.fedDeduction(age, 1000000, true, 0, false), rothConversionCalculator.fedTaxBracket, false, 0);
        assertEquals(f2, 322788.0);
        c1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.calDeduction(age, 0, true), rothConversionCalculator.calTaxBracket, false, 0);
        assertEquals(c1, 3979.0);
        c2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.calDeduction(age, 0, true), rothConversionCalculator.calTaxBracket, false, 0);
        assertEquals(c2, 101930.0);
    }

    @Test
    void taxAmount2() {
        double f1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.fedDeduction(age, 100000, true, 1, true), rothConversionCalculator.fedTaxBracket, true, 0);
        assertEquals(f1, 7648);
        double f2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.fedDeduction(age, 1000000, true, 2, true), rothConversionCalculator.fedTaxBracket, true, 0);
        assertEquals(f2, 285011.0);
        double c1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.calDeduction(age, 10, true), rothConversionCalculator.calTaxBracket, true, 0);
        assertEquals(c1, 1964);
        double c2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.calDeduction(age, 20,true), rothConversionCalculator.calTaxBracket, true, 0);
        assertEquals(c2, 87952.0);
        f1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.fedDeduction(age, 100000, true, 30, true), rothConversionCalculator.fedTaxBracket, false, 0);
        assertEquals(f1, 3832);
        f2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.fedDeduction(age, 1000000, true, 40, true), rothConversionCalculator.fedTaxBracket, false, 0);
        assertEquals(f2, 297321.0);
        c1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.calDeduction(age, 50, true), rothConversionCalculator.calTaxBracket, false, 0);
        assertEquals(c1, 2411.0);
        c2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.calDeduction(age, 60, true), rothConversionCalculator.calTaxBracket, false, 0);
        assertEquals(c2, 98394.0);
    }

    @Test
    void rmdAge() {
        int a = rothConversionCalculator.rmdAge(1962);
        assertEquals(a, 75);
    }

    @Test
    void fedDeduction1() {
        int[] age = {65, 63};
        long a = rothConversionCalculator.fedDeduction(age, 140000, true, 0, true);
        assertEquals(a, 39100);
        long a1 = rothConversionCalculator.fedDeduction(age, 200000, true, 0, true);
        assertEquals(a1, 33100);
        int[] age1 = {68, 63};
        long a2 = rothConversionCalculator.fedDeduction(age, 140000, true, 0, true);
        assertEquals(a2, 39100);
        long a3 = rothConversionCalculator.fedDeduction(age, 550000, true, 0, true);
        assertEquals(a3, 33100);
        long a4 = rothConversionCalculator.fedDeduction(age, 505000, true, 0, true);
        assertEquals(a4, 40400);
    }

    @Test
    void fedDeduction2() {
        int[] age = {65, 63};
        long a = rothConversionCalculator.fedDeduction(age, 140000, true, 1, true);
        assertEquals(a, 39887);
        long a1 = rothConversionCalculator.fedDeduction(age, 200000, true, 1, true);
        assertEquals(a1, 33887);
        int[] age1 = {68, 63};
        long a2 = rothConversionCalculator.fedDeduction(age, 140000, true, 10, true);
        assertEquals(a2, 47922);
        long a3 = rothConversionCalculator.fedDeduction(age, 550000, true, 21, true);
        assertEquals(a3, 54506);
        long a4 = rothConversionCalculator.fedDeduction(age, 505000, true, 31, true);
        assertEquals(a4, 69325);
    }

    @Test
    void calDeduction1() {
        int[] age = {65, 63};
        long a = rothConversionCalculator.calDeduction(age, 0, true);
        assertEquals(a, 20000);
        int[] age1 = {70, 63};
        long a1 = rothConversionCalculator.calDeduction(age1, 0, true);
        assertEquals(a1, 44000);
    }

    @Test
    void calDeduction12() {
        int[] age = {70, 63};
        long a = rothConversionCalculator.calDeduction(age, 2, true);
        assertEquals(a, 45214);
        int[] age1 = {70, 63};
        long a1 = rothConversionCalculator.calDeduction(age1, 1, true);
        assertEquals(a1, 44599);
    }

    @Test
    void evaluatedAsset() {
        double[] brk = {100000, 200000};
        double a = rothConversionCalculator.evaluatedAsset(100000, brk, 100000, 0, RothConversionCalculator.EvaluateMethod.MAX_ROTH);
        assertEquals(a, 100000.0);
        double b = rothConversionCalculator.evaluatedAsset(100000, brk, 100000, 0, RothConversionCalculator.EvaluateMethod.MAX_TOTAL);
        assertEquals(b, 400000.0);
        double c = rothConversionCalculator.evaluatedAsset(100000, brk, 100000, 0, RothConversionCalculator.EvaluateMethod.MAX_0_YEARS);
        assertEquals(c, 390293.0);
        double d = rothConversionCalculator.evaluatedAsset(100000, brk, 100000, 0, RothConversionCalculator.EvaluateMethod.MAX_10_YEARS);
        assertEquals(d, 397320.99923045);
    }
}
