package com.scholarscraper;

import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import java.util.List;
import com.scholarscraper.model.Course;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.app.DialogFragment;
import android.view.View;
import com.scholarscraper.alarm.AlarmSetter;
import android.content.Context;
import android.widget.NumberPicker;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SettingsFragment
    extends DialogFragment
{
    private SharedPreferences sharedPreferences;

    public static final String PREFERENCES = "preferences";
    public static final String OFFSET = "offset";
    public static final String CANCEL_NIGHT_ALARMS = "cancel_night_alarms";

    public static final int DEFAULT_OFFSET = 12;

    /* low and high cutoff times for alarms, used if CANCEL_NIGHT_ALARMS is set,
     * field is in 24 hour time, i.e. "22" is 10 pm.
     */
    public static final int LOW_CUTOFF = 22;
    public static final int HIGH_CUTOFF = 10;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = getActivity();
        sharedPreferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        getDialog().setTitle("Settings");
        NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        initNumberPicker(numberPicker);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
        initCheckBox(checkBox);
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        List<Course> courses = ((MainActivity) context).getCourseList();
        AlarmSetter.setNextAlarm(context, courses);
    }


    private void initNumberPicker(NumberPicker numberPicker) {
        numberPicker.setMaxValue(48);
        numberPicker.setMinValue(0);
        int offset =  sharedPreferences.getInt(OFFSET, DEFAULT_OFFSET);
        numberPicker.setValue(offset);
        numberPicker.setOnValueChangedListener( new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                sharedPreferences.edit().putInt(OFFSET, newVal).commit();
            }
        });
    }

    private void initCheckBox(CheckBox checkBox) {
        boolean state = sharedPreferences.getBoolean(CANCEL_NIGHT_ALARMS, true);
        checkBox.setChecked(state);
        checkBox.setOnClickListener(new OnClickListener() {
            public void onClick(View checkBox)
            {
                sharedPreferences.edit()
                                 .putBoolean(CANCEL_NIGHT_ALARMS, ((CheckBox) checkBox).isChecked())
                                 .commit();
            }
        });
    }
}
