package ch.tbz.bank.software;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    @Test
    void depositAddsAmount() {
        Account account = new Account("Muster", Currency.CHF, 100);

        account.deposit(25);

        assertEquals(125, account.getBalance(), 0.000001);
    }

    @ParameterizedTest
    @CsvSource({"0,true,100", "99.99,true,0.01", "100,true,0", "100.01,false,100"})
    void withdrawCoversBothBranchesAndBalanceBoundary(double amount, boolean success, double balance) {
        Account account = new Account("Muster", Currency.CHF, 100);

        boolean actual = account.withdraw(amount);

        assertEquals(success, actual);
        assertEquals(balance, account.getBalance(), 0.000001);
    }

    @Test
    void negativeWithdrawalCurrentlyIncreasesBalance_knownDefect() {
        Account account = new Account("Muster", Currency.CHF, 100);

        boolean actual = account.withdraw(-10);

        // Charakterisierung des Ist-Verhaltens, keine fachliche Soll-Anforderung.
        assertTrue(actual);
        assertEquals(110, account.getBalance(), 0.000001);
    }
}
