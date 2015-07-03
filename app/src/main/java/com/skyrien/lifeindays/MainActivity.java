package com.skyrien.lifeindays;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
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

    final Handler myHandler = new Handler();
    final Calendar birthdayCalendar = Calendar.getInstance();
    TextView textViewDayCount;
    TextView textViewYears;
    EditText editTextBirthdayValue;
    TextView textViewIntro;
    TextView textViewYourBirthday;
    TextView textViewYouAre;
    TextView textViewYearsOld;
    TextView textViewTodayIs;
    TextView textViewInLife;
    NumberFormat yearFormatter = new DecimalFormat("##0.000000000");
    NumberFormat longYearFormatter = new DecimalFormat("##0.00000000");
    NumberFormat dayFormattter = new DecimalFormat("0");
    NumberFormat lifeExpectancyFormatter = new DecimalFormat("##0.##");
    DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM);

    //Input filters to limit user text input
    InputFilter[] filterArrayYears;
    InputFilter[] filterArrayBirthday;

    final double millisToDays = 1000 * 60 * 60 * 24;
    final double millisToYears = 1000 * 60 * 60 * 24 * 365.24;

    // These are static variables for our app
    static float lifeExpectancy = 0;
    static Date theBirthday = new Date();
    static boolean viewIsLifeCounter = true;
    Timer myTimer = new Timer();

    // A TextWatcher object which we'll use in the app
    TextWatcher myWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String inputString = String.valueOf(s);
            // This is the regex of this
            if (inputString.matches("[\\d]*[.]?[\\d]*")) {
                if (inputString.length() == 0)
                    lifeExpectancy = 0;
                else {
                    try {
                        lifeExpectancy = Float.valueOf(String.valueOf(s));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        editTextBirthdayValue.setText(String.valueOf(lifeExpectancy));
                    }
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            String inputString = String.valueOf(s);
            // This is the regex of this
            if (inputString.matches("[\\d]*[.]?[\\d]*")) {
                editTextBirthdayValue.removeTextChangedListener(this);
                if (s.length() == 0) {
                    lifeExpectancy = 0;
                    editTextBirthdayValue.setText("0");
                    editTextBirthdayValue.selectAll();
                }
                else {
                    try {
                        lifeExpectancy = Float.valueOf(String.valueOf(s));
                    } catch (Exception e) {
                        e.printStackTrace();
                        editTextBirthdayValue.setText(String.valueOf(lifeExpectancy));
                    }
                }

                editTextBirthdayValue.addTextChangedListener(this);
                saveLifeExpectancy(lifeExpectancy);
                //editTextBirthdayValue.removeTextChangedListener(this);

            }


        }
    };

    // This is the runnable loop that keeps updating the view.
    final Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            updateView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate() called in MainActivity");

        int birthYear, birthMonth, birthDay;

        textViewDayCount = (TextView) findViewById(R.id.textviewDayCount);
        textViewYears = (TextView) findViewById(R.id.textviewYears);
        editTextBirthdayValue = (EditText) findViewById(R.id.edittextBirthdayValue);

        // Creating the two input filters
        filterArrayYears = new InputFilter[1];
        filterArrayYears[0] = new InputFilter.LengthFilter(7);

        filterArrayBirthday = new InputFilter[1];
        filterArrayBirthday[0] = new InputFilter.LengthFilter(20);

        textViewYears.setEllipsize(TextUtils.TruncateAt.END);
        textViewYears.setSingleLine();

        // THIS SECTION SETS THE INITIAL VALUES ON THE CLASS
        // LOADS THEM FROM SharedPreferences IF THERE
        // ELSE SETS THEM TO CURRENT DATE
        SharedPreferences settings = getPreferences(0);
        birthYear = settings.getInt("year", birthdayCalendar.get(Calendar.YEAR));
        birthMonth = settings.getInt("month", birthdayCalendar.get(Calendar.MONTH));
        birthDay = settings.getInt("day", birthdayCalendar.get(Calendar.DAY_OF_MONTH));
        lifeExpectancy = settings.getFloat("lifeExpectancy", 85);

        // THIS LOADS THE PREVIOUS VIEW AND MAKES SURE ITS THE CURRENT ONE
        viewIsLifeCounter = settings.getInt("viewIsLifeCounter", 1) == 1;

        // This sets the birthday to midnight and 1 second of the birthday, based on current
        // timezone
        birthdayCalendar.set(birthYear, birthMonth, birthDay, 0, 0, 1);
        theBirthday = birthdayCalendar.getTime();

        // Now set the action bar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        setTextBasedOnMode(viewIsLifeCounter);

        // Next, start the runnable.

        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                i++;
                myHandler.post(myRunnable);
            }
        },0,50);
        }

    @Override
    public void onPause() {
        super.onPause();
        myTimer.cancel();
        myTimer.purge();
        //myHandler.removeCallbacks(run);
    }

    @Override
    public void onResume() {
        super.onResume();
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                i++;
                myHandler.post(myRunnable);
            }
        },0,50);
    }

    public void showModalDialog(View v) {
        Log.d(TAG, "showModalDialog() called in MainActivity");
        if (viewIsLifeCounter)
            showDatePickerDialog();
        else
            editTextBirthdayValue.selectAll();
    }

    public void showDatePickerDialog() {
        Log.d(TAG, "showDatePickerDialog() called in MainActivity");
        DialogFragment newFragment = new BirthdayPickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void updateView() {
        //Log.d(TAG, "UpdateView() called in MainActivity");

        // This means we're showing the counting up view
        if (viewIsLifeCounter) {
            long timeAlive, daysAlive;
            double yearsAlive;

            timeAlive = System.currentTimeMillis() - theBirthday.getTime();
            daysAlive = (long)(timeAlive / millisToDays);
            yearsAlive = timeAlive / (millisToYears);
            //Log.d(TAG, "yearsAlive is: " + String.valueOf(yearsAlive));

            textViewDayCount.setText(dayFormattter.format(daysAlive));
            if (yearsAlive < 100 && yearsAlive > -100)
                textViewYears.setText(yearFormatter.format(yearsAlive));
            else textViewYears.setText(longYearFormatter.format(yearsAlive));
        }

        // viewIsLifeCounter being false means we're in time left mode
        else {
            long timeLeft, daysLeft;
            double yearsLeft;

            // This adds the life expectancy onto the birthday, and subtracts
            // the current time to get time left.
            timeLeft = theBirthday.getTime()
                                    + (long)(lifeExpectancy * millisToYears)
                                    - System.currentTimeMillis();
            daysLeft = (long)(timeLeft / millisToDays);
            yearsLeft = (timeLeft / millisToYears);
            textViewDayCount.setText(dayFormattter.format(daysLeft));

            if (yearsLeft < 100 && yearsLeft > -100)
                textViewYears.setText(yearFormatter.format(yearsLeft));
            else textViewYears.setText(longYearFormatter.format(yearsLeft));


        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Let's hide the keyboard if it's still open


        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideSoftKeyboard(this);

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_birthday) {
            Log.d(TAG, "Clicked CHANGE BIRTHDAY");
            showDatePickerDialog();
            return true;
        }

        else if (id == R.id.action_toggleMode) {
            Log.d(TAG, "Clicked SWITCH MODE. Currently, LifeCounterMode: " + String.valueOf(viewIsLifeCounter));

            // Moving from LifeCounter to TimeLeft
            viewIsLifeCounter = !viewIsLifeCounter;
            updateModeAndSave(viewIsLifeCounter);
            if(!viewIsLifeCounter)
                editTextBirthdayValue.setCursorVisible(false);
            else editTextBirthdayValue.setCursorVisible(true);
            return true;
        }

        else if (id == R.id.action_about) {

            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public synchronized void updateBirthdayAndSave(int year, int month, int day) {

        Log.d(TAG, "updateBirthdayAndSave() called in MainActivity");
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("year", year);
        editor.putInt("month", month);
        editor.putInt("day", day);
        editor.commit();

        // Update the calendar object, and re-derive the Date object
        birthdayCalendar.set(year, month, day);
        theBirthday = birthdayCalendar.getTime();

        // Update UI text for Birthday
        if (viewIsLifeCounter) {
            editTextBirthdayValue.removeTextChangedListener(myWatcher);
            editTextBirthdayValue.setText(dateFormatter.format(theBirthday));
        }
    }

    public synchronized void updateModeAndSave(boolean newMode) {
        Log.d(TAG, "updateModeAndSave() called in MainActivity");
        setTextBasedOnMode(viewIsLifeCounter);
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();

        if (viewIsLifeCounter)
            editor.putInt("viewIsLifeCounter", 1);
        else
            editor.putInt("viewIsLifeCounter", 0);

        editor.commit();
    }

    public synchronized void saveLifeExpectancy(float newLifeExpectancy) {
        Log.d(TAG, "saveLifeExpectancy() called in MainActivity. Saving: " + String.valueOf(newLifeExpectancy));
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        //if newLifeExpectancy ==
        editor.putFloat("lifeExpectancy", newLifeExpectancy);
        editor.commit();
    }

    private void setTextBasedOnMode(boolean newMode) {
        // Update values
        textViewIntro = (TextView)findViewById(R.id.textviewIntro);
        textViewYourBirthday = (TextView)findViewById(R.id.textviewBirthday);
        textViewYouAre = (TextView)findViewById(R.id.textviewYouAre);
        textViewYearsOld = (TextView)findViewById(R.id.textviewYearsOld);
        textViewTodayIs = (TextView)findViewById(R.id.textviewTodayIs);
        textViewInLife = (TextView)findViewById(R.id.textviewInLife);
        editTextBirthdayValue = (EditText) findViewById(R.id.edittextBirthdayValue);


        // Moving from TimeLeft to LifeCounter
        if (newMode)
        {
            textViewIntro.setText(R.string.textviewIntro);
            textViewYourBirthday.setText(R.string.textviewYourBirthday);
            editTextBirthdayValue.setInputType(0);
            editTextBirthdayValue.removeTextChangedListener(myWatcher);
            editTextBirthdayValue.setFilters(filterArrayBirthday);
            editTextBirthdayValue.setText(dateFormatter.format(theBirthday));
            textViewYouAre.setText(R.string.textviewYouAre);
            textViewYearsOld.setText(R.string.textviewYearsOld);
            textViewTodayIs.setText(R.string.textviewTodayIs);
            textViewInLife.setText(R.string.textviewInLife);
        }
        // Moving from LifeCounter to TimeLeft
        else {

            textViewIntro.setText(R.string.textviewIntroModeTwo);
            textViewYourBirthday.setText(R.string.textviewYourBirthdayModeTwo);

            editTextBirthdayValue.setRawInputType(InputType.TYPE_CLASS_NUMBER |
                    InputType.TYPE_NUMBER_FLAG_SIGNED);
            editTextBirthdayValue.setFilters(filterArrayYears);
            editTextBirthdayValue.setText(lifeExpectancyFormatter.format(lifeExpectancy));
            textViewYouAre.setText(R.string.textviewYouAreModeTwo);
            textViewYearsOld.setText(R.string.textviewYearsOldModeTwo);
            textViewTodayIs.setText(R.string.textviewTodayIsModeTwo);
            textViewInLife.setText(R.string.textviewInLifeModeTwo);

            editTextBirthdayValue.addTextChangedListener(myWatcher);
            editTextBirthdayValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hide_keyboard(v);
                    }
                }
            });

        }
    }

    public void hide_keyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
