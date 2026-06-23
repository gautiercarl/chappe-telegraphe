# Chappe — Telegrafen-Simulation

Kurzdarstellung
- Java-Projekt zur Implementierung/Simulation des Chappe-Telegrafenprotokolls (Semesterprojekt / PA).

Voraussetzungen
- Java 21
- Maven

Build
- Lokales Build: `mvn clean package`

Ausführen
- Direkt aus kompilierten Klassen: `java -cp target/classes com.chappe.client.App`
- Falls ein Jar erzeugt wurde: `java -jar target/chappe-1.0-SNAPSHOT.jar`

Tests
- Unit-Tests ausführen: `mvn test`

Projektstruktur (wichtigste Pakete)
- `com.chappe.client` — Client / Einstiegspunkt (`App`)
- `com.chappe.logik` — Logik für Turm/Netzwerk (`MainTowerNode`, `TowerNode`)
- `com.chappe.model` — Datenmodelle (`Tower`, `Message`, ...)
- `com.chappe.network` — Netzwerk/Topologie (`FranceNetwork`)
- `com.chappe.repository` — Repositories und Hilfsklassen
- `com.chappe.service` — Services (z. B. `UDPService`, `WordService`)
- `com.chappe.utils` — Hilfsfunktionen und Schlüssel

Technische Dokumentation
- Die detaillierte technische Dokumentation liegt als PDF im Projekt: [Technische Dokumentation Chappe-protokoll.pdf](Technische%20Dokumentation%20Chappe-protokoll.pdf)

