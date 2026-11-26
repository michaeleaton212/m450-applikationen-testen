# Übung 3 – Banksoftware in Java testen

## Aufgabenstellung und Vorgehen

Die mitgelieferte Banksoftware lokal starten, mögliche Blackbox-Testfälle aus
Benutzersicht identifizieren, geeignete Methoden für Whitebox-Tests bestimmen
und Verbesserungen am Code vorschlagen.

Diese Abgabe behandelt ausschliesslich Übung 3. Ihre Aufgabenstellung lautet:

- Die bereitgestellte Java-Banksoftware als lokales Projekt einrichten und starten.
- Sich mit dem Code vertraut machen und die Anwendung testen.
- Mögliche Blackbox-Testfälle identifizieren, die als Benutzer ausgeführt werden können.
- Methoden im Code bestimmen, die sich für Whitebox-Testfälle eignen.
- Allgemeine Codeverbesserungen und passende Best Practices nennen.
- Die Testfälle tabellarisch in einem Markdown-Dokument im Repository abgeben.

Das Java-Maven-Projekt wurde aus dem bereitgestellten `bank-software-mvn.zip`
übernommen. Vorlage: [Maven-Projekt der TBZ](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/teststrategie/bank-software-mvn.zip). Autor des
Beispielprogramms gemäss Quellcode: Thomas Trüb. Die vorhandene Banklogik wird
untersucht und bewusst nicht vollständig neu geschrieben: Die Aufgabe verlangt
eine Testanalyse, nicht die Entwicklung einer produktiven Banksoftware.

## Projekt und Startanleitung

```text
bank-software-mvn/
├── pom.xml
└── src/
    ├── main/java/ch/tbz/bank/software/
    │   ├── Account.java             Konto, Einzahlen und Abheben
    │   ├── Bank.java                Kontenverwaltung
    │   ├── Counter.java             Konsolenmenü und Überweisungen
    │   ├── ExchangeRateOkhttp.java  Externe Wechselkursabfrage
    │   └── Main.java                Start mit fünf Beispielkonten, Currency-Enum
    └── test/java/ch/tbz/bank/software/
        ├── AccountTest.java
        ├── BankTest.java
        └── ConsoleTest.java
```

Voraussetzung: JDK 20 oder neuer (geprüft mit Java 21) und Maven 3.9.
Gson, OkHttp und JUnit werden über Maven geladen; JAR-Dateien müssen nicht von
Hand installiert werden. Beim ersten Build ist Internetzugriff erforderlich.

Vom Repository-Hauptordner aus:

```powershell
cd "tag-02/aufgaben/Übung3/bank-software-mvn"
mvn clean verify
mvn exec:java
```

Auf diesem Laptop ist Maven nicht im `PATH`, aber mit IntelliJ installiert.
Hier funktionieren alternativ folgende Befehle im Projektordner:

```powershell
$maven = 'C:\Program Files\JetBrains\IntelliJ IDEA 2024.3.2.2\plugins\maven\lib\maven3\bin\mvn.cmd'
& $maven clean verify
& $maven exec:java
```

Alternativ in IntelliJ die `pom.xml` als Maven-Projekt öffnen, JDK 21 auswählen
und `Main.main()` ausführen. Im Hauptmenü zeigt `a` die Konten, eine Kontonummer
öffnet ein Konto und `q` beendet die Anwendung. Geldbeträge werden im Programm
mit Dezimalpunkt eingegeben, beispielsweise `50.25`.

## Blackbox-Testfälle

Blackbox betrachtet Eingaben und sichtbare Ausgaben ohne Ableitung aus internen
Verzweigungen. Jeder ausgeführte Konsolentest startet einen eigenen Java-Prozess
mit den fünf ursprünglichen Beispielkonten. Konto 1 hat anfangs 1'500 USD,
Konto 5 hat 1'500'000 USD. `→` trennt aufeinanderfolgende Eingabezeilen;
«leer» bedeutet eine leere Eingabezeile.

Die Fälle BB01–BB09 wurden über echte Standardeingabe und Programmausgabe
automatisiert ausgeführt. Die Spalte «Status» bewertet das fachliche Ergebnis,
nicht bloss den Erfolg des JUnit-Tests.

