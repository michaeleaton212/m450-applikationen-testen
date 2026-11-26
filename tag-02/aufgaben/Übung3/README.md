# Übung 3 – Banksoftware in Java testen

Abgabe von Michael Eaton. KI wurde als Hilfsmittel beim Formulieren und Formatieren der README, beispielsweise der Tabellen, verwendet.

## Aufgabe

Die Banksoftware starten, Blackbox-Testfälle aus Benutzersicht sammeln, passende Methoden für Whitebox-Tests finden und Verbesserungen vorschlagen.

Grundlage ist das [Java-Maven-Projekt der TBZ](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/teststrategie/bank-software-mvn.zip) von Thomas Trüb. Diese Abgabe behandelt nur Übung 3.

## Starten und testen

Benötigt werden JDK 20 oder neuer und Maven 3.9. Getestet wurde mit Java 21. Maven lädt Gson, OkHttp und JUnit automatisch.

Im Repository-Hauptordner:

```powershell
cd "tag-02/aufgaben/Übung3/bank-software-mvn"
mvn clean verify
mvn exec:java
```

Auf diesem Laptop ist Maven nicht im `PATH`. Im Projektordner funktioniert stattdessen:

```powershell
$maven = 'C:\Program Files\JetBrains\IntelliJ IDEA 2024.3.2.2\plugins\maven\lib\maven3\bin\mvn.cmd'
& $maven clean verify
& $maven exec:java
```

In IntelliJ kann auch die `pom.xml` geöffnet und `Main.main()` gestartet werden. Beträge mit Dezimalpunkt eingeben, z. B. `50.25`.

## Blackbox-Testfälle

Hier zählen die Eingaben und sichtbaren Ergebnisse. Jeder Fall beginnt mit einem neuen Programmstart: Konto 1 hat 1'500 USD, Konto 5 hat 1'500'000 USD. `→` bedeutet die nächste Eingabezeile.

| ID | Eingaben | Erwartet | Beobachtet / Status |
| --- | --- | --- | --- |
| BB01 | `a → q` | Konten anzeigen und beenden. | Rockefeller, Branson und Verabschiedung sichtbar. Bestanden. |
| BB02 | `1 → e → 50 → a → 25 → k → q` | Endstand 1'525 USD. | 1'525 USD. Bestanden. |
| BB03 | `1 → a → 1500.01 → 1500 → q` | Zu hohen Betrag ablehnen, danach vollständige Auszahlung erlauben. | Fehlermeldung, danach 0 USD. Bestanden. |
| BB04 | `999 → 1 → q` | Unbekanntes Konto melden, neue Auswahl erlauben. | Meldung, danach Konto Rockefeller. Bestanden. |
| BB05 | `e → Muster → CHF → q` | Neues Konto mit 0 CHF. | Konto 6, Muster, 0 CHF. Bestanden. |
| BB06 | `1 → l → j → 1 → q` | Gelöschtes Konto nicht mehr finden. | Löschmeldung, danach Konto unbekannt. Bestanden. |
| BB07 | `1 → ü → 5 → 50 → w → 5 → q` | Stände 1'450 und 1'500'050 USD. | Beide Stände stimmen. Bestanden. |
| BB08 | `1 → leere Zeile` | Erneute Eingabe statt Absturz. | Absturz mit `StringIndexOutOfBoundsException`. Fehler. |
| BB09 | `1 → e → -50 → q` | Negativen Betrag ablehnen, Stand unverändert. | Stand sinkt auf 1'450 USD. Fehler. |
| BB10 | `w → CHF USD` im Hauptmenü | Gültigen Wechselkurs oder klare Fehlermeldung anzeigen. | Live-Abfrage nicht ausgeführt; eigener API-Schlüssel fehlt. Offen. |

BB01–BB09 sind als Konsolentests automatisiert. Die Fehlerursachen von BB08 und BB09 sind die fehlende Leerprüfung und Betragsvalidierung. Das erwartete Verhalten ist dabei ein angenommenes Qualitätsziel.

`w` fragt im Hauptmenü den Wechselkurs ab, im Kontomenü wechselt es das Konto. Weitere sinnvolle Fälle sind Löschabbruch, unbekannte Währungen, ungültige Zahltexte und Überweisungen auf ungültige Konten.

## Whitebox-Testfälle

Hier hilft der Quellcode bei der Testauswahl. Zum Beispiel werden beide Zweige einer Bedingung geprüft. Private Methoden lassen sich über die öffentlichen Abläufe testen.

