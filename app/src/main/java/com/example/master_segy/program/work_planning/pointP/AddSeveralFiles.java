package com.example.master_segy.program.work_planning.pointP;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.master_segy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class AddSeveralFiles extends DialogFragment {

    ArrayList<String> errorFileList;
    ArrayList<String> updateReportList;
    ArrayList<String> noPointsList;

    private TextView btnOK;

    public AddSeveralFiles(ArrayList<String> _errorFileList, ArrayList<String> _updateReportList, ArrayList<String> _noPointsList) {
        errorFileList = _errorFileList;
        updateReportList = _updateReportList;
        noPointsList = _noPointsList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_reports, container, false);
        TextView textView = view.findViewById(R.id.textViewErrorText);
        btnOK = view.findViewById(R.id.buttonOK);

        if (errorFileList.size() != 0)
            for (String str : errorFileList) {
                textView.setText(textView.getText() + str + "\n");
            }
        else
            textView.setText(R.string.text_NoFiles);

        textView = view.findViewById(R.id.textViewReportText);
        if (updateReportList.size() != 0)
            for (String str : updateReportList) {
                textView.setText(textView.getText() + str + "\n");
            }
        else
            textView.setText(R.string.text_NoFiles);

        textView = view.findViewById(R.id.textViewNoPointsText);
        if (noPointsList.size() != 0)
            for (String str : noPointsList) {
                textView.setText(textView.getText() + str + "\n");
            }
        else
            textView.setText(R.string.text_NoFiles);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }
}
