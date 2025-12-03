package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! Search for treasure (movies) in our vast collection!
     * This method filters movies based on the search criteria ye provide.
     * 
     * @param name The movie name to search for (partial matches allowed, case-insensitive)
     * @param id The exact movie ID to find
     * @param genre The genre to filter by (case-insensitive)
     * @return List of movies matching yer search criteria, or empty list if no treasure found
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Arrr! Starting treasure hunt with criteria - name: '{}', id: {}, genre: '{}'", 
                   name, id, genre);
        
        List<Movie> searchResults = movies.stream()
            .filter(movie -> matchesSearchCriteria(movie, name, id, genre))
            .collect(Collectors.toList());
        
        logger.info("Treasure hunt complete! Found {} movies matching yer criteria", searchResults.size());
        return searchResults;
    }

    /**
     * Checks if a movie matches the search criteria like a true pirate examines treasure!
     * 
     * @param movie The movie to examine
     * @param name Name criteria (partial match, case-insensitive)
     * @param id ID criteria (exact match)
     * @param genre Genre criteria (case-insensitive)
     * @return true if the movie matches all provided criteria, false otherwise
     */
    private boolean matchesSearchCriteria(Movie movie, String name, Long id, String genre) {
        // If searching by ID, it must match exactly - no room for error on the high seas!
        if (id != null && !movie.getId().equals(id)) {
            return false;
        }
        
        // If searching by name, allow partial matches - even pirates make typos!
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            String movieName = movie.getMovieName().toLowerCase();
            if (!movieName.contains(searchName)) {
                return false;
            }
        }
        
        // If searching by genre, must match exactly - genres be sacred to us movie pirates!
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            String movieGenre = movie.getGenre().toLowerCase();
            if (!movieGenre.equals(searchGenre)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Get all available genres from our treasure chest of movies
     * Useful for showing what genres be available to search, matey!
     * 
     * @return List of unique genres found in our movie collection
     */
    public List<String> getAllGenres() {
        return movies.stream()
            .map(Movie::getGenre)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }
}
