package app.ie.sceneit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity {

    String userId;
    DatabaseReference mDatabase;
    ArrayList<Movie> movies;
    ArrayList<String> movieKeys;
    ListView viewList;
    MovieListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("/movies/" + userId);
        movies = new ArrayList<Movie>();
        movieKeys = new ArrayList<String>();
        viewList = (ListView) findViewById(R.id.viewList);

        adapter = new MovieListAdapter(this, R.layout.film_card, movies);
        viewList.setAdapter(adapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot){

                movies.clear();
                movieKeys.clear();

                Log.e("ViewActivity", "onDataChange Event Fired");
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    Movie movie = childSnapshot.getValue(Movie.class);
                    movies.add(0, movie);
                    movieKeys.add(0, childSnapshot.getKey());
                    Log.e("Movie from DB", movie.toMap().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled (DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        viewList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(ViewActivity.this);
                // Setting Dialog Message
                saveDialog.setMessage("Do you want to delete this film?");

                saveDialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                    }
                });

                saveDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Deleting film", Toast.LENGTH_SHORT).show();
                        deleteFilm(position);
                    }
                });

                // Showing Alert Message
                saveDialog.show().getWindow().setLayout(850,450);;
                return true;
            }
        });

    }

    private void deleteFilm(int position) {
        String message = String.format("deleting position %d", position);
        Log.i("ViewActivity", message);
        String movieKey = movieKeys.get(position);
        mDatabase.child(movieKey).removeValue();
    }

}
