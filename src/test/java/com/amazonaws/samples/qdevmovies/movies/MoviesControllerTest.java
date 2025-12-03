package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with pirate-themed test data
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "The Pirate's Treasure", "Captain Hook", 2023, "Adventure", "A swashbuckling adventure", 120, 4.5),
                    new Movie(2L, "Sea Battle", "Admiral Storm", 2022, "Action", "Epic naval combat", 140, 4.0),
                    new Movie(3L, "Treasure Island", "Long John Silver", 2021, "Adventure", "Classic pirate tale", 110, 4.8)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "The Pirate's Treasure", "Captain Hook", 2023, "Adventure", "A swashbuckling adventure", 120, 4.5));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> allMovies = getAllMovies();
                List<Movie> results = new ArrayList<>();
                
                for (Movie movie : allMovies) {
                    boolean matches = true;
                    
                    if (id != null && !movie.getId().equals(id)) {
                        matches = false;
                    }
                    
                    if (name != null && !name.trim().isEmpty()) {
                        String searchName = name.trim().toLowerCase();
                        String movieName = movie.getMovieName().toLowerCase();
                        if (!movieName.contains(searchName)) {
                            matches = false;
                        }
                    }
                    
                    if (genre != null && !genre.trim().isEmpty()) {
                        String searchGenre = genre.trim().toLowerCase();
                        String movieGenre = movie.getGenre().toLowerCase();
                        if (!movieGenre.equals(searchGenre)) {
                            matches = false;
                        }
                    }
                    
                    if (matches) {
                        results.add(movie);
                    }
                }
                
                return results;
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Action", "Adventure", "Drama");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        // Verify that movies and genres are added to model
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("allGenres"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size());
        
        @SuppressWarnings("unchecked")
        List<String> genres = (List<String>) model.getAttribute("allGenres");
        assertEquals(3, genres.size());
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
        
        assertTrue(model.containsAttribute("movie"));
        Movie movie = (Movie) model.getAttribute("movie");
        assertEquals("The Pirate's Treasure", movie.getMovieName());
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
        
        assertTrue(model.containsAttribute("title"));
        assertTrue(model.containsAttribute("message"));
    }

    // Arrr! Test the treasure hunt functionality!
    @Test
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("pirate", null, null, model);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("searchMessage"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Pirate's Treasure", movies.get(0).getMovieName());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Found 1 pieces of treasure"));
    }

    @Test
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, 2L, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Sea Battle", movies.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "Adventure", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(2, movies.size()); // Two adventure movies
    }

    @Test
    public void testSearchMoviesNoResults() {
        String result = moviesController.searchMovies("nonexistent", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(0, movies.size());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("No treasure found"));
    }

    @Test
    public void testSearchMoviesWithInvalidLongName() {
        String longName = "a".repeat(101); // Too long!
        String result = moviesController.searchMovies(longName, null, null, model);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("errorMessage"));
        String errorMessage = (String) model.getAttribute("errorMessage");
        assertTrue(errorMessage.contains("too long for our treasure map"));
    }

    @Test
    public void testSearchMoviesWithInvalidLongGenre() {
        String longGenre = "a".repeat(51); // Too long!
        String result = moviesController.searchMovies(null, null, longGenre, model);
        assertEquals("movies", result);
        
        assertTrue(model.containsAttribute("errorMessage"));
        String errorMessage = (String) model.getAttribute("errorMessage");
        assertTrue(errorMessage.contains("too long for our charts"));
    }

    @Test
    public void testSearchMoviesCombinedCriteria() {
        String result = moviesController.searchMovies("treasure", 1L, "Adventure", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Pirate's Treasure", movies.get(0).getMovieName());
    }

    // Test the JSON API endpoint - for tech-savvy pirates!
    @Test
    public void testSearchMoviesApiSuccess() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("pirate", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("movies"));
        assertTrue(body.containsKey("totalResults"));
        assertTrue(body.containsKey("pirateMessage"));
        
        assertEquals(1, body.get("totalResults"));
        String pirateMessage = (String) body.get("pirateMessage");
        assertTrue(pirateMessage.contains("Found 1 pieces of treasure"));
    }

    @Test
    public void testSearchMoviesApiNoResults() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("nonexistent", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals(0, body.get("totalResults"));
        
        String pirateMessage = (String) body.get("pirateMessage");
        assertTrue(pirateMessage.contains("No treasure found"));
    }

    @Test
    public void testSearchMoviesApiInvalidInput() {
        String longName = "a".repeat(101);
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(longName, null, null);
        
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("pirateMessage"));
        
        String pirateMessage = (String) body.get("pirateMessage");
        assertTrue(pirateMessage.contains("too long for our treasure map"));
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("The Pirate's Treasure", movies.get(0).getMovieName());
        
        List<String> genres = mockMovieService.getAllGenres();
        assertEquals(3, genres.size());
        assertTrue(genres.contains("Adventure"));
    }
}
