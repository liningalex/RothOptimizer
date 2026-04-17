package com.liningalex.rothoptimizer;

import org.apache.commons.lang3.SerializationUtils;
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
    int[] ssnAge = {67,67};
    int propertyTax = 20000;
    int mortgage = 0;
    int donation = 0;
    double inflation = 0.025;

    RothConversionCalculator rothConversionCalculator =
            new RothConversionCalculator(spending, 0.07, ira, brok, ssnIncome, ssnAge, yearBegin,  born, propertyTax, mortgage, donation, inflation);

    double[][] irmaaTbl = SerializationUtils.clone(rothConversionCalculator.irmaaTbl);
    @Test
    void rothBalance() {
        RothConvResults a1 = rothConversionCalculator.rothBalance(spending, 0,100);
        assertEquals((long)a1.roth, 431827);
        assertEquals((long)a1.brok, 6027012);
        assertEquals((long)a1.totalTax, 266699);
        RothConvResults a2 = rothConversionCalculator.rothBalance(spending,40000000, 100);
        assertEquals((long)a2.roth, 6097196);
        assertEquals((long)a2.brok, 2001525);
        assertEquals((long)a2.totalTax, 168504);
        RothConvResults a3 = rothConversionCalculator.rothBalance(spending, 0,100);
        assertEquals((long)a3.roth, 431827);
        assertEquals((long)a3.brok, 6027012);
        assertEquals((long)a3.totalTax, 266699);
        RothConvResults a4 = rothConversionCalculator.rothBalance(spending, 360000 - spending, 0);
        assertEquals((long)a4.roth, 6510022);
        assertEquals((long)a4.brok, 2001525);
        assertEquals((long)a4.totalTax, 169647);

        RothConvResults a = rothConversionCalculator.optimalConversion(0);
        assertEquals(a.roth, 8143851.201592207);
        assertEquals(a.brok, 1794772.0001140318);
        assertEquals(a.totalTax, 81662.0);
    }

    @Test
    void convRatio() {
        {
            int[] age = {60, 60};
            double[] ira = {10, 10};
            double[] a = rothConversionCalculator.convRatio(ira, age, 10);
            assertEquals(a[0], 0.5);
            assertEquals(a[1], 0.5);
        }
        {
            int[] age = {60, 60};
            double[] ira = {10, 20};
            double[] a = rothConversionCalculator.convRatio(ira, age, 20);
            assertEquals(a[0], 0.33333333333333337);
            assertEquals(a[1], 0.6666666666666667);
        }
        {
            int[] age = {74, 60};
            double[] ira = {10, 10};
            double[] a = rothConversionCalculator.convRatio(ira, age, 10);
            assertEquals(a[0], 1.0);
            assertEquals(a[1], 0.0);
        }
        {
            int[] age = {74, 60};
            double[] ira = {10, 10};
            double[] a = rothConversionCalculator.convRatio(ira, age, 15);
            assertEquals(a[0], 0.6666666666666666);
            assertEquals(a[1], 0.3333333333333333);
        }
        {
            int[] age = {70, 60};
            double[] ira = {10, 10};
            double[] a = rothConversionCalculator.convRatio(ira, age, 25);
            assertEquals(a[0], 0.5);
            assertEquals(a[1], 0.5);
        }
    }

    @Test
    void rmdAmount() {
        int[] age = {75, 0};
        double[] ira = {1000000, 0};
        long a = rothConversionCalculator.rmdAmount(age, ira, 0);
        assertEquals(a, 37735);
        assertEquals((long)ira[0], 962264);
    }

    @Test
    void medicarePremiums() {
        int[] age = {65, 0};
        long a = rothConversionCalculator.medicarePreminus(irmaaTbl, age, 300000, 0, true);
        assertEquals(a, 7248);
        a = rothConversionCalculator.medicarePreminus(irmaaTbl, age, 300000, 0, false);
        assertEquals(a, 9756);
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
    void withDraw() {
        {
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
        {
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
        {
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
        {
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
    }

    @Test
    void taxAmount() {
        long f1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.fedDeduction(age, 100000, true, ssnIncome), rothConversionCalculator.fedTaxBracket, true);
        assertEquals(f1, 7743);
        long f2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.fedDeduction(age, 1000000, true, ssnIncome), rothConversionCalculator.fedTaxBracket, true);
        assertEquals(f2, 282407);
        long c1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.calDeduction(age, ssnIncome), rothConversionCalculator.calTaxBracket, true);
        assertEquals(c1, 1964);
        long c2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.calDeduction(age, ssnIncome), rothConversionCalculator.calTaxBracket, true);
        assertEquals(c2, 87955);
        f1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.fedDeduction(age, 100000, true, ssnIncome), rothConversionCalculator.fedTaxBracket, false);
        assertEquals(f1, 9984);
        f2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.fedDeduction(age, 1000000, true, ssnIncome), rothConversionCalculator.fedTaxBracket, false);
        assertEquals(f2, 315365);
        c1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.calDeduction(age, ssnIncome), rothConversionCalculator.calTaxBracket, false);
        assertEquals(c1, 3982);
        c2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.calDeduction(age, ssnIncome), rothConversionCalculator.calTaxBracket, false);
        assertEquals(c2, 101934);
    }

    @Test
    void rmdAge() {
        int a = rothConversionCalculator.rmdAge(1962);
        assertEquals(a, 75);
    }

    @Test
    void fedDeduction() {
        int[] age = {65, 63};
        long a = rothConversionCalculator.fedDeduction(age, 140000, true, ssnIncome);
        assertEquals(a, 39100);
        long a1 = rothConversionCalculator.fedDeduction(age, 200000, true, ssnIncome);
        assertEquals(a1, 33100);
        int[] age1 = {68, 63};
        long a2 = rothConversionCalculator.fedDeduction(age, 140000, true, ssnIncome);
        assertEquals(a2, 39100);
        long a3 = rothConversionCalculator.fedDeduction(age, 550000, true, ssnIncome);
        assertEquals(a3, 33100);
        long a4 = rothConversionCalculator.fedDeduction(age, 505000, true, ssnIncome);
        assertEquals(a4, 40400);
    }

    @Test
    void calDeduction() {
        int[] age = {65, 63};
        long a = rothConversionCalculator.calDeduction(age, ssnIncome);
        assertEquals(a, 20000);
        int[] age1 = {70, 63};
        long a1 = rothConversionCalculator.calDeduction(age1, ssnIncome);
        assertEquals(a1, 44000);
    }
}
