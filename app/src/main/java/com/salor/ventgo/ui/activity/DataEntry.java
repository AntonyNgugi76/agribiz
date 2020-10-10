package com.salor.ventgo.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.salor.ventgo.R;
import com.wang.avi.AVLoadingIndicatorView;

public class DataEntry extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{
    private AVLoadingIndicatorView avi;

    String[] courses = { "Nairobi", "Kisumu", "Nyeri", "Busia", "Meru", "Kericho", "Narok", "Bomet" };
    Spinner counties;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_entry);
        String indicator=getIntent().getStringExtra("indicator");
        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.setIndicator(indicator);
        Button buttonsbt = (Button) findViewById(R.id.buttonsubt);
        buttonsbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avi.setVisibility(View.VISIBLE);
            }
        });


        counties = findViewById(R.id.county);
        counties.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,courses);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       counties.setAdapter(aa);
    }
    void startAnim(){
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim(){
        avi.hide();
        // or avi.smoothToHide();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}