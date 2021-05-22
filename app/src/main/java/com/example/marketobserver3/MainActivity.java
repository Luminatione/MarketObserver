package com.example.marketobserver3;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startObserving();
            }
        });
       initializeFieldsFormIntent();
    }
    private void initializeFieldsFormIntent()
    {
        ((EditText)findViewById(R.id.referenceSystemField)).setText(getIntent().getStringExtra(FieldPassNames.REFERENCE_SYSTEM_PASS_NAME));
        ((EditText)findViewById(R.id.maximalDistanceField)).setText(getIntent().getStringExtra(FieldPassNames.MAXIMAL_DISTANCE_PASS_NAME));
        ((EditText)findViewById(R.id.minimalDemandField)).setText(getIntent().getStringExtra(FieldPassNames.MINIMAL_DEMAND_PASS_NAME));
        ((CheckBox)findViewById(R.id.mPadAllowCheckBox)).setChecked(getIntent().getBooleanExtra(FieldPassNames.ALLOW_MSIZED_PADS_PASS_NAME, false));
    }

    protected void startObserving()
    {
        Intent observingService = new Intent(this, ObservingService.class);
        observingService.putExtra(FieldPassNames.REFERENCE_SYSTEM_PASS_NAME, getReferenceSystem());
        observingService.putExtra(FieldPassNames.MAXIMAL_DISTANCE_PASS_NAME, getMaximalDistance());
        observingService.putExtra(FieldPassNames.MINIMAL_DEMAND_PASS_NAME, getMinimalDemand());
        observingService.putExtra(FieldPassNames.ALLOW_MSIZED_PADS_PASS_NAME, getAllowMPads());
        ContextCompat.startForegroundService(this, observingService);
    }
    private String getReferenceSystem()
    {
        String referenceSystem = ((EditText)findViewById(R.id.referenceSystemField)).getText().toString();
        return referenceSystem.isEmpty() ? "Borann" : referenceSystem;
    }
    private String getMaximalDistance()
    {
        String maximalDistance = ((EditText)findViewById(R.id.maximalDistanceField)).getText().toString();
        return maximalDistance.isEmpty() ? "99999" : maximalDistance;
    }
    private String getMinimalDemand()
    {
        String minimalDemand = ((EditText)findViewById(R.id.minimalDemandField)).getText().toString();
        return minimalDemand.isEmpty() ? "0" : minimalDemand;
    }
    private boolean getAllowMPads()
    {
        return ((CheckBox)findViewById(R.id.mPadAllowCheckBox)).isChecked();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
            messageBox.setMessage(Html.fromHtml("Author: CMDR Luminatione<br>Icon make by <a href=\"https://www.flaticon.com/authors/freepik\">Freepik</a> from <a href=\"www.flaticon.com\">www.flaticon.com</a>"));
            messageBox.setTitle("Information");
            AlertDialog Alert1 = messageBox.create();
            Alert1.show();
            ((TextView)Alert1.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
