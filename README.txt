FoodApp – nazwa jeszcze nie została wymyślona, na razie jest to tymczasowa.

Język programowania: Java
Baza danych: SQLite

Główne funkcje programu

Wprowadzanie składników: Użytkownik wprowadza listę składników.
Baza przepisów: Program posiada bazę przepisów z określonymi składnikami.
Wyszukiwanie dań: Algorytm porównuje wprowadzone składniki z przepisami i pokazuje dostępne dania.
Rekomendacje: Jeśli z wprowadzonych składników nie można przygotować żadnego dania, program proponuje przepisy z brakującymi składnikami.
Filtry: Możliwość filtrowania dań według kategorii (wegetariańskie, desery, szybkie dania itp.).


Struktura projektu:

src/
 ├── main/
 │   ├── java/
 │   │   ├── FoodApp.java          # Główny klasy do uruchomienia programu
 │   │   ├── MainController.java   # Klasa logiki programu (wejście/wyjście w konsoli)
 │   │   ├── Ingredient.java       # Klasa dla składnika
 │   │   ├── Recipe.java           # Klasa dla przepisu
 │   │   └── RecipeDAO.java        # Klasa dostępu do bazy danych (SQLite)
 │   └── resources/
 │       └── db/
 │           └── foodapp.db        # Baza danych SQLite z tabelami przepisów
 ├── test/
 │   └── java/
 └── pom.xml



Rozszerzenie z czasem:

- Kalendarz przepisów: Planowanie posiłków na tydzień.
- Rekomendacje dietetyczne: Uwzględnianie kalorii lub ograniczeń dietetycznych.
- Lokalizacja: Przepisy popularne w różnych krajach.
