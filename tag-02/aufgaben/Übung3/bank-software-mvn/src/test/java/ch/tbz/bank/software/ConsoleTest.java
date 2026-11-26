package ch.tbz.bank.software;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleTest {
    @TempDir
    Path directory;

    // Ein eigener Prozess prüft echte Konsoleneingaben und schützt den Testlauf vor System.exit().
    private String run(String input, int expectedExitCode) throws Exception {
        Path output = directory.resolve("output.txt");
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classpath = System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"));
        Process process = new ProcessBuilder(java, "-Dfile.encoding=UTF-8",
                "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8", "-cp", classpath,
                "ch.tbz.bank.software.Main")
                .redirectErrorStream(true).redirectOutput(output.toFile()).start();
        try {
            try (var stdin = process.getOutputStream()) {
                stdin.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(10, TimeUnit.SECONDS), "Programm muss innerhalb von 10 Sekunden enden");
            String result = Files.readString(output, StandardCharsets.UTF_8);
            assertEquals(expectedExitCode, process.exitValue(), result);
            return result;
        } finally {
            process.destroyForcibly();
        }
    }

    @Test
    void listAccountsAndQuit() throws Exception {
        String output = run("a\nq\n", 0);
        assertTrue(output.contains("Rockefeller"));
        assertTrue(output.contains("Branson"));
        assertTrue(output.contains("Auf Wiedersehen!"));
    }

    @Test
    void depositAndWithdraw() throws Exception {
        String output = run("1\ne\n50\na\n25\nk\nq\n", 0);
        assertTrue(output.matches("(?s).*1525[,.]00 USD.*"));
    }

    @Test
    void insufficientBalanceAllowsAnotherAttempt() throws Exception {
        String output = run("1\na\n1500.01\n1500\nq\n", 0);
        assertTrue(output.contains("Kontostand zu niedrig"));
        assertTrue(output.matches("(?s).*Kontostand: 0[,.]00 USD.*"));
    }

    @Test
    void unknownAccountAllowsAnotherSelection() throws Exception {
        String output = run("999\n1\nq\n", 0);
        assertTrue(output.contains("Ein Konto mit dieser Nummer ist nicht vorhanden"));
        assertTrue(output.contains("Nachname: Rockefeller"));
    }

    @Test
    void createAccount() throws Exception {
        String output = run("e\nMuster\nCHF\nq\n", 0);
        assertTrue(output.contains("Kontonummer: 6"));
        assertTrue(output.contains("Nachname: Muster"));
        assertTrue(output.matches("(?s).*Kontostand: 0[,.]00 CHF.*"));
    }

    @Test
    void deleteAccountAfterConfirmation() throws Exception {
        String output = run("1\nl\nj\n1\nq\n", 0);
        assertTrue(output.contains("Konto mit Nummer 1 wurde gelöscht"));
        assertTrue(output.contains("Ein Konto mit dieser Nummer ist nicht vorhanden"));
    }

    @Test
    void transferBetweenUsdAccounts() throws Exception {
        String output = run("1\nü\n5\n50\nw\n5\nq\n", 0);
        assertTrue(output.matches("(?s).*Neuer Kontostand: 1450[,.]00 USD.*"));
        assertTrue(output.matches("(?s).*Kontostand: 1500050[,.]00 USD.*"));
    }

    @Test
    void emptyActionCurrentlyCrashes_knownDefect() throws Exception {
        String output = run("1\n\n", 1);
        assertTrue(output.contains("StringIndexOutOfBoundsException"));
    }

    @Test
    void negativeDepositCurrentlyReducesBalance_knownDefect() throws Exception {
        String output = run("1\ne\n-50\nq\n", 0);
        assertTrue(output.matches("(?s).*Kontostand: 1450[,.]00 USD.*"));
    }
}
