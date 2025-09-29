package com.liningalex.rothoptimizer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RothConversionCalculatorTest {
    int yearBegin = 2026;
    int[] born = {1968, 1970};
    int[] age = {yearBegin - born[0], yearBegin - born[1]};
    double[] ira = {100000, 180000};
    double fixIncome = 14000;
    double[] ssnIncome = {2000 * 12, 400 * 12};
    int propertyTax = 20000;
    int mortgage = 0;
    int donation = 0;

    RothConversionCalculator rothConversionCalculator =
            new RothConversionCalculator(fixIncome, 0.05, ira, ssnIncome, yearBegin, true, born, propertyTax, mortgage, donation);

    @Test
    void rothBalance() {
        RothConvResults a1 = rothConversionCalculator.rothBalance(fixIncome, false);
        assertEquals((long)a1.roth, 280398);
        assertEquals((long)a1.rmd, 530351);
        assertEquals((long)a1.totalTax, 78887);
        RothConvResults a2 = rothConversionCalculator.rothBalance(fixIncome + 40000000, false);
        assertEquals((long)a2.roth, 1126952);
        assertEquals((long)a2.rmd, 0);
        assertEquals((long)a2.totalTax, 48694);
        RothConvResults a3 = rothConversionCalculator.rothBalance(fixIncome, true);
        assertEquals((long)a3.roth, 257538);
        assertEquals((long)a3.rmd, 530351);
        assertEquals((long)a3.totalTax, 104223);
        RothConvResults a4 = rothConversionCalculator.rothBalance(360000, true);
        assertEquals((long)a4.roth, 1013441);
        assertEquals((long)a4.rmd, 0);
        assertEquals((long)a4.totalTax, 65564);
    }

    @Test
    void convRatio() {
        final int[] life = {88, 89};
        double[] a = rothConversionCalculator.convRatio(ira, age, life);
        assertEquals(a[0], 0.3816793893129771);
        assertEquals(a[1], 0.6183206106870229);;
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
        long f1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.fedDeduction(age, 100000, true), rothConversionCalculator.fedTaxRate);
        assertEquals(f1, 7743);
        long f2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.fedDeduction(age, 1000000, true), rothConversionCalculator.fedTaxRate);
        assertEquals(f2, 282407);
        long c1 = rothConversionCalculator.taxAmount(100000 - rothConversionCalculator.calDeduction(age), rothConversionCalculator.calTaxRate);
        assertEquals(c1, 1964);
        long c2 = rothConversionCalculator.taxAmount(1000000 - rothConversionCalculator.calDeduction(age), rothConversionCalculator.calTaxRate);
        assertEquals(c2, 87955);
    }

    @Test
    void rmdAge() {
        int a = rothConversionCalculator.rmdAge(1962);
        assertEquals(a, 75);
    }

    @Test
    void fedDeduction() {
        int[] age = {65, 63};
        long a = rothConversionCalculator.fedDeduction(age, 140000, true);
        assertEquals(a, 39100);
        long a1 = rothConversionCalculator.fedDeduction(age, 200000, true);
        assertEquals(a1, 33100);
        int[] age1 = {68, 63};
        long a2 = rothConversionCalculator.fedDeduction(age, 140000, true);
        assertEquals(a2, 39100);
    }

    @Test
    void calDeduction() {
        int[] age = {65, 63};
        long a = rothConversionCalculator.calDeduction(age);
        assertEquals(a, 20000);
        int[] age1 = {70, 63};
        long a1 = rothConversionCalculator.calDeduction(age1);
        assertEquals(a1, 44000);
    }
}