| ID | Ablauf / Eingaben | Erwartetes Resultat | Effektives Resultat | Status / mögliche Ursache |
| --- | --- | --- | --- | --- |
| BB01 | `a → q` | Kontenliste und reguläres Beenden. | Namen Rockefeller und Branson sowie Verabschiedung sichtbar; Exitcode 0. | Bestanden. |
| BB02 | `1 → e → 50 → a → 25 → k → q` | 50 USD einzahlen, 25 USD abheben; Endstand 1'525 USD. | Endstand 1'525 USD; Exitcode 0. | Bestanden. |
| BB03 | `1 → a → 1500.01 → 1500 → q` | Zu hohe Auszahlung ablehnen; danach gesamtes Guthaben abheben können. | Fehlermeldung; nach zweitem Versuch 0 USD. | Bestanden. |
| BB04 | `999 → 1 → q` | Unbekanntes Konto melden, erneute Auswahl ermöglichen. | Fehlermeldung und danach Konto Rockefeller sichtbar. | Bestanden. |
| BB05 | `e → Muster → CHF → q` | Neues CHF-Konto mit 0 CHF anlegen. | Konto 6, Name Muster, Stand 0 CHF. | Bestanden. |
| BB06 | `1 → l → j → 1 → q` | Nach bestätigtem Löschen ist Konto 1 nicht mehr auswählbar. | Löschmeldung; erneute Auswahl meldet unbekanntes Konto. | Bestanden. |
| BB07 | `1 → ü → 5 → 50 → w → 5 → q` | Überweisung ohne Währungswechsel; 1'450 USD und 1'500'050 USD. | Beide erwarteten Kontostände sichtbar. | Bestanden. |
| BB08 | `1 → leer` | Verständliche Eingabeaufforderung statt Absturz (Robustheitsziel). | `StringIndexOutOfBoundsException`, Exitcode 1. | Fehler: `substring(0,1)` ohne Prüfung auf leeren Text. |
| BB09 | `1 → e → -50 → q` | Negativen Einzahlungsbetrag ablehnen, Guthaben unverändert (angenommenes fachliches Soll). | Guthaben sinkt auf 1'450 USD. | Fehler: fehlende Betragsvalidierung. |
| BB10 | Im Hauptmenü `w → CHF USD`. | Bei gültiger Konfiguration einen positiven Wechselkurs anzeigen, andernfalls einen verständlichen Fehler. | Nicht ausgeführt: kein eigener API-Schlüssel eingerichtet. | Offen; Netzverbindung, Berechtigung und API-Verhalten gesondert testen. |

Zu BB10: `w` bedeutet im Hauptmenü Wechselkursabfrage, innerhalb eines Kontos
hingegen Kontowechsel. Der Ablauf lautet deshalb direkt `w → CHF USD` nach dem Start.

Zusätzliche sinnvolle Tests: Löschbestätigung mit `n`, ungültige Währung,
leerer Name, Überweisung auf dasselbe oder ein unbekanntes Konto, Überweisung
mit unzureichendem Guthaben und alle Währungspaare. Ebenso sollten `NaN`,
`Infinity`, ungültige Zahltexte und sehr grosse Beträge untersucht werden.
Diese weiteren Fälle sind hier Testvorschläge und nicht als ausgeführt ausgewiesen.

## Whitebox-Testfälle und geeignete Methoden

Whitebox nutzt die bekannte Implementierung zur Auswahl von Tests, beispielsweise
beide Zweige einer Bedingung. Ein Unit-Test ist nicht allein aufgrund seines
Testwerkzeugs Whitebox. Die folgenden Tests wurden gezielt anhand des Codes gewählt.
Verglichen werden feste erwartete Werte, nicht eine Kopie der Implementierungslogik.

