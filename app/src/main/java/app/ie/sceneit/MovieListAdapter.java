package app.ie.sceneit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import app.ie.sceneit.R;

public class MovieListAdapter extends ArrayAdapter<Movie> {

    int vg;
    ArrayList<Movie> list;
    Context context;


    public MovieListAdapter(Context context, int vg, ArrayList<Movie> list) {
        super(context, vg, list);
        this.context = context;
        this.vg = vg;
        this.list = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(vg, parent, false);

        TextView txtTitle = (TextView) itemView.findViewById(R.id.title);
        TextView txtRelease = (TextView) itemView.findViewById(R.id.release);
        TextView txtOverview = (TextView) itemView.findViewById(R.id.overview);
        ImageView imgFilm = (ImageView)  itemView.findViewById(R.id.filmIcon);

        Movie movie = getItem(position);

        try {
            txtTitle.setText(movie.title);
            txtRelease.setText(movie.releaseDate);
            txtOverview.setText(movie.overview);

            if (movie.poster != null) {
                imgFilm.setImageBitmap(movie.poster);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemView;

    }
}