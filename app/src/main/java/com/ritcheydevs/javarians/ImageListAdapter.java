package com.ritcheydevs.javarians;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by Solanto on 10/08/2017.
 */

public class ImageListAdapter extends ArrayAdapter<String> {
    private Activity context;
    public ArrayList<String> userList;
    public ArrayList<String> imgList;
    private ConnectionHelper imgConnectionHelper;

    public ImageListAdapter(Activity context) {

        super(context, R.layout.list_item);
        // TODO Auto-generated constructor stub
        this.userList = new ArrayList<>();
        this.imgList = new ArrayList<>();
        this.context = context;
        imgConnectionHelper = ConnectionHelper.getInstance(context.getApplicationContext());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.list_item, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.username);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/trebuc.ttf");
        txtTitle.setTypeface(typeface);
        txtTitle.setText(userList.get(position));

        CircularNetworkImageView imageView = (CircularNetworkImageView) rowView.findViewById(R.id.avatar);
        imageView.setImageUrl(imgList.get(position), imgConnectionHelper.getImageLoader());




        return rowView;

    }

}