| ID | Methode / Struktur | Eingaben und erwartetes Resultat | Umsetzung / Ergebnis |
| --- | --- | --- | --- |
| WB01 | `Account.deposit()` | Start 100 CHF, Einzahlung 25 CHF: 125 CHF. | `depositAddsAmount`: bestanden. |
| WB02 | `Account.withdraw()`, Erfolgs- und Fehlerzweig | Jeweils Start 100 CHF: 0 → Erfolg, 100; 99.99 → Erfolg, 0.01; 100 → Erfolg, 0; 100.01 → Ablehnung, 100. | Parametrisierter Test `withdrawCoversBothBranchesAndBalanceBoundary`: vier Fälle bestanden. Prüft insbesondere `>` gegenüber `>=`. |
| WB03 | `Account.withdraw()`, fehlende Prüfung negativer Beträge | Start 100 CHF, Auszahlung -10 CHF: fachlich sollte abgelehnt werden (Annahme). Tatsächlich Erfolg und 110 CHF. | `negativeWithdrawalCurrentlyIncreasesBalance_knownDefect` bestätigt den Fehler. |
| WB04 | `Bank.createAccount()`, `getAccount()`, Kontoliste | Zwei Konten erstellen: verschiedene IDs, Anzahl 2, beide über ihre IDs auffindbar. | `createdAccountsHaveDifferentIdsAndCanBeFound`: bestanden. |
| WB05 | `Bank.getAccount()`, Schleife ohne Treffer | Nicht vorhandene ID -1 in leerer Bank: `null`. | `unknownAccountReturnsNull`: bestanden. Ein Treffer wird in WB04 geprüft. |
| WB06 | `Bank.deleteAccount()`, Liste nach Entfernen | Eines von zwei Konten löschen: nicht mehr auffindbar, anderes unverändert auffindbar, Anzahl 1. | `deletedAccountCannotBeFoundButOtherAccountRemains`: bestanden. |
| WB07 | `Counter.convertCurrency()` | USD→CHF: 100 → 111; USD→EUR: 100 → 91; CHF→USD: 100 → 90, gemäss festen Beispielkursen im Code. Fehlende Paare gesondert prüfen. | Vorschlag, nicht automatisiert. Private Methode über den öffentlichen Überweisungsablauf prüfen; keine Reflection nötig. |
| WB08 | `Counter.editAccount()`, `getConfirmation()` | Leere Eingaben und beide Bestätigungszweige untersuchen. | Leere Aktion durch BB08 reproduziert; leere Bestätigung und `n` noch offen. |
| WB09 | `ExchangeRateOkhttp.getExchangeRate()` | Erfolgreiche Antwort, HTTP-Fehler, leere Antwort, ungültiges JSON, Timeout und fehlender Schlüssel. | Vorschläge, nicht ausgeführt. HTTP-Antworten künftig mit einem lokalen Testserver kontrollieren. |

Die Tests folgen Arrange–Act–Assert. Bei den Kontoständen wird wegen des vorhandenen
`double`-Datentyps eine Toleranz von `0.000001` verwendet. Die Tests setzen keine
globale Start-ID voraus: Bank-Tests verwenden die tatsächlich vergebenen IDs.
Nur die Konsolentests erwarten IDs 1–5, weil jeder Test einen neuen Prozess startet.

## Auswertung und Grenzen

Stand der Prüfung: 26.11.2026, Windows, Java 21.

- `mvn clean verify`: erfolgreich; Anwendung kompiliert und JAR erstellt.
- **18 JUnit-Testfälle:** 6 für `Account`, 3 für `Bank`, 9 für die Konsole.
- Drei erfolgreiche Tests charakterisieren bekannte Fehler: negative Auszahlung,
  negative Einzahlung und Absturz bei leerer Aktion. Sie bestätigen das aktuelle
  Ist-Verhalten. Sie erklären die Banksoftware ausdrücklich nicht für fehlerfrei.
- Die fachliche Blackbox-Auswertung lautet: sieben Fälle bestanden, zwei Fehler,
  eine externe Prüfung offen. WB03 dokumentiert einen weiteren fachlichen Fehler.
- Keine Live-API-Abfrage, keine vollständige Codeabdeckungsmessung und keine
  Behauptung vollständiger Zweigabdeckung der gesamten Anwendung.

Nach einer späteren Fehlerkorrektur müssen die Charakterisierungstests auf das
vereinbarte Soll-Verhalten geändert werden. Ein grüner Testlauf allein bedeutet
nicht, dass alle fachlichen Anforderungen erfüllt sind.

## Verbesserungen und Best Practices

