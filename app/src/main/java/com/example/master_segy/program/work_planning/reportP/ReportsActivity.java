package com.example.master_segy.program.work_planning.reportP;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.master_segy.R;
import com.example.master_segy.data.AppDataBase;
import com.example.master_segy.data.pointP.Point;
import com.example.master_segy.program.work_planning.reportP.AddEditReport;
import com.example.master_segy.data.reportP.Report;
import com.example.master_segy.program.CustomDividerItemDecoration;
import com.example.master_segy.program.work_planning.pointP.Add_EditPoint;
import com.example.master_segy.program.work_planning.traceP.ReportTraceActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReportsActivity extends AppCompatActivity {
    public static final String POINT_ID = "point_ID";
    Toolbar toolbar;
    private ReportAdapter reportAdapter;
    private FloatingActionButton addReport;
    RecyclerView recyclerView;
    //TextView textViewX, textViewY, textViewAmp;
    AppDataBase db;
    Point point;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        toolbar = findViewById(R.id.toolbarReports);
        setSupportActionBar(toolbar);
        db = AppDataBase.getInstance(getApplicationContext());
        point = db.pointDao().getById(getIntent().getIntExtra(POINT_ID,0));
        addReport = findViewById(R.id.fabAddReport);
        recyclerView = findViewById(R.id.recyclerViewReport);
        /*textViewX = findViewById(R.id.textViewCoordinateX);
        textViewY = findViewById(R.id.textViewCoordinateY);
        textViewAmp = findViewById(R.id.textViewCoordinateAmp);*/
        ReportAdapter.OnReportClickListener stateClickListener = new ReportAdapter.OnReportClickListener() {
            @Override
            public void onReportClick(Report report, int position) {
                Intent intent = new Intent(getBaseContext(), ReportTraceActivity.class);
                intent.putExtra(ReportTraceActivity.REPORT_ID, report.get_id());
                startActivity(intent);
            }
        };
        // создаем адаптер
        reportAdapter = new ReportAdapter(getBaseContext(), stateClickListener);
        loadReports();
        initRecyclerView();
        addReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("g", "onClick: opening dialog.");
                Bundle bundle = new Bundle();
                bundle.putInt("ID_point", point.get_id());
                AddEditReport dialog = new AddEditReport();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "c");
            }
        });
        update();
    }
    protected void onResume() {
        loadReports();
        super.onResume();
    }
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        int dividerColor = ContextCompat.getColor(getApplicationContext(), R.color.table_border);
        recyclerView.addItemDecoration(new CustomDividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation(), dividerColor));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(reportAdapter);
    }

    public void loadReports() {
        reportAdapter.setReportList(db.reportDao().getAllPoint(point.get_id()));
    }

    public void update(){
        getSupportActionBar().setTitle("X = " + String.valueOf(point.get_coordinate_X()) + " Y = " + String.valueOf(point.get_coordinate_Y()));
        /*textViewX.setText(getString(R.string.coordinate_x) +" "+ point.get_latitude());
        textViewY.setText( getString(R.string.coordinate_y) +" "+ point.get_longitude());
        textViewAmp.setText(getString(R.string.altitude) +" "+ point.get_altitude());*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_object, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.buttonEdit){
            Bundle bundle = new Bundle();
            bundle.putSerializable("my_point_key", point);
            Add_EditPoint dialog = new Add_EditPoint();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(),"ewf");
            update();
        }
        else if(item.getItemId() == R.id.buttonDelete) {
            AlertDialog.Builder a_builder = new AlertDialog.Builder(ReportsActivity.this);
            a_builder.setMessage(getString(R.string.questionDelete)).setCancelable(false).setPositiveButton(getString(R.string.answerYes), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    db.pointDao().delete(point);
                    Toast.makeText(getBaseContext(), getString(R.string.resultDelete), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }).setNegativeButton(getString(R.string.answerNo),new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getBaseContext(), getString(R.string.resultNo) +" "+getString(R.string.resultDelete), Toast.LENGTH_SHORT).show();
                }
            } );
            AlertDialog alertDialog = a_builder.create();
            alertDialog.show();
        }
        else{
            onBackPressed();
        }
        return true;
    }


}