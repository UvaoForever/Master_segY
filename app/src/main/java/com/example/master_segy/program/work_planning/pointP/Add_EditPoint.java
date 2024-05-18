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
import com.example.master_segy.data.objectLocationP.ObjectLocation;
import com.example.master_segy.data.plateP.Plate;
import com.example.master_segy.data.pointP.Point;
import com.example.master_segy.program.work_planning.plateP.PlatesActivity;
import com.example.master_segy.program.work_planning.reportP.ReportsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

public class Add_EditPoint extends DialogFragment {
    private EditText editTextCoordinateX, editTextCoordinateY;
    private TextInputLayout textInputLayoutCoordinateX, textInputLayoutCoordinateY;
    private TextView mActionOk;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private FusedLocationProviderClient fusedLocationClient;

    Point point;
    ArrayList<Point> pointList;
    TreeSet<Double> arrayX = new TreeSet<Double>();
    TreeSet<Double> arrayY = new TreeSet<Double>();
    private int ID_plate;
    AppDataBase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_edit_point, container, false);
        mActionOk = view.findViewById(R.id.buttonSavePoint);
        textInputLayoutCoordinateX = view.findViewById(R.id.inputLayoutCoordinateX);
        editTextCoordinateX = view.findViewById(R.id.inputCoordinateX);
        textInputLayoutCoordinateY = view.findViewById(R.id.inputLayoutCoordinateY);
        editTextCoordinateY = view.findViewById(R.id.inputCoordinateY);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        db = AppDataBase.getInstance(getActivity().getApplicationContext());
        if (getArguments() != null) {
            ID_plate = getArguments().getInt("ID_plate");
            if (getArguments().getSerializable("my_point_key") != null) {
                point = (Point) getArguments().get("my_point_key");
                editTextCoordinateX.setText(String.valueOf(point.get_coordinate_X()));
                editTextCoordinateY.setText(String.valueOf(point.get_coordinate_Y()));
            }
        }

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    if (point != null) {
                        savePoint(Double.valueOf(editTextCoordinateX.getText().toString()), Double.valueOf(editTextCoordinateY.getText().toString()));
                    } else {
                        saveNewPoint(Double.valueOf(editTextCoordinateX.getText().toString()), Double.valueOf(editTextCoordinateY.getText().toString()));
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

    private double max_x(){
        double max = Double.MIN_VALUE;
        for (int i = 0; i < pointList.size(); i++)
            if (max < pointList.get(i).get_coordinate_X())
                max = pointList.get(i).get_coordinate_X();

        return max;
    }

    private double max_y(){
        double max = Double.MIN_VALUE;
        for (int i = 0; i < pointList.size(); i++)
            if (max < pointList.get(i).get_coordinate_Y())
                max = pointList.get(i).get_coordinate_Y();

        return max;
    }

    private void creatingArrays(){
        arrayX.clear();
        arrayY.clear();

        for (int i = 0; i < pointList.size(); i++) {
            arrayX.add(pointList.get(i).get_coordinate_X());
            arrayY.add(pointList.get(i).get_coordinate_Y());
        }
    }

    private void saveNewPoint(double coordinateX, double coordinateY) {
        pointList = new ArrayList<Point>(db.pointDao().getAllObject(ID_plate));
        // Если в базе данных есть хотя бы одна точка
        if (pointList != null && pointList.size() != 0){
            // Если точка уже существует, то не добавляем
            if (db.pointDao().is_Exist(coordinateX, coordinateY, ID_plate))
                return;
            // Если не существует
            point = new Point(coordinateX, coordinateY, ID_plate);
            pointList.add(point);
            Collections.sort(pointList, Comparator.comparing(Point::get_coordinate_X).thenComparing(Point::get_coordinate_Y)); // Сортировка по X и Y
            creatingArrays();

            double max_x = max_x() > coordinateX ? coordinateX : max_x();
            double max_y = max_y() > coordinateY ? coordinateY : max_y();
            for (double x : arrayX){
                for (double y : arrayY){
                    if (!db.pointDao().is_Exist(x, y, ID_plate)){
                        point = new Point(x, y, ID_plate);
                        db.pointDao().insert(point);
                    }
                }
            }
            return;
        }
        // Если в базе совсем нет точек
        point = new Point(coordinateX, coordinateY, ID_plate);
        db.pointDao().insert(point);
    }
    private void savePoint(double coordinateX, double coordinateY) {
        point.set_coordinate_X(coordinateX);
        point.set_coordinate_Y(coordinateY);
        db.pointDao().update(point);
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
        if (editTextCoordinateX.getText().toString().isEmpty()) {
            textInputLayoutCoordinateX.setError(getString(R.string.titleError));
            flag = false;
        } else {
            textInputLayoutCoordinateX.setErrorEnabled(false);
        }
        if (editTextCoordinateY.getText().toString().isEmpty()) {
            textInputLayoutCoordinateY.setError(getString(R.string.descriptionError));
            flag = false;
        }
        return flag;
    }
}
