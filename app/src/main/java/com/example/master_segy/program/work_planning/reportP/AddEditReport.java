package com.example.master_segy.program.work_planning.reportP;

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
import com.example.master_segy.data.reportP.Report;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditReport extends DialogFragment {

    private EditText editTextTitle;
    private TextInputLayout textInputLayoutTitle;
    private TextView mActionOk;
    Report report;
    private int ID_point;
    AppDataBase db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
    savedInstanceState) {
        View view = inflater.inflate(R.layout.add_edit_report, container, false);
        mActionOk = view.findViewById(R.id.buttonSaveReport);
        textInputLayoutTitle = view.findViewById(R.id.inputLayoutTitleReport);
        editTextTitle = view.findViewById(R.id.inputTitleReport);
        db  = AppDataBase.getInstance(getActivity().getApplicationContext());
        if (getArguments()!=null){
            ID_point = getArguments().getInt("ID_point");
            if(getArguments().getSerializable("my_point_key")!=null) {
                report = (Report) getArguments().get("my_point_key");
                editTextTitle.setText(report.get_titleReport());
            }
        }

        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()) {
                    if(report !=null){
                        saveReport(editTextTitle.getText().toString());
                    }
                    else {
                        saveNewReport(editTextTitle.getText().toString());
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
    private void saveNewReport(String titleReport) {
        report = new Report(titleReport, ID_point);
        if (db.reportDao().is_Exist(report.get_titleReport(), ID_point))
            return;
        db.reportDao().insert(report);
    }
    private void saveReport(String titleReport) {
        report.set_titleReport(titleReport);
        db.reportDao().update(report);
    }
    @Override
    public void onDismiss(DialogInterface dialog) {
        Activity activity = getActivity();
        if(activity != null && activity instanceof ReportsActivity){
            ((ReportsActivity) activity).loadReports();
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
        return flag;
    }
}