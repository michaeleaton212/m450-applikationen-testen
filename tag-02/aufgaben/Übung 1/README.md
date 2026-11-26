# Übung 1 – Rabattlogik und Softwaretests in C#

## Aufgabenstellung

Die Originalaufgabe beschreibt folgende Regeln:

- Weniger als 15'000 CHF: kein Rabatt.
- Bis zu 20'000 CHF: 5 % Rabatt.
- Unter 25'000 CHF: 7 % Rabatt.
- Darüber: 8,5 % Rabatt.

Daraus sind eine Tabelle abstrakter Testfälle mit Vergleichsoperatoren und eine
Tabelle konkreter Testfälle mit Eingabewerten abzuleiten. Die Angaben werden als
aufeinanderfolgende Rabattstufen verstanden. Exakt 25'000 CHF bleibt im
Originalwortlaut offen; die Annahme dazu steht unten.

Eine Verkaufssoftware für ein Autohaus bestimmt anhand des Kaufpreises den Rabatt.
Die Klasse `DiscountCalculator` stellt dafür die Methode
`public decimal CalculateDiscount(decimal purchasePrice)` bereit. Sie gibt den
Prozentwert zurück, beispielsweise `8.5m` für 8,5 %. Geldbeträge und Prozentwerte
verwenden `decimal`, damit die Dezimalwerte exakt dargestellt werden.

Implementierung und xUnit-Tests liegen in separaten Projekten. Die Solution
`DiscountCalculator.sln` fasst beide Projekte zusammen.

## Geschäftsregeln

- Kaufpreis < 15'000 CHF: 0 % Rabatt
- Kaufpreis >= 15'000 CHF und <= 20'000 CHF: 5 % Rabatt
- Kaufpreis > 20'000 CHF und < 25'000 CHF: 7 % Rabatt
- Kaufpreis >= 25'000 CHF: 8,5 % Rabatt

## Abstrakte Testfälle – Äquivalenzklassenanalyse

Die Äquivalenzklassenanalyse teilt Eingaben in Gruppen mit demselben erwarteten
Verhalten ein. Innerhalb einer Klasse gilt jeweils derselbe Rabatt. Die vier
Klassen decken alle Kaufpreise ohne Überschneidungen oder Lücken ab.

Für die fachliche Betrachtung nehmen wir `purchasePrice >= 0m` an. Die Tabelle
gilt innerhalb dieses Eingabebereichs. Die Bedingungen allein klassifizieren
technisch auch negative Werte; daraus folgt keine fachliche Zulässigkeit.

Die folgende separate Tabelle beschreibt die abstrakten Testfälle: Statt eines
einzelnen Kaufpreises steht jeweils eine Bedingung für einen ganzen Wertebereich.
`&&` bedeutet, dass beide Vergleiche erfüllt sein müssen. Das Suffix `m`
kennzeichnet einen `decimal`-Wert in C#.

| Testfall-ID | Bedingung für den Kaufpreis | Erwarteter Rabatt |
| --- | --- | --- |
| A01 | `purchasePrice < 15000m` | 0 % |
| A02 | `purchasePrice >= 15000m && purchasePrice <= 20000m` | 5 % |
| A03 | `purchasePrice > 20000m && purchasePrice < 25000m` | 7 % |
| A04 | `purchasePrice >= 25000m` | 8,5 % |

## Konkrete Testfälle – Grenzwertanalyse

Die Grenzwertanalyse prüft Werte direkt unterhalb, auf und oberhalb einer Grenze.
So erkennt man beispielsweise, ob versehentlich `<` statt `<=` verwendet wurde.
Hier beträgt der Abstand jeweils 0,01 CHF.

| Testfall-ID | Kaufpreis in CHF | Erwarteter Rabatt | Begründung |
| --- | --- | --- | --- |
| K01 | 14'999,99 | 0 % | 0,01 CHF unter 15'000 CHF; noch kein Rabatt. |
| K02 | 15'000,00 | 5 % | Exakt auf der unteren Grenze der 5-%-Stufe. |
| K03 | 15'000,01 | 5 % | 0,01 CHF über 15'000 CHF; innerhalb der 5-%-Stufe. |
| K04 | 19'999,99 | 5 % | 0,01 CHF unter 20'000 CHF; weiterhin 5 %. |
| K05 | 20'000,00 | 5 % | Die obere Grenze gehört noch zur 5-%-Stufe. |
| K06 | 20'000,01 | 7 % | 0,01 CHF über 20'000 CHF; Wechsel auf 7 %. |
| K07 | 24'999,99 | 7 % | 0,01 CHF unter 25'000 CHF; noch 7 %. |
| K08 | 25'000,00 | 8,5 % | Exakt 25'000 CHF gehört gemäss Annahme zur 8,5-%-Stufe. |
| K09 | 25'000,01 | 8,5 % | 0,01 CHF über 25'000 CHF; weiterhin 8,5 %. |

Die neun Fälle stehen mit denselben IDs als `decimal`-Werte in `BoundaryCases`.
`[Theory]` und `[MemberData]` führen für jede Zeile einen eigenen Test aus.
`Assert.Equal()` vergleicht den berechneten Rabatt mit dem erwarteten Wert.

