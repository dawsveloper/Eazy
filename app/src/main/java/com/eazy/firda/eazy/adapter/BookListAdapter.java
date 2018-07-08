package com.eazy.firda.eazy.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.eazy.firda.eazy.R;
import com.eazy.firda.eazy.application.EazyApplication;
import com.eazy.firda.eazy.models.Book;

import java.util.List;

/**
 * Created by firda on 3/1/2018.
 */

public class BookListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Book> books;
    public String book_id;
    ImageLoader imageLoader = EazyApplication.getInstance().getImageLoader();

    public BookListAdapter(Activity activity, List<Book> books){
        this.activity = activity;
        this.books = books;
    }

    @Override
    public int getCount() {return books.size();}

    @Override
    public Object getItem(int position) {return books.get(position);}

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(inflater == null){
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null){
            //convertView = inflater.inflate(R.layout.book_row_list, null);
            convertView = inflater.inflate(R.layout.book_row_list_v2, null);
        }
        if(imageLoader == null){
            imageLoader = EazyApplication.getInstance().getImageLoader();
        }

//        NetworkImageView book_img = (NetworkImageView)convertView.findViewById(R.id.book_img);
        TextView book_name = (TextView)convertView.findViewById(R.id.book_name);
        TextView book_status = (TextView)convertView.findViewById(R.id.book_status);
        TextView book_location = (TextView)convertView.findViewById(R.id.book_location);
        TextView book_date = (TextView)convertView.findViewById(R.id.book_date);


        Book b = books.get(position);

        //book_img.setImageUrl(b.getCarImage(), imageLoader);
        book_name.setText(b.getName());
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
                && ((Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP))) {
            // your code here - is between 15-21
            if(b.getStatus().equals("CONFIRMED")){book_status.setBackgroundColor(Color.parseColor("#43A047"));}
            else if(b.getStatus().equals("PENDING")){book_status.setBackgroundColor(Color.parseColor("#FB8C00"));}
            else if(b.getStatus().equals("REJECTED")){book_status.setBackgroundColor(Color.parseColor("#F44336"));}
            else if(b.getStatus().equals("COMPLETED")){book_status.setBackgroundColor(Color.parseColor("#2196F3"));}
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // your code here - is api 21
            if(b.getStatus().equals("CONFIRMED")){book_status.setBackgroundTintList
                    (ColorStateList.valueOf(Color.parseColor("#43A047")));}
            else if(b.getStatus().equals("PENDING")){book_status.setBackgroundTintList
                    (ColorStateList.valueOf(Color.parseColor("#FB8C00")));}
            else if(b.getStatus().equals("REJECTED")){book_status.setBackgroundTintList
                    (ColorStateList.valueOf(Color.parseColor("#F44336")));}
            else if(b.getStatus().equals("COMPLETED")){book_status.setBackgroundTintList
                    (ColorStateList.valueOf(Color.parseColor("#2196F3")));}
        }
        book_status.setText(b.getStatus());
        book_location.setText(b.getLocation());
        book_date.setText(b.getDate());

        book_id = b.getId();

        return convertView;
    }
}
