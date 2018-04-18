package app.ie.sceneit;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Movie {
    public String title;
    public String release;
    public String overview;
    public String posterUrl;

    public Movie() {
        // Default constructor required for calls to DataSnapshot.getValue
    }

    public Movie(String title, String release, String overview, String posterUrl) {
        this.title = title;
        this.release = release;
        this.overview = overview;
        this.posterUrl = posterUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease() {
        return release;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("release", release);
        result.put("overview", overview);
        result.put("posterUrl", posterUrl);

        return result;
    }
}
