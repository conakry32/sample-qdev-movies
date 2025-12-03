# 🏴‍☠️ Pirate's Movie Treasure Chest - Spring Boot Demo Application

Ahoy matey! Welcome to the most swashbuckling movie catalog web application on the seven seas! Built with Spring Boot and featuring a pirate-themed search interface that'll make ye want to hunt for cinematic treasure!

## ⚓ Features

- **🎬 Movie Catalog**: Browse 12 classic movies with detailed information, displayed in a treasure chest grid
- **🔍 Pirate Search & Filtering**: Hunt for movies by name, ID, or genre with our pirate-themed search interface
- **📋 Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **⭐ Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **📱 Responsive Design**: Mobile-first design that works on all devices, from ship to shore
- **🌙 Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **🏴‍☠️ Pirate Language**: All search functionality includes authentic pirate language and messaging

## 🛠️ Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**
- **Thymeleaf** for templating
- **JSON** for data storage

## 🚀 Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **🏴‍☠️ Pirate's Movie Treasure Chest**: http://localhost:8080/movies
- **🔍 Movie Search**: Use the search form on the main page or directly access http://localhost:8080/movies/search
- **📋 Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **🔧 JSON API**: http://localhost:8080/api/movies/search (for tech-savvy pirates!)

## 🏴‍☠️ New Search & Filtering Features

### Web Interface Search
The main movies page now includes a pirate-themed search form where ye can:
- **Search by Movie Name**: Partial matches allowed, case-insensitive (e.g., "prison" finds "The Prison Escape")
- **Search by Movie ID**: Exact match only (e.g., ID "1" finds the first movie)
- **Filter by Genre**: Choose from available genres in the dropdown
- **Combined Search**: Use multiple criteria together for precise treasure hunting!

### Search Examples
- Find all Adventure movies: Select "Adventure" from genre dropdown
- Find movies with "the" in the name: Enter "the" in movie name field
- Find a specific movie: Enter the exact ID number

### API Endpoints

#### 🔍 Search Movies (HTML Response)
```
GET /movies/search?name={name}&id={id}&genre={genre}
```
Returns an HTML page with filtered movie results and pirate-themed messages.

**Parameters:**
- `name` (optional): Movie name to search for (partial matches, case-insensitive, max 100 characters)
- `id` (optional): Exact movie ID to find (positive integer)
- `genre` (optional): Genre to filter by (case-insensitive, max 50 characters)

**Examples:**
```
http://localhost:8080/movies/search?name=prison
http://localhost:8080/movies/search?genre=Drama
http://localhost:8080/movies/search?name=the&genre=Adventure
http://localhost:8080/movies/search?id=1
```

#### 🔧 Search Movies API (JSON Response)
```
GET /api/movies/search?name={name}&id={id}&genre={genre}
```
Returns JSON response for API consumers with pirate-themed messages.

**Response Format:**
```json
{
  "movies": [...],
  "totalResults": 2,
  "searchCriteria": {
    "name": "prison",
    "id": "",
    "genre": ""
  },
  "pirateMessage": "Ahoy! Found 1 pieces of treasure!"
}
```

**Error Response:**
```json
{
  "error": "Movie name too long (max 100 characters)",
  "pirateMessage": "Arrr! That movie name be too long for our treasure map!"
}
```

## 🏗️ Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Business logic with search methods
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data (12 movies)
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       ├── static/css/
│       │   └── movies.css                    # Enhanced CSS with search form styling
│       └── templates/
│           ├── movies.html                   # Main page with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Comprehensive unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MoviesControllerTest.java     # Controller tests with search scenarios
            ├── MovieServiceTest.java         # Service tests for search functionality
            └── MovieTest.java                # Model tests
```

## 🧪 Testing

Run all tests including the new search functionality:
```bash
mvn test
```

The test suite includes:
- **MovieServiceTest**: Comprehensive tests for search methods with various scenarios
- **MoviesControllerTest**: Tests for both HTML and JSON API endpoints
- **Edge Case Testing**: Invalid inputs, empty results, performance tests
- **Pirate Language Testing**: Ensures proper pirate-themed messaging

## 🔧 API Endpoints (Complete List)

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with the search form and pirate theming.

### Search Movies (HTML)
```
GET /movies/search?name={name}&id={id}&genre={genre}
```
Returns filtered movie results with pirate-themed messages and search form.

### Search Movies (JSON API)
```
GET /api/movies/search?name={name}&id={id}&genre={genre}
```
Returns JSON response with filtered movies and pirate messages.

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

## 🏴‍☠️ Pirate Language Features

The application includes authentic pirate language throughout:
- **Search Form Labels**: "Hunt for Treasure!", "Choose yer favorite genre, ye landlubber!"
- **Success Messages**: "Ahoy! Found X pieces of treasure matching yer criteria!"
- **Error Messages**: "Arrr! No treasure found matching yer search. Try another search, ye savvy sailor!"
- **Validation Messages**: "Arrr! That movie name be too long for our treasure map!"
- **Code Comments**: Pirate-themed documentation throughout the codebase

## 🛠️ Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

Check the logs for pirate-themed error messages:
```bash
tail -f logs/application.log
```

## 🤝 Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the treasure chest (movies.json)
- Enhance the pirate UI/UX with more nautical elements
- Add new search features like director or year filtering
- Improve the responsive design for different ship sizes
- Add more pirate language and themed elements

## 📊 Search Performance

The search functionality is optimized for the current dataset:
- **In-memory filtering**: Fast searches on 12 movies
- **Case-insensitive matching**: Efficient string operations
- **Multiple criteria support**: AND logic for combined searches
- **Input validation**: Prevents performance issues with large inputs

## 🏴‍☠️ Pirate Easter Eggs

Look out for these hidden pirate treasures in the application:
- Pirate emojis throughout the interface
- Nautical terminology in error messages
- Treasure chest themed styling
- Pirate flag in the page title
- "Arrr!" and "Ahoy!" in log messages

## 📜 License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

*"Arrr! May yer code be bug-free and yer searches swift, ye savvy developer!"* 🏴‍☠️