| ID | Methode | Testidee und Ergebnis |
| --- | --- | --- |
| WB01 | `Account.deposit()` | 100 + 25 = 125 CHF. Bestanden. |
| WB02 | `Account.withdraw()` | Bei 100 CHF Guthaben: 0, 99.99 und 100 akzeptieren; 100.01 ablehnen. Vier Fälle bestanden. |
| WB03 | `Account.withdraw()` | −10 sollte abgelehnt werden. Tatsächlich steigt der Stand von 100 auf 110 CHF. Fehler bestätigt. |
| WB04 | `Bank.createAccount()`, `getAccount()` | Zwei Konten: verschiedene IDs, Anzahl 2, beide auffindbar. Bestanden. |
| WB05 | `Bank.getAccount()` | Leere Bank, ID −1: `null`. Bestanden. |
| WB06 | `Bank.deleteAccount()` | Eines von zwei Konten löschen: Anzahl 1, anderes Konto bleibt. Bestanden. |
| WB07 | `Counter.convertCurrency()` | Mit Beispielkursen: 100 USD → 111 CHF, 100 USD → 91 EUR, 100 CHF → 90 USD. Vorschläge; nicht durch eigene Unit-Tests geprüft. |
| WB08 | `Counter.editAccount()`, `getConfirmation()` | Leere Aktion, Bestätigung und Abbruch prüfen. BB08 bestätigt den Absturz bei leerer Aktion. |
| WB09 | `ExchangeRateOkhttp.getExchangeRate()` | Erfolg, HTTP-Fehler, ungültige Antwort und Timeout mit kontrollierten Antworten prüfen. Vorschläge, nicht ausgeführt. |

Die Unit-Tests verwenden feste erwartete Werte. Wegen `double` werden Kontostände mit einer Toleranz von `0.000001` verglichen.

## Ergebnisse und Grenzen

Der letzte Maven-Lauf vom 26.11.2026 war erfolgreich: **18 Tests, 0 fehlgeschlagen**. Davon bestätigen drei Tests bewusst vorhandene Fehler: negative Einzahlung, negative Auszahlung und leere Aktion. Grün bedeutet hier deshalb nicht fehlerfrei.

Zusätzliche Konsolenprüfungen im Review zeigten:

- Löschabbruch funktioniert; Einzahlung 0 verändert das Guthaben nicht.
- Ungültiger Betrag `abc` und Aktion `x` führen zu einer neuen Eingabeaufforderung.
- 100 USD nach CHF werden mit dem festen Beispielkurs korrekt umgerechnet.
- EUR nach CHF wird ohne Umrechnung gebucht. Das ist ein Fehler.
- `NaN` als Einzahlung macht den Kontostand unbrauchbar.
- Bei unbekannter Währung erscheint eine Warnung; danach wird USD verwendet.
- Ohne API-Konfiguration erscheint eine verständliche Meldung.

Diese Zusatzprüfungen sind keine weiteren JUnit-Tests. Live-API, Dienstausfall und vollständige Codeabdeckung wurden nicht geprüft. Die festen Umrechnungskurse sind keine aktuellen Marktkurse.

## Was ich am Code verbessern würde

| Stelle | Problem | Verbesserung |
| --- | --- | --- |
| `Account` | Negative Beträge und `NaN` verändern das Guthaben. | Beträge zentral prüfen; Regel für Null klären. |
| Geldbeträge | `double` kann Rundungsfehler verursachen. | `BigDecimal` mit festgelegter Rundung verwenden. |
| `Counter` | Leere Eingaben können abstürzen. | Vor dem Zugriff prüfen und erneut fragen. |
| `convertCurrency()` | Fehlende Währungspaare werden unverändert gebucht. | Umrechnen oder vor der Buchung ablehnen. |
| `transferAmount()` | Abbuchung erfolgt vor weiteren Prüfungen. | Erst alles prüfen, dann beide Konten konsistent buchen. |
| `ExchangeRateOkhttp` | HTTP-Fehler und ungültige Antworten werden ungenügend unterschieden. | Status und Daten prüfen; Ressourcen mit try-with-resources verwalten. |
| `Counter` | Menü, Eingaben und Buchungen sind vermischt. | Fachlogik trennen und Abhängigkeiten übergeben; das erleichtert Tests. |
| `createAccount()` | Unbekannte Währung wird trotz Warnung zu USD. | Erneute Auswahl verlangen statt Währung ersetzen. |
| Fehlerbehandlung | Breites `catch (Exception)` verschluckt Details. | Passende Exceptions gezielt behandeln. |
| IDs und Löschen | Globaler Zähler und ungenutzte Pseudolöschung. | ID-Vergabe kapseln und unnötigen Code entfernen. |
| Abhängigkeiten | Alte Bibliotheksversionen aus der Vorlage. | Wartungsstand und Sicherheit prüfen; noch kein Versionsaudit durchgeführt. |

## Annahmen und Änderungen

Die Banklogik stammt aus der Vorlage. Ergänzt wurden JUnit-Tests und Maven-Konfiguration. Die Fehler bleiben für die Analyse sichtbar; ihre vollständige Behebung gehört nicht zur Aufgabenstellung.

Negative Beträge abzulehnen und leere Eingaben abzufangen sind angenommene Qualitätsziele. Die genaue Behandlung von Nullbeträgen muss fachlich geklärt werden.

Der eingebettete API-Schlüssel wurde entfernt. Die optionale Kursabfrage liest `EXCHANGE_RATE_API_KEY`; ohne diese Variable erfolgt kein Netzwerkaufruf. Die Überweisungen verwenden davon unabhängig feste Beispielkurse.

Das Original-ZIP bleibt wegen des enthaltenen Schlüssels lokal. ZIP und `target/` werden nicht versioniert.
