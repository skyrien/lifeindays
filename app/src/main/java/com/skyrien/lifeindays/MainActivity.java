package com.skyrien.lifeindays;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    Date theBirthday = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate() called in MainActivity");

        // This is to update the view
        updateView();

    }

    public void showDatePickerDialog(View v) {
        Log.d(TAG, "showDatePickerDialog() called in MainActivity");
        DialogFragment newFragment = new BirthdayPickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void updateView() {
        Log.d(TAG, "UpdateView() called in MainActivity");
        int birthYear, birthMonth, birthDay;
        long currentTime, timeAlive, daysAlive;
        double yearsAlive;

        TextView textviewDayCount = (TextView) findViewById(R.id.textviewDayCount);
        TextView textviewYears = (TextView) findViewById(R.id.textviewYears);

        SharedPreferences settings = getPreferences(0);
        birthYear = settings.getInt("year", 0);
        birthMonth = settings.getInt("month", 0);
        birthDay = settings.getInt("day", 0);

        final Calendar c = Calendar.getInstance();
        c.set(birthYear, birthMonth, birthDay);
        theBirthday = c.getTime();

        currentTime = System.currentTimeMillis();
        Log.i(TAG, "Current time is " + String.valueOf(currentTime));
        //today.setTime(currentTime);

        timeAlive = currentTime - theBirthday.getTime();
        daysAlive = timeAlive / (1000 * 60 * 60 * 24);
        yearsAlive = (double) timeAlive / (1000 * 60 * 60 * 24 * 365.24);

        textviewDayCount.setText(String.valueOf(daysAlive));
        textviewYears.setText(String.valueOf(yearsAlive));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
