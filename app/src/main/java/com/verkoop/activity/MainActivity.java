package com.verkoop.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.verkoop.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private String []nameList={"name","secondName"};
    private static String USERID="userId";
    private ArrayList<String> newList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for(int i=0;i<nameList.length;i++){

        }
    }
}
