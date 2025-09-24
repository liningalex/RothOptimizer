package com.liningalex.rothoptimizer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RothConversionCalculatorTest {
    int[] age = {62, 59};
    double[] ira = {100000, 180000};
    double fixIncome = 14000;
    double[] ssnIncome = {2000 * 12, 400 * 12};

    RothConversionCalculator rothConversionCalculator = new RothConversionCalculator(fixIncome, 0.05, age, ira, ssnIncome, 2026, true);

    @Test
    void rothBalance() {
        rothConversionCalculator.rothBalance(360000, true);
        rothConversionCalculator.rothBalance(4360000, true);
        long a1 = rothConversionCalculator.rothBalance(fixIncome, false);
        assertEquals(a1, 479371);
        long a2 = rothConversionCalculator.rothBalance(fixIncome + 40000000, false);
        assertEquals(a2, 681611);
        long a3 = rothConversionCalculator.rothBalance(fixIncome, true);
        assertEquals(a3, 469280);
        long a4 = rothConversionCalculator.rothBalance(360000, true);
        assertEquals(a4, 564433);

    }

    @Test
    void balanceRatio() {
        double[] a = rothConversionCalculator.balanceRatio(ira);
        assertEquals(a[0], 0.35714285714285715);
        assertEquals(a[1], 0.6428571428571429);;
    }

    @Test
    void rmdAmount() {
        int[] age = {73, 0};
        double[] ira = {1000000, 0};
        long a = rothConversionCalculator.rmdAmount(age, ira, 0);
        assertEquals(a, 37735);
        assertEquals((long)ira[0], 962264);
    }

    @Test
    void medicarePremiums() {
        int[] age = {65, 0};
        long a = rothConversionCalculator.medicarePreminus(age, 300000, 0);
        assertEquals(a, 5340);
    }

    @Test
    void taxAmount() {
        long f1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.fedDeduction, rothConversionCalculator.fedTaxRate);
        assertEquals(f1, 7923);
        long f2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.fedDeduction, rothConversionCalculator.fedTaxRate);
        assertEquals(f2, 282962);
        long c1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.calDeduction, rothConversionCalculator.calTaxRate);
        assertEquals(c1, 2490);
        long c2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.calDeduction, rothConversionCalculator.calTaxRate);
        assertEquals(c2, 87521);
    }
}