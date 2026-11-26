package ch.tbz.bank.software;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {
    @Test
    void createdAccountsHaveDifferentIdsAndCanBeFound() {
        Bank bank = new Bank();

        Account first = bank.createAccount("Muster", Currency.CHF, 100);
        Account second = bank.createAccount("Meier", Currency.EUR, 200);

        assertEquals(2, bank.getNumberOfAccounts());
        assertNotEquals(first.getId(), second.getId());
        assertSame(first, bank.getAccount(first.getId()));
        assertSame(second, bank.getAccount(second.getId()));
    }

    @Test
    void unknownAccountReturnsNull() {
        Bank bank = new Bank();

        Account actual = bank.getAccount(-1);

        assertNull(actual);
    }

    @Test
    void deletedAccountCannotBeFoundButOtherAccountRemains() {
        Bank bank = new Bank();
        Account first = bank.createAccount("Muster", Currency.CHF, 100);
        Account second = bank.createAccount("Meier", Currency.CHF, 200);
        int deletedId = first.getId();

        bank.deleteAccount(first);

        assertNull(bank.getAccount(deletedId));
        assertSame(second, bank.getAccount(second.getId()));
        assertEquals(1, bank.getNumberOfAccounts());
    }
}
