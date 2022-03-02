package com.ariel.tremp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DateDialog extends DialogFragment {

    private boolean clicked = false;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private GetTime getTime;

    private int year, day, month;
    private int hours, minutes;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.date_dialog, null);

        datePicker = view.findViewById(R.id.date);
        timePicker = view.findViewById(R.id.time);
        Button button = view.findViewById(R.id.btnAdd);
        button.setOnClickListener(this::addTime);

        builder.setTitle("Date Picker")
                .setView(view);

        return builder.create();
    }

    private void addTime(View view) {
        if (clicked) {
            hours = timePicker.getHour();
            minutes = timePicker.getMinute();

            Time time = new Time(year, month, day, hours, minutes);
            getTime.get(time);
            dismiss();
        } else {
            datePicker.setVisibility(View.GONE);
            timePicker.setVisibility(View.VISIBLE);

            year = datePicker.getYear();
            month = datePicker.getMonth() + 1;
            day = datePicker.getDayOfMonth();

            clicked = true;
        }
    }

    public interface GetTime {
        void get(Time time);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getTime = (GetTime) context;
    }
}
