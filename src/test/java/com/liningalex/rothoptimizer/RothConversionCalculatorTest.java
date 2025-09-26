package com.liningalex.rothoptimizer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RothConversionCalculatorTest {
    int[] age = {62, 59};
    int[] born = {1968, 1970};
    double[] ira = {100000, 180000};
    double fixIncome = 14000;
    double[] ssnIncome = {2000 * 12, 400 * 12};

    RothConversionCalculator rothConversionCalculator = new RothConversionCalculator(fixIncome, 0.05, age, ira, ssnIncome, 2026, true, born);

    @Test
    void rothBalance() {
        double[] a1 = rothConversionCalculator.rothBalance(fixIncome, false);
        assertEquals((long)a1[0], 217130);
        assertEquals((long)a1[1], 295665);
        assertEquals((long)a1[2], 54538);
        double[] a2 = rothConversionCalculator.rothBalance(fixIncome + 40000000, false);
        assertEquals((long)a2[0], 702290);
        assertEquals((long)a2[1], 0);
        assertEquals((long)a2[2], 45153);
        double[] a3 = rothConversionCalculator.rothBalance(fixIncome, true);
        assertEquals((long)a3[0], 201276);
        assertEquals((long)a3[1], 295665);
        assertEquals((long)a3[2], 84020);
        double[] a4 = rothConversionCalculator.rothBalance(360000, true);
        assertEquals((long)a4[0], 594325);
        assertEquals((long)a4[1], 0);
        assertEquals((long)a4[2], 72112);
    }

    @Test
    void balanceRatio() {
        final int[] life = {88, 89};
        double[] a = rothConversionCalculator.convRatio(ira, age, life);
        assertEquals(a[0], 0.40983606557377045);
        assertEquals(a[1], 0.5901639344262294);;
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
        long a = rothConversionCalculator.medicarePreminus(age, 300000, 0);
        assertEquals(a, 5340);
    }

    @Test
    void taxAmount() {
        long f1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.fedDeduction(age, 100000), rothConversionCalculator.fedTaxRate);
        assertEquals(f1, 7743);
        long f2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.fedDeduction(age, 1000000), rothConversionCalculator.fedTaxRate);
        assertEquals(f2, 282407);
        long c1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.calDeduction, rothConversionCalculator.calTaxRate);
        assertEquals(c1, 2490);
        long c2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.calDeduction, rothConversionCalculator.calTaxRate);
        assertEquals(c2, 88963);
    }

    @Test
    void rmdAge() {
        int a = rothConversionCalculator.rmdAge(1962);
        assertEquals(a, 75);
    }

    @Test
    void fedDeduction() {
        int[] age = {65, 63};
        long a = rothConversionCalculator.fedDeduction(age, 140000);
        assertEquals(a, 39100);

    }
}