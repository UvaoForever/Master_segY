package com.example.master_segy.program.work_planning.pointP;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.master_segy.R;
import com.example.master_segy.data.AppDataBase;
import com.example.master_segy.data.pointP.Point;
import com.example.master_segy.program.work_planning.reportP.ReportsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputLayout;

public class Add_ArrayPoints extends DialogFragment{
    private EditText editTextStartX, editTextEndX, editTextStartY, editTextEndY, editTextStepX, editTextStepY;
    private TextInputLayout textInputLayoutStartX, textInputLayoutStartY, textInputLayoutEndX, textInputLayoutEndY, textInputLayoutStepX, textInputLayoutStepY;
    private TextView mActionOk;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private FusedLocationProviderClient fusedLocationClient;

    Point point;
    private int ID_plate;
    AppDataBase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_array_points, container, false);
        mActionOk = view.findViewById(R.id.buttonSavePoint);
        textInputLayoutStartX = view.findViewById(R.id.inputLayoutStartX);
        editTextStartX = view.findViewById(R.id.inputStartX);
        textInputLayoutEndX = view.findViewById(R.id.inputLayoutEndX);
        editTextEndX = view.findViewById(R.id.inputEndX);
        textInputLayoutStartY = view.findViewById(R.id.inputLayoutStartY);
        editTextStartY = view.findViewById(R.id.inputStartY);
        textInputLayoutEndY = view.findViewById(R.id.inputLayoutEndY);
        editTextEndY = view.findViewById(R.id.inputEndY);
        textInputLayoutStepX = view.findViewById(R.id.inputLayoutStepX);
        editTextStepX = view.findViewById(R.id.inputStepX);
        textInputLayoutStepY = view.findViewById(R.id.inputLayoutStepY);
        editTextStepY = view.findViewById(R.id.inputStepY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        db = AppDataBase.getInstance(getActivity().getApplicationContext());
        if (getArguments() != null) {
            ID_plate = getArguments().getInt("ID_plate");
            if (getArguments().getSerializable("my_point_key") != null) {
                point = (Point) getArguments().get("my_point_key");
            }
        }

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {

                    double startX = Double.valueOf(editTextStartX.getText().toString());
                    double endX = Double.valueOf(editTextEndX.getText().toString());
                    double startY = Double.valueOf(editTextStartY.getText().toString());
                    double endY = Double.valueOf(editTextEndY.getText().toString());
                    double stepX = Double.valueOf(editTextStepX.getText().toString());
                    double stepY = Double.valueOf(editTextStepY.getText().toString());

                    for (double x = startX; x <= endX; x += stepX)
                        for (double y = startY; y <= endY; y += stepY){
                            saveNewPoint(x, y);
                            getDialog().dismiss();
                        }
                } else {
                    Toast.makeText(getContext(), getString(R.string.fieldError), Toast.LENGTH_SHORT).show();
                }
            }

        });

        return view;
    }

    private void saveNewPoint(double coordinateX, double coordinateY) {
        point = new Point(coordinateX, coordinateY, ID_plate);
        db.pointDao().insert(point);
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity activity = getActivity();
        if(activity != null && activity instanceof ReportsActivity){
            ((ReportsActivity) activity).update();
        }
        if(activity != null && activity instanceof PointsActivity){
            ((PointsActivity) activity).loadPoints();
        }
        super.onDismiss(dialog);
    }
    private boolean isValid(){
        boolean flag = true;
        if (editTextStartX.getText().toString().isEmpty()) {
            textInputLayoutStartX.setError(getString(R.string.xError));
            flag = false;
        } else {
            textInputLayoutStartX.setErrorEnabled(false);
        }

        if (editTextEndX.getText().toString().isEmpty()) {
            textInputLayoutEndX.setError(getString(R.string.xError));
            flag = false;
        } else {
            textInputLayoutEndX.setErrorEnabled(false);
        }

        if (editTextStartY.getText().toString().isEmpty()) {
            textInputLayoutStartY.setError(getString(R.string.yError));
            flag = false;
        } else {
            textInputLayoutStartY.setErrorEnabled(false);
        }

        if (editTextEndY.getText().toString().isEmpty()) {
            textInputLayoutEndY.setError(getString(R.string.yError));
            flag = false;
        } else {
            textInputLayoutEndY.setErrorEnabled(false);
        }

        if (editTextStepX.getText().toString().isEmpty()) {
            textInputLayoutStepX.setError(getString(R.string.stepX));
            flag = false;
        } else {
            textInputLayoutStepX.setErrorEnabled(false);
        }

        if (editTextStepY.getText().toString().isEmpty()) {
            textInputLayoutStepY.setError(getString(R.string.stepY));
            flag = false;
        } else {
            textInputLayoutStepY.setErrorEnabled(false);
        }
        return flag;
    }
}
