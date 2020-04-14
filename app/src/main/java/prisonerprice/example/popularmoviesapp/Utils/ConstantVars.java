package prisonerprice.example.popularmoviesapp.Utils;

public class ConstantVars {

    public final static int STATE_POP = 42;
    public final static int STATE_HIGH = 41;
    public final static int STATE_FAV = 40;

    // 1 day, for the ease of testing
    public final static long EXPIRATION_TIME = 1000L * 60 * 1440;

    /*
    Network Constants
     */
    private static final String API_KEY = "You need to use your own";

    public static final String GET_MOST_POPULAR_MOVIES = "https://api.themoviedb.org/3/movie/popular?api_key=" +
            API_KEY + "&language=en-US&page=1";

    public static final String GET_TOP_RATED_MOVIES = "https://api.themoviedb.org/3/movie/top_rated?api_key=" +
            API_KEY + "&language=en-US&page=1";

    public static final String GET_FAVORITE_MOVIES = "get_favorite_movies";

    public static final String GET_VIDEOS = "https://api.themoviedb.org/3/movie/" + " " + "/videos?api_key=" +
            API_KEY;

    public static final String GET_COMMENTS = "https://api.themoviedb.org/3/movie/" + " " + "/reviews?api_key=" +
            API_KEY;

    public static final String VIDEO_PREFIX = "https://www.youtube.com/watch?v=";
}
