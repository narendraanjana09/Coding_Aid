package com.nsa.CodingAid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.nsa.CodingAid.Adapter.SlidePagerAdapter;
import com.nsa.CodingAid.Model.SlideModel;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
       private ViewPager viewPager;
       SlidePagerAdapter slidePagerAdapter;
    private List<SlideModel> list;
    TabLayout tabLayout;
    Button previous_button,next_button,skip_button;
    String prevStarted = "first_time";
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean(prevStarted, false)){
            nextActivity();
        }
        previous_button=findViewById(R.id.button_previous);
        next_button=findViewById(R.id.button_next);
        skip_button=findViewById(R.id.button_skip);

        list=new ArrayList<>();
        list.add(new SlideModel("Coding Aid Intro",R.drawable.scrennshot_intro));
        list.add(new SlideModel("Start With\nSign-In!😊",R.drawable.screenshot_signin));
        list.add(new SlideModel("Choose Your\nPath!\n1.Need Help.   2.Helper",R.drawable.screenshot_welocme));
        list.add(new SlideModel("1.Select a Field in\nwhich You Have Doubt!",R.drawable.screenshot_need_help));
        list.add(new SlideModel("Connect To a Helper!",R.drawable.screenshot_call));
        list.add(new SlideModel("2.If You Want to be a Helper!\nSelect Your Top Fields!",R.drawable.screenshot_helper));
        list.add(new SlideModel("Select a platform\nhere!\nAnd Upload",R.drawable.screenshot_platform));
        list.add(new SlideModel("Connect To Students!",R.drawable.screenshot_main));
        list.add(new SlideModel("Connected!☺",R.drawable.screenshot_call));

        viewPager=findViewById(R.id.view_pager);
        slidePagerAdapter=new SlidePagerAdapter(IntroActivity.this,list);
        viewPager.setAdapter(slidePagerAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch(position){

                    case 0:previous_button.setVisibility(View.INVISIBLE);
                    skip_button.setVisibility(View.VISIBLE);
                    break;
                    case 8:next_button.setText("Sign-In");
                        skip_button.setVisibility(View.INVISIBLE);
                    break;
                    default:previous_button.setVisibility(View.VISIBLE);
                        skip_button.setVisibility(View.VISIBLE);
                        next_button.setText("Next");
                        break;
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    public void nextSlide(View view) {
        int a=tabLayout.getSelectedTabPosition();
        if(a==8){
            nextActivity();
        }else{
        viewPager.setCurrentItem(a+1);
    }}
   public void nextActivity(){
       editPreferences();
       Intent intent =new Intent(IntroActivity.this,SignInActivity.class);
       startActivity(intent);
       finish();
   }

    private void editPreferences() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(prevStarted, Boolean.TRUE);
        editor.apply();
    }

    public void skipSlides(View view) {
      nextActivity();
    }

    public void previousSlide(View view) {
        viewPager.setCurrentItem(tabLayout.getSelectedTabPosition()-1);
    }
}