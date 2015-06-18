package com.skyrien.lifeindays;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Alexander on 6/11/2015.
 */
public class BirthdayPickerFragment extends DialogFragment
                        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //default value
        final Calendar c = Calendar.getInstance();
        SharedPreferences settings = getActivity().getPreferences(0);
        int birthYear = settings.getInt("year", 0);
        int birthMonth = settings.getInt("month", 0);
        int birthDay = settings.getInt("day", 0);

        // Not set yet
        if ((birthYear + birthMonth + birthDay) == 0) {
            birthYear = c.get(Calendar.YEAR);
            birthMonth = c.get(Calendar.MONTH);
            birthDay = c.get(Calendar.DAY_OF_MONTH);
        }


        return new DatePickerDialog(getActivity(), this, birthYear, birthMonth, birthDay);

    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        SharedPreferences settings = getActivity().getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("year", year);
        editor.putInt("month", month);
        editor.putInt("day", day);
        editor.commit();
        ((MainActivity)getActivity()).updateView();
    }

}