Damit sind beide Varianten vorhanden: A01–A04 beschreiben die vier abstrakten
Äquivalenzklassen; K01–K09 prüfen konkrete Kaufpreise an den drei Grenzen.
K01 gehört zu A01, K02–K05 zu A02, K06–K07 zu A03 und K08–K09 zu A04.
Alle neun konkreten Grenzwerttests bleiben erhalten.

### Ergänzende konkrete Testfälle

Werte im Inneren der Klassen ergänzen die neun Grenzwertfälle: Sie prüfen, ob
auch typische Kaufpreise den richtigen Rabatt liefern. Das kann beispielsweise
eine fehlerhafte Sonderbehandlung ausschliesslich der Grenzwerte aufdecken.
K10 prüft zusätzlich die angenommene untere Eingabegrenze. Die nach oben offene
Klasse A04 besitzt keinen Mittelpunkt; 30'000 CHF ist ein typischer Vertreter.

| Testfall-ID | Kaufpreis in CHF | Erwarteter Rabatt | Begründung |
| --- | --- | --- | --- |
| K10 | 0,00 | 0 % | Untergrenze des angenommenen gültigen Eingabebereichs, A01. |
| K11 | 10'000,00 | 0 % | Repräsentativer Wert im Inneren von A01. |
| K12 | 17'500,00 | 5 % | Repräsentativer Wert im Inneren von A02. |
| K13 | 22'500,00 | 7 % | Repräsentativer Wert im Inneren von A03. |
| K14 | 30'000,00 | 8,5 % | Repräsentativer Wert im Inneren von A04. |

K10–K14 sind als feste `decimal`-Testdaten in `RepresentativeCases` hinterlegt
und werden durch eine eigene konkrete Testmethode ausgeführt. Insgesamt gibt
es 14 ausführbare Tests. Die abstrakten Fälle A01–A04 bleiben der Testentwurf.

### Welche Fehler erkennen die Tests?

- K02 erkennt, wenn bei 15'000 CHF noch 0 % gewährt werden, etwa durch `<=`
  statt `<` in der ersten Abfrage.
- K05 erkennt `<` statt `<=` bei 20'000 CHF: Dann würden fälschlich 7 % gelten.
- K08 erkennt eine falsche Zuordnung von exakt 25'000 CHF zu 7 %, etwa durch
  `<=` statt `<` in der dritten Abfrage. Entsprechend würde bei einer Abfrage
  der höchsten Stufe `>` statt `>=` durch diesen Fall auffallen.
- Die Nachbarwerte prüfen beide Seiten jeder Grenze. Die festen erwarteten
  Rabatte erkennen auch falsche Prozentwerte.

Diese Zuordnung ergibt sich aus der Prüfung der Testdaten und Vergleichsoperatoren;
ein automatisiertes Mutationstest-Werkzeug wird nicht eingesetzt. Einzelne
Testwerte beweisen nicht das Verhalten für sämtliche möglichen `decimal`-Werte.

Die abstrakten Bedingungen werden als Testentwurf in der Tabelle dokumentiert.
Automatisiert werden die konkreten Beispiele geprüft. Ihre erwarteten Rabatte
sind feste Testdaten aus den Geschäftsregeln und werden nicht mit derselben
Verzweigungslogik wie in `CalculateDiscount` berechnet.

## Unit-Tests ausführen

Voraussetzung: .NET SDK 10 und beim ersten Ausführen Zugriff auf NuGet für die
Testpakete. Im Repository-Hauptordner folgende Befehle ausführen:

```powershell
cd "tag-02/aufgaben/Übung 1"
dotnet build
dotnet test
```

Erwartung: 14 erfolgreiche Tests (neun Grenzwertfälle und fünf ergänzende Fälle).
`bin/` und `obj/` werden durch die bestehende
`.gitignore` des Repositorys ausgeschlossen.

## Annahmen

- Exakt 25'000 CHF wird der Rabattstufe von 8,5 % zugeordnet. Dieser Grenzwert
  war in der ursprünglichen Aufgabenstellung nicht eindeutig definiert; die
  hier umgesetzten Geschäftsregeln legen ihn ausdrücklich fest.
- Als gültiger Eingabebereich wird für diese Übung `0m <= purchasePrice` bis
  `decimal.MaxValue` angenommen. Die Einbeziehung von 0 CHF ist eine Annahme,
  keine ausdrücklich vorgegebene Geschäftsregel. Eine weitere fachliche
  Preisobergrenze ist nicht angegeben.
- Negative Kaufpreise sind im Original nicht geregelt und liegen ausserhalb
  dieses angenommenen Bereichs. Es wird keine zusätzliche Validierungsregel
  erfunden: Die aktuelle Implementierung liefert dafür technisch 0 %.
  Dies ist keine fachliche Festlegung für negative Preise und wird nicht als
  Anforderung durch einen Unit-Test festgeschrieben.
- Die Methode berechnet ausschliesslich den Rabatt als Prozentwert, weder den
  Rabattbetrag in CHF noch den Endpreis.
- Es wird nicht gerundet. Die Testfälle verwenden zwei Nachkommastellen;
  auch andere `decimal`-Werte werden direkt anhand der Grenzen verglichen.
- Der bestehende Ordner `tag-02/aufgaben` entspricht „Tag 2 / Aufgaben“.
  Der neue Unterordner heisst wie verlangt `Übung 1`.
- .NET-Version, Testpakete und Projektformat folgen der vorhandenen C#-Übung
  unter `tag-01/hausaufgaben/calculate-price`.
