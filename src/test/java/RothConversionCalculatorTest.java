import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RothConversionCalculatorTest {
    int[] age = {64, 60};
    double[] ira = {1000000, 1800000};
    double fixIncome = 110000;
    double[] ssnIncome = {4000 * 12, 4000 * 12};

    RothConversionCalculator myCal = new RothConversionCalculator(fixIncome, 0.05, age, ira, ssnIncome, 2026, true);

    @Test
    void rothBalance() {
        myCal.rothBalance(360000, true);
        myCal.rothBalance(4360000, true);
        long a1 = myCal.rothBalance(fixIncome, false);
        assertEquals(a1, 6639471);
        long a2 = myCal.rothBalance(fixIncome + 40000000, false);
        assertEquals(a2, 6116534);
        long a3 = myCal.rothBalance(fixIncome, true);
        assertEquals(a3, 5932161);
        long a4 = myCal.rothBalance(360000, true);
        assertEquals(a4, 8152726);

    }

    @Test
    void balanceRatio() {
        double[] a = myCal.balanceRatio(ira);
        assertEquals(a[0], 0.35714285714285715);
        assertEquals(a[1], 0.6428571428571429);;
    }

    @Test
    void rmdAmount() {
        int[] age = {73, 0};
        double[] ira = {1000000, 0};
        long a = myCal.rmdAmount(age, ira, 0);
        assertEquals(a, 37735);
        assertEquals((long)ira[0], 962264);
    }

    @Test
    void taxAmount() {
        long f1 = myCal.taxAmount(100000 - myCal.fedDeduction, myCal.fedTaxRate);
        assertEquals(f1, 7923);
        long f2 = myCal.taxAmount(1000000 - myCal.fedDeduction, myCal.fedTaxRate);
        assertEquals(f2, 282962);
        long c1 = myCal.taxAmount(100000 - myCal.calDeduction, myCal.calTaxRate);
        assertEquals(c1, 2490);
        long c2 = myCal.taxAmount(1000000 - myCal.calDeduction, myCal.calTaxRate);
        assertEquals(c2, 87521);
    }
}