package prisonerprice.example.popularmoviesapp.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie")
public class Movie {

    @PrimaryKey
    private int id;

    @ColumnInfo
    private String title;

    @ColumnInfo(name = "poster_url")
    private String posterUrl;

    @ColumnInfo(name = "user_rating")
    private String userRating;

    @ColumnInfo(name = "release_year")
    private int releaseYear;

    @ColumnInfo
    private String description;

    @ColumnInfo
    private String comment;

    @ColumnInfo(name = "trailer_url_1")
    private String trailerUrl1;

    @ColumnInfo(name = "trailer_url_2")
    private String trailerUrl2;

    @ColumnInfo(name = "is_liked")
    private int isLiked;

    @ColumnInfo(name = "is_popular")
    private int isPopular;

    @ColumnInfo(name = "is_highly_ranked")
    private int isHighlyRanked;

    @ColumnInfo(name = "updated_at")
    private Long updatedAt;

    @Ignore
    public Movie(String title, String posterUrl, String userRating, int releaseYear, String description, String comment, String trailerUrl1, String trailerUrl2, int isLiked, int isPopular, int isHighlyRanked, Long updatedAt) {
        this.title = title;
        this.posterUrl = posterUrl;
        this.userRating = userRating;
        this.releaseYear = releaseYear;
        this.description = description;
        this.comment = comment;
        this.trailerUrl1 = trailerUrl1;
        this.trailerUrl2 = trailerUrl2;
        this.isLiked = isLiked;
        this.isPopular = isPopular;
        this.isHighlyRanked = isHighlyRanked;
        this.updatedAt = updatedAt;
    }

    public Movie(int id, String title, String posterUrl, String userRating, int releaseYear, String description, String comment, String trailerUrl1, String trailerUrl2, int isLiked, int isPopular, int isHighlyRanked, Long updatedAt) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.userRating = userRating;
        this.releaseYear = releaseYear;
        this.description = description;
        this.comment = comment;
        this.trailerUrl1 = trailerUrl1;
        this.trailerUrl2 = trailerUrl2;
        this.isLiked = isLiked;
        this.isPopular = isPopular;
        this.isHighlyRanked = isHighlyRanked;
        this.updatedAt = updatedAt;
    }

    public Movie(Movie movie) {
        this.id = movie.getId();
        this.title = movie.getTitle();
        this.posterUrl = movie.getPosterUrl();
        this.userRating = movie.getUserRating();
        this.releaseYear = movie.getReleaseYear();
        this.description = movie.getDescription();
        this.comment = movie.getComment();
        this.trailerUrl1 = movie.getTrailerUrl1();
        this.trailerUrl2 = movie.getTrailerUrl2();
        this.isLiked = movie.getIsLiked();
        this.isPopular = movie.getIsPopular();
        this.isHighlyRanked = movie.getIsHighlyRanked();
        this.updatedAt = movie.getUpdatedAt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTrailerUrl1() {
        return trailerUrl1;
    }

    public void setTrailerUrl1(String trailerUrl1) {
        this.trailerUrl1 = trailerUrl1;
    }

    public String getTrailerUrl2() {
        return trailerUrl2;
    }

    public void setTrailerUrl2(String trailerUrl2) {
        this.trailerUrl2 = trailerUrl2;
    }

    public int getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }

    public int getIsPopular() {
        return isPopular;
    }

    public void setIsPopular(int isPopular) {
        this.isPopular = isPopular;
    }

    public int getIsHighlyRanked() {
        return isHighlyRanked;
    }

    public void setIsHighlyRanked(int isHighlyRanked) {
        this.isHighlyRanked = isHighlyRanked;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String encoder() {
        String record = null;
        record = id + "  "
                + title + "  "
                + posterUrl + "  "
                + userRating + "  "
                + releaseYear + "  "
                + description + "  "
                + comment + "  "
                + trailerUrl1 + "  "
                + trailerUrl2 + "  "
                + isLiked + "  "
                + isPopular + "  "
                + isHighlyRanked + "  "
                + updatedAt;
        return record;
    }

    public static Movie decoder(String record){
        String[] data = record.split("  ");
        if (data == null || data.length < 12) return null;
        Movie movie = new Movie(
                Integer.parseInt(data[0]),
                data[1],
                data[2],
                data[3],
                Integer.parseInt(data[4]),
                data[5],
                data[6],
                data[7],
                data[8],
                Integer.parseInt(data[9]),
                Integer.parseInt(data[10]),
                Integer.parseInt(data[11]),
                Long.parseLong(data[12])
        );
        return movie;
    }
}
