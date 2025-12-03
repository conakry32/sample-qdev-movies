package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Ahoy! Fetching all movies from our treasure chest");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("allGenres", movieService.getAllGenres());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy matey! Search for movies in our treasure chest!
     * This endpoint handles both JSON API requests and HTML form submissions.
     * 
     * @param name Movie name to search for (partial matches allowed)
     * @param id Exact movie ID to find
     * @param genre Genre to filter by
     * @param model Spring model for HTML responses
     * @return JSON response for API calls or HTML template for browser requests
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Arrr! Treasure hunt initiated with name: '{}', id: {}, genre: '{}'", name, id, genre);
        
        try {
            // Validate input parameters - no scurvy input allowed!
            if (name != null && name.trim().length() > 100) {
                logger.warn("Movie name search too long, ye scallywag!");
                model.addAttribute("errorMessage", "Arrr! That movie name be too long for our treasure map!");
                model.addAttribute("movies", movieService.getAllMovies());
                model.addAttribute("allGenres", movieService.getAllGenres());
                return "movies";
            }
            
            if (genre != null && genre.trim().length() > 50) {
                logger.warn("Genre search too long, ye landlubber!");
                model.addAttribute("errorMessage", "Arrr! That genre be too long for our charts!");
                model.addAttribute("movies", movieService.getAllMovies());
                model.addAttribute("allGenres", movieService.getAllGenres());
                return "movies";
            }
            
            // Perform the treasure hunt!
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Prepare the response with pirate flair
            if (searchResults.isEmpty()) {
                String searchCriteria = buildSearchCriteriaMessage(name, id, genre);
                model.addAttribute("searchMessage", "Arrr! No treasure found matching " + searchCriteria + ". Try another search, ye savvy sailor!");
                logger.info("No movies found for search criteria");
            } else {
                String searchCriteria = buildSearchCriteriaMessage(name, id, genre);
                model.addAttribute("searchMessage", "Ahoy! Found " + searchResults.size() + " pieces of treasure matching " + searchCriteria + "!");
                logger.info("Found {} movies matching search criteria", searchResults.size());
            }
            
            model.addAttribute("movies", searchResults);
            model.addAttribute("allGenres", movieService.getAllGenres());
            model.addAttribute("searchName", name);
            model.addAttribute("searchId", id);
            model.addAttribute("searchGenre", genre);
            
            return "movies";
            
        } catch (Exception e) {
            logger.error("Blimey! Error during movie search: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Arrr! Something went wrong during the treasure hunt. Try again, matey!");
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("allGenres", movieService.getAllGenres());
            return "movies";
        }
    }

    /**
     * JSON API endpoint for movie search - for ye tech-savvy pirates!
     * Returns pure JSON response for API consumers.
     */
    @GetMapping("/api/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMoviesApi(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("API treasure hunt initiated with name: '{}', id: {}, genre: '{}'", name, id, genre);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate input parameters
            if (name != null && name.trim().length() > 100) {
                response.put("error", "Movie name too long (max 100 characters)");
                response.put("pirateMessage", "Arrr! That movie name be too long for our treasure map!");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (genre != null && genre.trim().length() > 50) {
                response.put("error", "Genre too long (max 50 characters)");
                response.put("pirateMessage", "Arrr! That genre be too long for our charts!");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Perform the search
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            response.put("movies", searchResults);
            response.put("totalResults", searchResults.size());
            response.put("searchCriteria", Map.of(
                "name", name != null ? name : "",
                "id", id != null ? id : "",
                "genre", genre != null ? genre : ""
            ));
            
            if (searchResults.isEmpty()) {
                response.put("pirateMessage", "Arrr! No treasure found matching yer criteria. Try another search, ye savvy sailor!");
            } else {
                response.put("pirateMessage", "Ahoy! Found " + searchResults.size() + " pieces of treasure!");
            }
            
            logger.info("API search completed successfully with {} results", searchResults.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Blimey! Error during API movie search: {}", e.getMessage(), e);
            response.put("error", "Internal server error during search");
            response.put("pirateMessage", "Arrr! Something went wrong during the treasure hunt. Try again, matey!");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Helper method to build a user-friendly search criteria message
     */
    private String buildSearchCriteriaMessage(String name, Long id, String genre) {
        StringBuilder criteria = new StringBuilder();
        boolean hasMultiple = false;
        
        if (name != null && !name.trim().isEmpty()) {
            criteria.append("name containing '").append(name.trim()).append("'");
            hasMultiple = true;
        }
        
        if (id != null) {
            if (hasMultiple) criteria.append(" and ");
            criteria.append("ID ").append(id);
            hasMultiple = true;
        }
        
        if (genre != null && !genre.trim().isEmpty()) {
            if (hasMultiple) criteria.append(" and ");
            criteria.append("genre '").append(genre.trim()).append("'");
        }
        
        return criteria.length() > 0 ? criteria.toString() : "yer search";
    }
}