package com.skyrien.lifeindays;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
//import java.util.logging.Handler;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    int i = 0;
    int birthYear, birthMonth, birthDay;
    final Handler myHandler = new Handler();
    final Calendar birthdayCalendar = Calendar.getInstance();
    TextView textviewDayCount;
    TextView textviewYears;
    NumberFormat yearFormatter = new DecimalFormat("##0.000000000");
    NumberFormat dayFormattter = new DecimalFormat("0");

    final double millisToDays = (1000 * 60 * 60 * 24);
    final double millisToYears = 1000 * 60 * 60 * 24 * 365.24;

    Date theBirthday = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate() called in MainActivity");

        textviewDayCount = (TextView) findViewById(R.id.textviewDayCount);
        textviewYears = (TextView) findViewById(R.id.textviewYears);

        // get initial view from saved prefs... if there.
        SharedPreferences settings = getPreferences(0);
        birthYear = settings.getInt("year", 0);
        birthMonth = settings.getInt("month", 0);
        birthDay = settings.getInt("day", 0);

        // Not set yet, set it to the current time
        if ((birthYear + birthMonth + birthDay) == 0) {
            birthYear = birthdayCalendar.get(Calendar.YEAR);
            birthMonth = birthdayCalendar.get(Calendar.MONTH);
            birthDay = birthdayCalendar.get(Calendar.DAY_OF_MONTH);
        }
        birthdayCalendar.set(birthYear, birthMonth, birthDay);
        theBirthday = birthdayCalendar.getTime();

        // Next, start the runnable.
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                i++;
                myHandler.post(myRunnable);
            }
        },0,50);
        }

    public void showDatePickerDialog(View v) {
        Log.d(TAG, "showDatePickerDialog() called in MainActivity");
        DialogFragment newFragment = new BirthdayPickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void updateView() {
        //Log.d(TAG, "UpdateView() called in MainActivity");

        long timeAlive, daysAlive;
        double yearsAlive;

        timeAlive = System.currentTimeMillis() - theBirthday.getTime();
        daysAlive = (long)(timeAlive / millisToDays);
        yearsAlive = timeAlive / (millisToYears);
        //Log.d(TAG, "yearsAlive is: " + String.valueOf(yearsAlive));

        textviewDayCount.setText(dayFormattter.format(daysAlive));
        textviewYears.setText(yearFormatter.format(yearsAlive));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_birthday) {
            //showDatePickerDialog();

            return true;
        }

        else if (id == R.id.action_about) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    final Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            updateView();
        }
    };

    public synchronized void updateBirthdayAndSave(int year, int month, int day) {

        Log.d(TAG, "saveDateInPreferences() called in MainActivity");
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("year", year);
        editor.putInt("month", month);
        editor.putInt("day", day);
        editor.commit();

        birthYear = year;
        birthMonth = month;
        birthDay = day;
        birthdayCalendar.set(year, month, day);
        theBirthday = birthdayCalendar.getTime();
    }
}
