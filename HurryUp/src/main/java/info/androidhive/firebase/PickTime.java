package info.androidhive.firebase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by wenjun on 11/28/2016.
 */
public class PickTime extends DialogFragment {
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    public PickTime() {
        // Required empty public constructor
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(),R.style.datepicker, onTimeSetListener,hour,min,true);

    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof DatePickerDialog.OnDateSetListener) {
            onTimeSetListener = (TimePickerDialog.OnTimeSetListener) activity;
        }
    }


}

