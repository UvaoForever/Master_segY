package com.example.master_segy.program.work_planning.objectLocationP;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.master_segy.R;
import com.example.master_segy.data.AppDataBase;
import com.example.master_segy.data.objectLocationP.ObjectLocation;

import com.example.master_segy.program.work_planning.plateP.PlatesActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Add_EditObjectLocation extends DialogFragment {

    private EditText editTextTitle, editTextAddress, editTextDate;
    private TextInputLayout textInputLayoutTitle, textInputLayoutAddress, textInputLayoutDate;
    private TextView mActionOk;
    private Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    ObjectLocation objectLocation;
    AppDataBase db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_edit_object_location, container, false);
        mActionOk = view.findViewById(R.id.buttonSave);
        textInputLayoutTitle = view.findViewById(R.id.inputLayoutTitle);
        editTextTitle = view.findViewById(R.id.inputTitle);
        textInputLayoutAddress = view.findViewById(R.id.inputLayoutAddress);
        editTextAddress = view.findViewById(R.id.inputAddress);
        textInputLayoutDate = view.findViewById(R.id.inputLayoutDate);
        editTextDate = view.findViewById(R.id.inputDate);
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        db  = AppDataBase.getInstance(getActivity().getApplicationContext());
        calendar=Calendar.getInstance();
        if(getArguments()!=null){
            objectLocation = (ObjectLocation) getArguments().get("my_object_key");
            editTextTitle.setText(objectLocation.get_titleObject());
            editTextAddress.setText(objectLocation.get_address());
            editTextDate.setText(objectLocation.get_datePlaneWork());
            try {
                calendar.setTime(simpleDateFormat.parse(objectLocation.get_datePlaneWork()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateDate();
            }

        };
      editTextDate.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
          }
      });
        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()) {
                if(objectLocation!=null){
                    saveObjectLocation(editTextTitle.getText().toString(), editTextAddress.getText().toString(), editTextDate.getText().toString());
                }
              else {
                    saveNewObjectLocation(editTextTitle.getText().toString(), editTextAddress.getText().toString(), editTextDate.getText().toString());
                    Log.i("test", "add");

                }
                    getDialog().dismiss();
                }

                else{
                    Toast.makeText(getContext(), getString(R.string.fieldError), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
    private void saveNewObjectLocation(String titleObjL, String addressObjL, String dateObjL) {
        try {
            objectLocation = new ObjectLocation(titleObjL, dateObjL, addressObjL);
            db.objectDao().insert(objectLocation);
        } catch (SQLiteConstraintException e) {
            // Обработка исключения при нарушении уникальности
            Log.e("Room", "Exception: " + e.getMessage());
        }
    }
    private void saveObjectLocation(String titleObjL, String addressObjL, String dateObjL) {
        objectLocation.set_titleObject(titleObjL);
        objectLocation.set_address(addressObjL);
        objectLocation.set_datePlaneWork(dateObjL);
        db.objectDao().update(objectLocation);
    }
    private void updateDate(){
       editTextDate.setText(simpleDateFormat.format(calendar.getTime()));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null && parentFragment instanceof WorkPlanningFragment) {
            ((WorkPlanningFragment) parentFragment).loadObjects();
        }
        Activity activity = getActivity();
        if(activity != null && activity instanceof PlatesActivity){
            ((PlatesActivity) activity).update();
        }
        super.onDismiss(dialog);
    }
    private boolean isValid(){
        boolean flag = true;
        if (editTextTitle.getText().toString().isEmpty()) {
            textInputLayoutTitle.setError(getString(R.string.titleError));
            flag = false;
        } else {
            textInputLayoutTitle.setErrorEnabled(false);
        }
        if (editTextDate.getText().toString().isEmpty()) {
            textInputLayoutDate.setError(getString(R.string.dateError));
            flag = false;
        } else {
            textInputLayoutDate.setErrorEnabled(false);
        }
        return flag;
    }
}