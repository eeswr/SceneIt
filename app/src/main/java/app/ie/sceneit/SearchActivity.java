package app.ie.sceneit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import static app.ie.sceneit.FilmConfig.TITLE;



public class SearchActivity extends Activity {

    TextView responseView;
    EditText filmText;
    ListView filmList;
    Context activityContext;

    static final String API_KEY = "b1d7abb033c99a90dd0af6fab8471e1c";
    static final String API_URL = "https://api.themoviedb.org/3/search/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        filmText = (EditText) findViewById(R.id.filmText);
        filmList = (ListView) findViewById(R.id.filmList);
        findViewById(R.id.savePrompt).setVisibility(View.GONE);
        activityContext = this;

        Button queryButton = (Button) findViewById(R.id.searchButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(filmText.getWindowToken(), 0);
                findViewById(R.id.savePrompt).setVisibility(View.VISIBLE);
                new RetrieveFeedTask().execute();
            }
        });
    }

    public class RetrieveFeedTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        private Exception exception;

        protected ArrayList<Movie> doInBackground(Void... urls) {
            String film = filmText.getText().toString();
            // Do some validation here

            try {
                URL url = new URL(API_URL + "movie?" + "api_key=" + API_KEY + "&language=en-US&query=" + film + "&page=1&include_adult=false");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String responseString = stringBuilder.toString();
                    JSONObject response = new JSONObject(responseString);
                    JSONArray resultList = response.getJSONArray("results");

                    return getMovieListFromJSONArray(resultList);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(ArrayList<Movie> movieList) {
            ListAdapter adapter = new MovieListAdapter(activityContext, R.layout.film_card, movieList);
            filmList.setAdapter(adapter);
        }
    }

    public void parseFilmJSON(View view) {

    }

    private ArrayList<Movie> getMovieListFromJSONArray(JSONArray jsonArray) {

        ArrayList<Movie> movieList = new ArrayList<Movie>();

        try {

            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject movieJSON = jsonArray.getJSONObject(i);

                    String title = movieJSON.getString("title");
                    String releaseDate = movieJSON.getString("release_date");
                    String overview = movieJSON.getString("overview");
                    Bitmap poster = null;

                    String posterPath = movieJSON.getString("poster_path");

                    if (posterPath != null) {
                        URL posterUrl =  new URL ("https://image.tmdb.org/t/p/w154/"+movieJSON.getString("poster_path"));
                        poster = BitmapFactory.decodeStream(posterUrl.openConnection().getInputStream());
                    }

                    movieList.add(new Movie(title, releaseDate, overview, poster));

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return movieList;
    }
}

