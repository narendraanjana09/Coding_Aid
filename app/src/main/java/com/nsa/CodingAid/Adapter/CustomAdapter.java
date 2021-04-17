package com.nsa.CodingAid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.nsa.CodingAid.Model.FieldsModel;
import com.nsa.CodingAid.R;
import com.nsa.CodingAid.helperActivity;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    List<FieldsModel> list ;


    com.nsa.CodingAid.helperActivity helperActivity;




    public CustomAdapter(Context context,List<FieldsModel> list){
        this.context=context;
        this.list=list;
        this.helperActivity=(helperActivity) context;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v=LayoutInflater.from(context).inflate(R.layout.fields_recycler_row,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final FieldsModel field = list.get(position);

        holder.fieldTxt.setText(field.getName());
        holder.checkBox.setChecked(field.isSelected());
        holder.checkBox.setTag(list.get(position));

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.get(position).isSelected()){
                    holder.checkBox.setChecked(false);
                }else{
                    holder.checkBox.setChecked(true);
                }
                holder.checkBox.callOnClick();
            }
        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FieldsModel fieldsModel = (FieldsModel) holder.checkBox.getTag();

                fieldsModel.setSelected(holder.checkBox.isChecked());

                list.get(position).setSelected(holder.checkBox.isChecked());

                if(list.get(position).isSelected()){
                    helperActivity.selectedList.add(list.get(position).getName());
                    helperActivity.counter++;
                }else{
                    helperActivity.counter--;
                    helperActivity.selectedList.remove(list.get(position).getName());
                }

                helperActivity.updateCounter(helperActivity.counter);


            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView fieldTxt;
        CheckBox checkBox;
        ConstraintLayout mainLayout;

        Animation translate_anim;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);



            fieldTxt=itemView.findViewById(R.id.fieldNametxt);
            checkBox=itemView.findViewById(R.id.checkbox);

            mainLayout=itemView.findViewById(R.id.mainLayout);

            translate_anim= AnimationUtils.loadAnimation(context,R.anim.bottomtotop);
            mainLayout.setAnimation(translate_anim);
        }
    }
}
