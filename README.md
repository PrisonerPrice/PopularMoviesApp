# Popular Movies App

![Image](https://raw.githubusercontent.com/PrisonerPrice/PopularMoviesApp/master/Image/6BA8EE8F-72A1-4393-9332-FC06AE3500A0.JPG)

## Project overview

This project fully utilizes RecyclerView to provide a polished and smooth user experience for the users to explore popular movies sorting by either popularity or user rating. You can also click on the movie posters to read detailed information of the movie.

## API Connection

This application needs API connection with The Movie DB (TMDb), you can find settings information including how to generate an API key via this link https://www.themoviedb.org/settings/api After getting the key, you can inplement the key in NetworkUtils.class at this place:

	private static final String API_KEY = "You need to define your";

This project also uses OKHttp and Picasso libaries. They are useful and straightforward, you can find resources here:

https://square.github.io/okhttp/

https://square.github.io/picasso/

My dependencies settings for these two libraries are shown as following:

	implementation 'com.squareup.picasso:picasso:2.71828'
	
	implementation "com.squareup.okhttp3:okhttp:4.2.1"


