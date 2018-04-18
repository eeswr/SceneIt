package app.ie.sceneit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    EditText filmText;
    ListView filmList;
    Context activityContext;
    ArrayList<Movie> CurrentSearchResults;
    private DatabaseReference mDatabase;

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
        CurrentSearchResults = new ArrayList<Movie>();

        mDatabase = FirebaseDatabase.getInstance().getReference();


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

        filmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(SearchActivity.this);
                // Setting Dialog Message
                saveDialog.setMessage("Do you want to save this film?");

                saveDialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });

                saveDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Saving film", Toast.LENGTH_SHORT).show();
                        Movie movie = CurrentSearchResults.get(position);

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String movieId = mDatabase.child("movies").child(userId).push().getKey();
                        mDatabase.child("movies").child(userId).child(movieId).setValue(movie.toMap());
                        finish();
                    }
                });

                // Showing Alert Message
                saveDialog.show().getWindow().setLayout(850,450);;
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

                    CurrentSearchResults = getMovieListFromJSONArray(resultList);
                    return CurrentSearchResults;
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



    private ArrayList<Movie> getMovieListFromJSONArray(JSONArray jsonArray) {

        ArrayList<Movie> movieList = new ArrayList<Movie>();

        try {

            if (jsonArray != null) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject movieJSON = jsonArray.getJSONObject(i);

                    String title = movieJSON.getString("title");
                    String releaseDate = movieJSON.getString("release_date");
                    String overview = movieJSON.getString("overview");

                    String posterPath = movieJSON.getString("poster_path");
                    String posterUrl = null;

                    if (posterPath != null) {
                        posterUrl = "https://image.tmdb.org/t/p/w154" + posterPath;
                    }

                    movieList.add(new Movie(title, releaseDate, overview, posterUrl));

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return movieList;
    }


}

