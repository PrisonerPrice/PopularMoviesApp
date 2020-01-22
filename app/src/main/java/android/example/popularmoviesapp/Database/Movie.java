package android.example.popularmoviesapp.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "movie")
public class Movie {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "is_liked")
    private int isLiked;

    @ColumnInfo(name = "is_popular")
    private int isPopular;

    @ColumnInfo(name = "is_highly_ranked")
    private int isHighlyRanked;

    @ColumnInfo(name = "data_string")
    private String dataString;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @Ignore
    public Movie(int isLiked, int isPopular, int isHighlyRanked, String dataString, Date updatedAt) {
        this.isLiked = isLiked;
        this.isPopular = isPopular;
        this.isHighlyRanked = isHighlyRanked;
        this.dataString = dataString;
        this.updatedAt = updatedAt;
    }

    public Movie(int id, int isLiked, int isPopular, int isHighlyRanked, String dataString, Date updatedAt) {
        this.id = id;
        this.isLiked = isLiked;
        this.isPopular = isPopular;
        this.isHighlyRanked = isHighlyRanked;
        this.dataString = dataString;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDataString() {
        return dataString;
    }

    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
