package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ahoy! Test class for MovieService search functionality
 * These tests ensure our treasure hunting methods work like a true pirate's compass!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        // Create a real MovieService instance to test with actual JSON data
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // Should load 12 movies from the JSON file
        assertEquals(12, movies.size());
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("The Prison Escape", movie.get().getMovieName());
        assertEquals("Drama", movie.get().getGenre());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdInvalid() {
        Optional<Movie> movie1 = movieService.getMovieById(null);
        assertFalse(movie1.isPresent());
        
        Optional<Movie> movie2 = movieService.getMovieById(-1L);
        assertFalse(movie2.isPresent());
        
        Optional<Movie> movie3 = movieService.getMovieById(0L);
        assertFalse(movie3.isPresent());
    }

    // Arrr! Test the treasure hunting methods!
    @Test
    public void testSearchMoviesByNamePartialMatch() {
        List<Movie> results = movieService.searchMovies("prison", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results1 = movieService.searchMovies("PRISON", null, null);
        List<Movie> results2 = movieService.searchMovies("prison", null, null);
        List<Movie> results3 = movieService.searchMovies("Prison", null, null);
        
        assertEquals(1, results1.size());
        assertEquals(1, results2.size());
        assertEquals(1, results3.size());
        
        assertEquals(results1.get(0).getMovieName(), results2.get(0).getMovieName());
        assertEquals(results2.get(0).getMovieName(), results3.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameNoMatch() {
        List<Movie> results = movieService.searchMovies("nonexistent", null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByNameEmptyString() {
        List<Movie> results1 = movieService.searchMovies("", null, null);
        List<Movie> results2 = movieService.searchMovies("   ", null, null);
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertEquals(allMovies.size(), results1.size());
        assertEquals(allMovies.size(), results2.size());
    }

    @Test
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByIdNotFound() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertFalse(results.isEmpty());
        
        // All results should be Drama genre
        for (Movie movie : results) {
            assertEquals("Drama", movie.getGenre());
        }
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results1 = movieService.searchMovies(null, null, "DRAMA");
        List<Movie> results2 = movieService.searchMovies(null, null, "drama");
        List<Movie> results3 = movieService.searchMovies(null, null, "Drama");
        
        assertFalse(results1.isEmpty());
        assertEquals(results1.size(), results2.size());
        assertEquals(results2.size(), results3.size());
    }

    @Test
    public void testSearchMoviesByGenreNotFound() {
        List<Movie> results = movieService.searchMovies(null, null, "NonexistentGenre");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByGenreEmptyString() {
        List<Movie> results1 = movieService.searchMovies(null, null, "");
        List<Movie> results2 = movieService.searchMovies(null, null, "   ");
        List<Movie> allMovies = movieService.getAllMovies();
        
        assertEquals(allMovies.size(), results1.size());
        assertEquals(allMovies.size(), results2.size());
    }

    @Test
    public void testSearchMoviesCombinedCriteria() {
        // Search for a specific movie by name and genre
        List<Movie> results = movieService.searchMovies("prison", null, "Drama");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
        assertEquals("Drama", results.get(0).getGenre());
    }

    @Test
    public void testSearchMoviesCombinedCriteriaNoMatch() {
        // Search for a movie with conflicting criteria
        List<Movie> results = movieService.searchMovies("prison", null, "Action");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesAllCriteria() {
        // Search using all three criteria
        List<Movie> results = movieService.searchMovies("prison", 1L, "Drama");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesAllCriteriaConflicting() {
        // Search with conflicting ID and name
        List<Movie> results = movieService.searchMovies("prison", 2L, "Drama");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesNullParameters() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        List<Movie> allMovies = movieService.getAllMovies();
        assertEquals(allMovies.size(), results.size());
    }

    @Test
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        
        // Should contain unique genres from the JSON data
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Action/Crime"));
        assertTrue(genres.contains("Adventure/Fantasy"));
        
        // Should be sorted
        for (int i = 1; i < genres.size(); i++) {
            assertTrue(genres.get(i-1).compareTo(genres.get(i)) <= 0);
        }
        
        // Should not contain duplicates
        long uniqueCount = genres.stream().distinct().count();
        assertEquals(genres.size(), uniqueCount);
    }

    @Test
    public void testSearchMoviesWithSpecialCharacters() {
        // Test searching with special characters that might be in movie names
        List<Movie> results = movieService.searchMovies("'", null, null);
        // Should handle apostrophes gracefully without errors
        assertNotNull(results);
    }

    @Test
    public void testSearchMoviesPerformance() {
        // Test that search operations complete in reasonable time
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            movieService.searchMovies("test", null, null);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete 100 searches in less than 1 second
        assertTrue(duration < 1000, "Search operations took too long: " + duration + "ms");
    }

    @Test
    public void testSearchMoviesGenreWithSlash() {
        // Test searching for genres that contain slashes (like "Crime/Drama")
        List<Movie> results = movieService.searchMovies(null, null, "Crime/Drama");
        assertFalse(results.isEmpty());
        
        for (Movie movie : results) {
            assertEquals("Crime/Drama", movie.getGenre());
        }
    }

    @Test
    public void testSearchMoviesPartialGenreMatch() {
        // Genre search should be exact match, not partial
        List<Movie> results = movieService.searchMovies(null, null, "Crime");
        assertTrue(results.isEmpty()); // Should not match "Crime/Drama"
    }
}