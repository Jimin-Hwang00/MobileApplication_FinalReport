package ddwu.mobile.finalproject.ma02_20201036;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma02_20201036.model.json.Movie;

public class MovieAdapter extends BaseAdapter {
    private Context context;                    // inflater 객체 생성 시 필요
    private int layout;                         // AdapterView 항목에 대한 layout
    private ArrayList<Movie> movieList;         // 원본 데이터 리스트
    private LayoutInflater layoutInflater;      // inflater 객체

    static final String TAG = "MovieAdapter";

    public MovieAdapter(Context context, int layout, ArrayList<Movie> movieList) {
        this.context = context;
        this.layout = layout;
        this.movieList = movieList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int i) {
        return movieList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position  = i;

        if (view == null) {
            view = layoutInflater.inflate(layout, viewGroup, false);
        }

        ImageView moviePoster = view.findViewById(R.id.moviePoster);;
        TextView movieTitle = view.findViewById(R.id.movieTitle);
        TextView movieDirector = view.findViewById(R.id.movieDirector);
        TextView movieActor = view.findViewById(R.id.movieActor);

        Glide.with(context)
                .load(movieList.get(i).getImage())
                .into(moviePoster);

        movieTitle.setText(movieList.get(i).getTitle());
        movieDirector.setText(movieList.get(i).getDirector());
        movieActor.setText(movieList.get(i).getActor());

        return view;
    }
}
