package com.example.master_segy.program.work_planning.plateP;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import com.example.master_segy.R;
import com.example.master_segy.data.AppDataBase;
import com.example.master_segy.data.plateP.Plate;
import com.example.master_segy.program.work_planning.reportP.ReportsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;

import android.location.Location;

public class Add_EditPlate extends DialogFragment {

    private EditText editTextTitle, editTextDescription;
    private TextInputLayout textInputLayoutTitle, textInputLayoutDescription;
    private TextView mActionOk;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private FusedLocationProviderClient fusedLocationClient;

    Plate plate;
    private int ID_object;
    AppDataBase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_edit_plate, container, false);
        mActionOk = view.findViewById(R.id.buttonSavePlate);
        textInputLayoutTitle = view.findViewById(R.id.inputLayoutTitlePlate);
        editTextTitle = view.findViewById(R.id.inputTitlePlate);
        editTextDescription = view.findViewById(R.id.inputDescription);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        db = AppDataBase.getInstance(getActivity().getApplicationContext());
        if (getArguments() != null) {
            ID_object = getArguments().getInt("ID_object");
            if (getArguments().getSerializable("my_plate_key") != null) {
                plate = (Plate) getArguments().get("my_plate_key");
                editTextTitle.setText(plate.get_titlePlate());
                editTextDescription.setText(String.valueOf(plate.get_descriptionPlate()));
            }
        }

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    if (plate != null) {
                        savePlate(editTextTitle.getText().toString(), editTextDescription.getText().toString());
                    } else {
                        saveNewPlate(editTextTitle.getText().toString(), editTextDescription.getText().toString());
                        Log.i("test", "add");

                    }
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getContext(), getString(R.string.fieldError), Toast.LENGTH_SHORT).show();
                }
            }

        });

        return view;
    }

    private void saveNewPlate(String titlePlate, String descriptionPlate) {
        plate = new Plate(titlePlate, descriptionPlate, ID_object);
        db.plateDao().insert(plate);
    }
    private void savePlate(String titlePlate, String descriptionPlate) {
        plate.set_titlePlate(titlePlate);
        plate.set_descriptionPlate(descriptionPlate);
        db.plateDao().update(plate);
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity activity = getActivity();
        if(activity != null && activity instanceof ReportsActivity){
            ((ReportsActivity) activity).update();
        }
        if(activity != null && activity instanceof PlatesActivity){
            ((PlatesActivity) activity).loadPlates();
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
        if (editTextDescription.getText().toString().isEmpty()) {
            textInputLayoutDescription.setError(getString(R.string.descriptionError));
            flag = false;
        }
        return flag;
    }
}