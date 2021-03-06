package prisonerprice.example.popularmoviesapp.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie WHERE id = :id")
    Movie getMovieById(int id);

    @Query("SELECT * FROM movie ORDER BY updated_at")
    List<Movie> getAllMovies();

    @Query("SELECT * FROM movie ORDER BY updated_at")
    LiveData<List<Movie>> getAllLiveMovies();

    @Query("SELECT * FROM movie WHERE is_popular = 1 ORDER BY updated_at DESC")
    List<Movie> getPopularMovies();

    @Query("SELECT * FROM movie WHERE is_highly_ranked = 1 ORDER BY updated_at DESC")
    List<Movie> getHighlyRankedMovies();

    @Query("SELECT * FROM movie WHERE is_liked = 1 ORDER BY updated_at DESC")
    List<Movie> getFavoriteMovies();

    @Query("SELECT * FROM movie WHERE is_popular = 1 ORDER BY updated_at DESC")
    LiveData<List<Movie>> getLivePopularMovies();

    @Query("SELECT * FROM movie WHERE is_highly_ranked = 1")
    LiveData<List<Movie>> getLiveHighlyRankedMovies();

    @Query("SELECT * FROM movie WHERE is_liked = 1 ORDER BY updated_at DESC")
    LiveData<List<Movie>> getLiveFavoriteMovies();

    @Insert
    void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Query("DELETE FROM movie")
    void deleteMovie();

}
