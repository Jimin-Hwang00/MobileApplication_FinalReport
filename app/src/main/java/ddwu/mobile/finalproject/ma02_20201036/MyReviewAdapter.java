package ddwu.mobile.finalproject.ma02_20201036;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import ddwu.mobile.finalproject.ma02_20201036.model.dto.Review;

public class MyReviewAdapter extends BaseAdapter {
    final static String TAG = "MyReviewAdapter";

    private Context context;                    // inflater 객체 생성 시 필요
    private int layout;                         // AdapterView 항목에 대한 layout
    private ArrayList<Review> myReviewList;         // 원본 데이터 리스트
    private LayoutInflater layoutInflater;      // inflater 객체

    public MyReviewAdapter(Context context, int layout, ArrayList<Review> myReviewList) {
        this.context = context;
        this.layout = layout;
        this.myReviewList = myReviewList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return myReviewList.size();
    }

    @Override
    public Object getItem(int i) {
        return myReviewList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position  = i;

        if (view == null) {
            view = layoutInflater.inflate(layout, viewGroup, false);
        }

        ImageView poster = view.findViewById(R.id.moviePoster);;
        TextView title = view.findViewById(R.id.movieTitle);
        TextView director = view.findViewById(R.id.movieDirector);
        TextView actor = view.findViewById(R.id.movieActor);

        if ((myReviewList.get(i).getImage()) != null) {
            File readFile = new File(myReviewList.get(i).getImage());
            if (readFile.exists()) {
                if (isExternalStorageWritable()) {
                    Glide.with(context)
                            .load(readFile)
                            .into(poster);
                }
            }
        }

        title.setText(myReviewList.get(i).getTitle());
        director.setText(myReviewList.get(i).getDirector());
        actor.setText(myReviewList.get(i).getActor());

        return view;
    }
}