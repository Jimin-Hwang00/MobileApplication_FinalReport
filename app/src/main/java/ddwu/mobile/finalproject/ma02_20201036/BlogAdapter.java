package ddwu.mobile.finalproject.ma02_20201036;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma02_20201036.model.json.Blog;

public class BlogAdapter extends BaseAdapter {
    private Context context;                    // inflater 객체 생성 시 필요
    private int layout;                         // AdapterView 항목에 대한 layout
    private ArrayList<Blog> blogList;         // 원본 데이터 리스트
    private LayoutInflater layoutInflater;      // inflater 객체

    static final String TAG = "MovieAdapter";

    public BlogAdapter(Context context, int layout, ArrayList<Blog> blogList) {
        this.context = context;
        this.layout = layout;
        this.blogList = blogList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return blogList.size();
    }

    @Override
    public Object getItem(int i) {
        return blogList.get(i);
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

        TextView blogItemTitle = view.findViewById(R.id.blogItemTitle);
        TextView blogItemBloggerName = view.findViewById(R.id.blogItemBloggerName);

        blogItemTitle.setText(blogList.get(i).getTitle());
        blogItemBloggerName.setText(blogList.get(i).getBloggername());

        return view;
    }
}