| Priorität | Beobachtung im gelieferten Code | Verbesserung und Nutzen |
| --- | --- | --- |
| Hoch | `deposit()` und `withdraw()` akzeptieren negative und nicht endliche Werte. | Zulässige Beträge explizit festlegen und in der Fachlogik validieren; verhindert falsche Guthaben. Regeln für 0 separat klären. |
| Hoch | Geld wird als `double` gespeichert. | `BigDecimal` mit definierter Skala und Rundung oder ganze Rappen verwenden; vermeidet binäre Rundungsfehler. |
| Hoch | `editAccount()` und `getConfirmation()` verwenden `substring()` ohne Leerprüfung. | Eingaben vor Zugriff prüfen, EOF behandeln und verständlich erneut abfragen. |
| Hoch | `convertCurrency()` unterstützt nur drei Richtungen, andere werden unverändert verbucht. | Alle unterstützten Paare eindeutig behandeln und nicht unterstützte Umrechnungen vor Abbuchung ablehnen. |
| Hoch | `transferAmount()` bucht vor der Umrechnung ab. | Gesamte Überweisung zuerst prüfen und dann atomar buchen; Fehler dürfen keine einseitige Buchung hinterlassen. |
| Hoch | API-Schlüssel war im Quellcode eingebettet. | Konfiguration ausserhalb des Repositorys verwenden; Betreiber sollte den bereits im Lehrmaterial enthaltenen Schlüssel ersetzen. |
| Mittel | HTTP-Status und Antwort werden unzureichend geprüft; Fehler liefert 0. | Antwort mit try-with-resources schliessen, Status und Daten validieren, Fehler klar unterscheiden; Client wiederverwenden. |
| Mittel | `Counter` mischt Menü, Validierung, Buchungen und Umrechnung. | Fachliche Operationen von Konsolenein-/ausgabe trennen; gezielte Unit-Tests werden einfacher. |
| Mittel | Konto-IDs stammen aus einem globalen statischen Zähler. | ID-Vergabe in der Bank kapseln; unabhängige Instanzen und Tests ermöglichen. |
| Mittel | Unbekannte Währung wird stillschweigend zu USD, leerer Name wird akzeptiert. | Gewünschte Validierungsregeln festlegen und Eingaben mit klarer Rückmeldung ablehnen. |
| Mittel | Breites `catch (Exception)` verschluckt Details; Eingabeprüfung nutzt teils `find()` statt vollständigem Abgleich. | Spezifische Fehler behandeln und ganze Eingaben validieren; Fehler werden nachvollziehbar. |
| Mittel | Alte Gson-Version und OkHttp-Vorabversion im Originalprojekt. | Abhängigkeiten auf Wartungsstand und bekannte Schwachstellen prüfen und nach Kompatibilitätstests aktualisieren; hier kein Versionsaudit durchgeführt. |
| Niedrig | `AccountExeption` ist falsch geschrieben; `pseudoDeleteAccount()` setzt inkonsistente Felder und wird nicht verwendet. | Namen korrigieren und unnötigen Code entfernen. Der Kommentar in `Bank.deleteAccount()` zur Referenzübergabe sollte präzisiert werden: Java übergibt auch Objektreferenzen als Wert. |

## Änderungen und Annahmen dieser Abgabe

- Übernommen wurden die Originalquellen mit Maven-Struktur; Ergänzungen sind
  JUnit-Tests, Build-/Startkonfiguration und diese Dokumentation.
- Der einzige funktionale Eingriff ist die Entfernung des eingebetteten
  API-Schlüssels: `ExchangeRateOkhttp` liest `EXCHANGE_RATE_API_KEY`. Fehlt die
  Variable, erscheint eine Meldung, ohne einen Netzwerkaufruf zu starten.
  Ein eigener gültiger Schlüssel ist nur für die optionale Live-Abfrage nötig.
- Das ursprüngliche ZIP bleibt lokal erhalten, wird wegen des enthaltenen
  Schlüssels und der kompilierten Dateien aber nicht veröffentlicht.
  `target/` wird ebenfalls ignoriert. Die lesbaren Java-Quellen werden versioniert.
- Die Negativbetrag-Ablehnung und robuste Behandlung leerer Eingaben sind
  begründete Qualitätsziele, keine im Aufgabenblatt ausformulierten Bankregeln.
  Vor einer produktiven Umsetzung müssten diese Regeln verbindlich geklärt werden.
- Die vorhandene Beispielumrechnung ist keine Aussage über aktuelle Wechselkurse.
- Die dokumentierten Programmfehler bleiben zur Analyse sichtbar. Eine umfassende
  Behebung wäre eine Folgeaufgabe; diese Abgabe beantwortet die Testanalyse.
