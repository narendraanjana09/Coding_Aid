package com.nsa.CodingAid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.nsa.CodingAid.Model.SlideModel;
import com.nsa.CodingAid.R;

import java.util.List;

public class SlidePagerAdapter extends PagerAdapter {
    Context context;
    List<SlideModel> list;

    public SlidePagerAdapter(Context context, List<SlideModel> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutscreen=inflater.inflate(R.layout.slide_layout,null);
        ImageView imageView=layoutscreen.findViewById(R.id.slideImageView);
        TextView textView=layoutscreen.findViewById(R.id.slideTextView);
        textView.setText(list.get(position).getText());


//        if (position==0){
//            ConstraintLayout mainLayout=layoutscreen.findViewById(R.id.mainLayout);
//            mainLayout.setBackgroundResource(list.get(position).getImageResource());
//        }else{
            imageView.setImageResource(list.get(position).getImageResource());
    //    }
        container.addView(layoutscreen);
        return  layoutscreen;

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
