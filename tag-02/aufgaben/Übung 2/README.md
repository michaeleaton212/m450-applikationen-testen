# Übung 2 – Blackbox, Whitebox und Graybox

## Aufgabenstellung

Ordne die folgenden zehn Situationen den Testmethoden Blackbox, Whitebox oder
Graybox zu und begründe jede Entscheidung. Dokumentiere bei mehrdeutigen
Situationen die getroffenen Annahmen.

## Theoretische Grundlagen

- **Blackbox:** Testen des externen Verhaltens ohne Kenntnis der internen
  Implementierung. Eingaben und beobachtete Ausgaben werden mit dem erwarteten
  Verhalten verglichen.
- **Whitebox:** Testen unter Berücksichtigung des Quellcodes und der internen
  Programmstruktur, beispielsweise von Verzweigungen oder Ausführungspfaden.
- **Graybox:** Testen mit teilweisem Wissen über die interne Implementierung.
  Dieses Wissen wird gezielt für die Auswahl oder Auswertung der Tests genutzt.

Entscheidend ist nicht das verwendete Werkzeug oder der Installationsort,
sondern welches interne Wissen beim Testen tatsächlich eingesetzt wird.
Auch mit vorhandenem Quellcode kann ein Test rein auf Anforderungen beruhen
und damit ein Blackbox-Test sein.

## Lösungen

| Nr. | Situation | Testmethode | Begründung |
| --- | --- | --- | --- |
| 1 | Anmeldung auf einer Webseite | Blackbox | Nur Eingabe und Ausgabe sind bekannt. |
| 2 | Unit-Tests für die Klasse eines Arbeitskollegen | Whitebox | Vorausgesetzt, der Quellcode ist bekannt und wird bei der Testerstellung berücksichtigt. |
| 3 | Ausführen einer Funktion über das Terminal | Blackbox | Nur das äussere Verhalten wird überprüft. |
| 4 | Daten anhand einer API-Dokumentation abrufen | Blackbox | Die Schnittstelle ist bekannt, nicht aber die interne Implementierung. |
| 5 | Testen einer Klasse mit einer Abhängigkeit zu einem Software-Paket | Graybox | Die eigene Implementierung ist bekannt, die interne Implementierung der Abhängigkeit möglicherweise nicht. |
| 6 | End-to-End-Tests ausschliesslich anhand von Anforderungen erstellen | Blackbox | Die Tests basieren auf dem erwarteten Verhalten. |
| 7 | Vollständiger Zugriff auf eine Funktion via API | Blackbox | Der Zugriff auf eine API ermöglicht nicht automatisch Einsicht in die Implementierung. |
| 8 | Einem LLM eine Frage stellen und eine Antwort erhalten | Blackbox | Nur Eingabe und Ausgabe werden betrachtet. |
| 9 | Ein lokal installiertes LLM befragen | Graybox | Vorausgesetzt, teilweise vorhandenes internes Wissen wird beim Testen verwendet. Ansonsten handelt es sich um Blackbox. |
| 10 | Eine Webseite anpingen | Blackbox | Es wird lediglich die Erreichbarkeit überprüft. |

## Annahmen und fachliche Einordnung

- **Nr. 1, 3 und 8:** Bewertet werden nur die beobachtbaren Ergebnisse. Quellcode,
  interne Abläufe und Debugger werden nicht zur Testgestaltung verwendet.
- **Nr. 2:** Unit-Tests sind nicht automatisch Whitebox-Tests. Die Zuordnung
  setzt voraus, dass die interne Struktur der Klasse die Testauswahl beeinflusst.
  Werden nur Anforderungen berücksichtigt, ist Blackbox ebenfalls möglich.
- **Nr. 4 und 7:** Die API beschreibt den Zugriff und das erwartete Verhalten.
  Es wird angenommen, dass keine Implementierungsdetails für den Test verwendet
  werden. Auch vollständiger API-Zugriff bedeutet keinen Quellcodezugriff.
- **Nr. 5:** Als Testobjekt wird das Zusammenspiel der eigenen Klasse mit dem
  Paket betrachtet. Die eigene Implementierung wird bei der Testerstellung
  berücksichtigt, das Paket bleibt intern unbekannt. Das begründet Graybox.
  Allein eine Paketabhängigkeit macht einen Test noch nicht zu Graybox: Wird
  ausschliesslich die bekannte eigene Klasse isoliert und strukturbezogen
  getestet, ist Whitebox möglich; reine Verhaltenstests sind Blackbox.
- **Nr. 6:** Die Anforderungen bilden ausdrücklich die einzige Testgrundlage.
  Die Teststufe End-to-End legt für sich allein keine dieser Methoden fest.
- **Nr. 9:** Lokale Installation allein liefert keine ausreichende Begründung
  für Graybox. Angenommen wird, dass bekannte interne Details, etwa zur
  Verarbeitung der Eingabe, gezielt die Tests beeinflussen. Wird nur eine Frage
  eingegeben und die Antwort beurteilt, bleibt es auch lokal Blackbox.
- **Nr. 10:** Mit «Webseite anpingen» ist ein Ping an den zugehörigen Host gemeint.
  Eine Antwort prüft dessen Erreichbarkeit über ICMP, nicht die Funktion der
  Webseite über HTTP/HTTPS. Eine ausbleibende Antwort kann auch durch blockiertes
  ICMP entstehen. Die Prüfung bleibt Blackbox, da keine interne Implementierung
  untersucht wird.
