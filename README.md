# 📦 localhost-schowek

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20.4-brightgreen.svg)
![Engine](https://img.shields.io/badge/Server-Paper%20%7C%20Spigot%20%7C%20Purpur-blue.svg)
![Build](https://img.shields.io/badge/Build-Maven-red.svg)
![Author](https://img.shields.io/badge/Author-Localhost-purple.svg)

Kompletny, profesjonalny i wysoce zoptymalizowany plugin **Schowka / Depozytu Przedmiotów PvP** dla serwerów Minecraft (Survival, Gildie, Anarchia, BoxPvP, Hardcore).

Plugin zapobiega noszeniu w ekwipunku niezrównoważonych ilości przedmiotów leczących i ucieczkowych (Koxy, Refile, Perły, Totemy, Strzały), automatycznie deponując ich nadmiar w bezpiecznym, wirtualnym schowku gracza.

---

## ✨ Funkcje i Możliwości

- **Automatyczny Depozyt (Auto-Schowek)**:
  - Przy podnoszeniu przedmiotów z ziemi, craftingu oraz zamykaniu skrzyń, każda ilość przekraczająca limit w EQ natychmiast trafia do wirtualnego schowka gracza.
  - Powiadomienia na pasku **ActionBar** oraz delikatny dźwięk przy zdeponowaniu nadmiaru.
- **Interaktywne Menu GUI (`/schowek` / `/depozyt`)**:
  - Estetyczne menu z podglądem na żywo stanu ekwipunku (`W EQ: X / Limit`) oraz zasobów w schowku (`W schowku: Y szt.`).
  - **LPM** – Wypłaca dany przedmiot do limitu w ekwipunku.
  - **PPM** – Wypłaca dokładnie 1 sztukę.
  - **Shift + LPM** – Wpłaca dany przedmiot z ekwipunku do schowka.
- **Błyskawiczne Uzupełnianie (One-Click Restock)**:
  - Centralny przycisk **"DOBIERZ WSZYSTKO DO LIMITU"** w GUI oraz szybka komenda `/schowek dobierz` uzupełniająca wszystkie zapasy gracza jednym kliknięciem (niezastąpione podczas dynamicznych walk PvP).
- **Trwały i Bezpieczny Zapis Danych**:
  - Wbudowany system zapisu w pliku `data/players.yml` z pamięcią podręczną RAM i asynchronicznym auto-zapisem (zero lagów na serwerze).
- **Pełne Wsparcie Administracyjne**:
  - Komenda `/schowek admin <gracz> <daj/zabierz/ustaw> <przedmiot> <ilość>` z inteligentnym auto-uzupełnianiem (TabCompleter).

---

## 🛡️ Domyślne Limity Przedmiotów

| Przedmiot | Materiał Minecraft | Domyślny limit w EQ | Slot w GUI |
| :--- | :---: | :---: | :---: |
| 🍏 **Koxy** | `ENCHANTED_GOLDEN_APPLE` | **2 szt.** | Slot 11 |
| 🍎 **Refile** | `GOLDEN_APPLE` | **12 szt.** | Slot 12 |
| 🔮 **Perły Kresu** | `ENDER_PEARL` | **4 szt.** | Slot 13 |
| 🪓 **Totemy** | `TOTEM_OF_UNDYING` | **1 szt.** | Slot 14 |
| 🏹 **Strzały** | `ARROW` | **32 szt.** | Slot 15 |

*Wszystkie limity, nazwy, przedmioty oraz sloty w GUI można w 100% dowolnie konfigurować w `config.yml`.*

---

## 💻 Komendy i Uprawnienia

### Komendy:
Główna komenda: `/schowek` (aliasy: `/depozyt`, `/schowekpvp`, `/limit`)

- `/schowek` – Otwiera graficzny interfejs (GUI) schowka.
- `/schowek dobierz` – Błyskawicznie dobiera wszystkie przedmioty ze schowka do limitu w EQ.
- `/schowek schowaj` – Natychmiast deponuje wszystkie nadmiarowe przedmioty do schowka.
- `/schowek status` – Wyświetla aktualny stan wszystkich zasobów schowka na czacie.
- `/schowek admin <gracz> <daj/zabierz/ustaw> <przedmiot> <ilość>` – Zarządza stanem schowka wybranego gracza.
- `/schowek reload` – Przeładowuje konfigurację `config.yml`.

### Uprawnienia (Permissions):

| Uprawnienie | Domyślnie | Opis |
| :--- | :---: | :--- |
| `schowek.use` | Wszyscy (`true`) | Dostęp do komendy `/schowek`, interfejsu GUI oraz automatycznego schowka |
| `schowek.admin` | Administratorzy (`op`) | Dostęp do komend administracyjnych (`/schowek admin`, `/schowek reload`) |

---

## 📂 Struktura Projektu

```text
localhost-schowek/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── pl/localhost/schowek/
        │       ├── Main.java                        # Główna klasa pluginu (Main)
        │       ├── command/
        │       │   └── SchowekCommand.java          # Obsługa komend graczy i administratorów
        │       ├── gui/
        │       │   └── SchowekGUI.java              # Interaktywny interfejs ekwipunku
        │       ├── listener/
        │       │   └── SchowekListener.java         # Auto-depozyt przy podnoszeniu, craftingu i skrzyniach
        │       ├── manager/
        │       │   ├── ConfigManager.java           # Menadżer konfiguracji i tłumaczeń
        │       │   ├── DataManager.java             # Bezpieczny menadżer zapisu danych graczy
        │       │   └── SchowekManager.java          # Logika biznesowa wypłat, wpłat i restocku
        │       └── model/
        │           └── SchowekItem.java             # Klasa modelu konfigurowalnego przedmiotu
        └── resources/
            ├── config.yml                           # Konfiguracja limitów, dźwięków i wiadomości
            └── plugin.yml                           # Metadane pluginu
```

---

## 🛠️ Jak Zbudować (Kompilacja)

### Wymagania:
- **Java 17** lub nowsza
- **Maven 3.8+**

### Kompilacja:
```bash
cd localhost-schowek
mvn clean package
```

Gotowy plik `.jar` zostanie wygenerowany w:
```text
target/localhost-schowek-1.0-SNAPSHOT.jar
```

---

## ⚙️ Wymagania i Zależności

- **Zależności:** Brak dodatkowych pluginów! Działa natywnie (*standalone*) na **Paper**, **Purpur** oraz **Spigot 1.20+**.
- **Kompatybilność:** Java 17+, Paper 1.20 - 1.20.4+.

---

## 👤 Autor

- **Autor:** Localhost
- **Discord:** `localhost_127001`
- **Projekt:** Seria pluginów serwerowych `localhost-*`
