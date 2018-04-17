package app.ie.sceneit;

import android.graphics.Bitmap;

public class Movie {
    public String title;
    public String releaseDate;
    public String overview;
    public Bitmap poster;

    public Movie(String title, String releaseDate, String overview, Bitmap poster) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.poster = poster;
    }
}
