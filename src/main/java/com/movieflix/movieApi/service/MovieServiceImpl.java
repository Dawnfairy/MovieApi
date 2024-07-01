package com.movieflix.movieApi.service;

import com.movieflix.movieApi.dto.MovieDto;
import com.movieflix.movieApi.entities.Movie;
import com.movieflix.movieApi.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        // 1. upload the file
        String uploadedFileName = fileService.uploadFile(path, file);

        // 2. set the vale of field 'poster' as filename
        movieDto.setPoster(uploadedFileName);

        // 3. map dto to movie object
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        // 4. save the movie object - > saved movie object
        Movie savedMovie = movieRepository.save(movie);

        // 5. generate the posterUrl
        var posterUrl = baseUrl + "/file/" + uploadedFileName;

        // 6. map movie object to DTO object and return it
        return new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudio(),
                savedMovie.getMovieCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        // 1. check the data in DB and if exists, fetch the data of given ID
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException(("Movie not foumd ")));

        // 2. generate posterUrl
        var posterUrl = baseUrl + "/file/" + movie.getPoster();

        // 3. map to MovieDto object and return it
        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
    }

    @Override
    public List<MovieDto> getALlMovies() {

        // 1. fetch all data from DB
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();

        // 2. iterate through the list, genrate posterUrl for each movie obj,
        // and map to MovieDto obj
       for(Movie movie : movies) {
           var posterUrl = baseUrl + "/file/" + movie.getPoster();
           MovieDto movieDto = new MovieDto(
                   movie.getMovieId(),
                   movie.getTitle(),
                   movie.getDirector(),
                   movie.getStudio(),
                   movie.getMovieCast(),
                   movie.getReleaseYear(),
                   movie.getPoster(),
                   posterUrl
           );
           movieDtos.add(movieDto);
       }

        return movieDtos;
    }
}
