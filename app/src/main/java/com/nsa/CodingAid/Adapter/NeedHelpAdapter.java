package com.nsa.CodingAid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nsa.CodingAid.Model.availableFieldModel;
import com.nsa.CodingAid.R;
import com.nsa.CodingAid.needHelpActivity;

import java.util.List;


public class NeedHelpAdapter extends RecyclerView.Adapter<NeedHelpAdapter.MyViewHolder> {

    Context context;
    List<availableFieldModel> list ;


    public NeedHelpAdapter(Context context, List<availableFieldModel> list){
        this.context=context;
        this.list=list;



    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v=LayoutInflater.from(context).inflate(R.layout.need_help_recycler_row,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {



        holder.fieldTxt.setText(list.get(position).getFieldName());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new needHelpActivity().searchField(list.get(position),context);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView fieldTxt;

        LinearLayout mainLayout;

        Animation translate_anim;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            fieldTxt=itemView.findViewById(R.id.fieldNametxt);


            mainLayout=itemView.findViewById(R.id.needhelpLayout);

            translate_anim= AnimationUtils.loadAnimation(context,R.anim.bottomtotop);
            mainLayout.setAnimation(translate_anim);
        }
    }
}
