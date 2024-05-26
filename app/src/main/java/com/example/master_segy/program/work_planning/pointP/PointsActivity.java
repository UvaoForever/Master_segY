package com.example.master_segy.program.work_planning.pointP;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.master_segy.R;
import com.example.master_segy.data.AppDataBase;
import com.example.master_segy.data.plateP.Plate;
import com.example.master_segy.data.pointP.Point;
import com.example.master_segy.data.reportP.Report;
import com.example.master_segy.data.traceP.Trace;
import com.example.master_segy.program.CustomDividerItemDecoration;
import com.example.master_segy.program.DataReaderTraceFile_Segy;
import com.example.master_segy.program.work_planning.plateP.Add_EditPlate;
import com.example.master_segy.program.work_planning.reportP.ReportsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PointsActivity extends AppCompatActivity {
    public static final String PLATE_ID = "plateId";
    private static final int REQUEST_CODE_PICK_FOLDER = 123;
    Toolbar toolbar;
    private PointAdapter pointAdapter;

    AddSeveralFiles dialog;
    TextView share;
    private FloatingActionButton addPoint, addReports;
    RecyclerView recyclerView;
    AppDataBase db;
    Plate plate;
    Report report;
    ArrayList<Point> pointList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_points);
        toolbar = findViewById(R.id.toolbarPoints);
        setSupportActionBar(toolbar);
        db = AppDataBase.getInstance(getApplicationContext());
        plate = db.plateDao().getById(getIntent().getIntExtra(PLATE_ID,0));
        addPoint = findViewById(R.id.fabAddPoint);
        addReports = findViewById(R.id.fabAddReports);
        recyclerView = findViewById(R.id.recyclerViewPoint);
        // НАЖАТИЕ НА КООРДИНАТЫ,
        // Относится к ReportsActivity, где идёт работа с плитой, а не с точкой
        PointAdapter.OnPointClickListener stateClickListener = new PointAdapter.OnPointClickListener() {
            @Override
            public void onPointClick(Point point, int position) {
                Intent intent = new Intent(getBaseContext(), ReportsActivity.class);
                intent.putExtra(ReportsActivity.POINT_ID, point.get_id());
                startActivity(intent);
            }
        };
        pointAdapter = new PointAdapter(getBaseContext(), stateClickListener);
        loadPoints();
        initRecyclerView();
        // Создание координат
        addPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("g", "onClick: opening dialog.");
                Bundle bundle = new Bundle();
                bundle.putInt("ID_plate", plate.get_id());
                Add_EditPoint dialog = new Add_EditPoint();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "c");
            }
        });
        update();

        addReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFolderPicker();
            }
        });
    }

    private void showFolderPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_FOLDER);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            Log.v("TAG","Permission is granted");
            return true;
        }
    }
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        int dividerColor = ContextCompat.getColor(getApplicationContext(), R.color.table_border);
        recyclerView.addItemDecoration(new CustomDividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation(), dividerColor));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pointAdapter);
    }

    @Override
    protected void onResume() {
        loadPoints();
        super.onResume();
    }

    public void loadPoints() {
        pointAdapter.setPointList(db.pointDao().getAllPlate(plate.get_id()));
    }

    public void update(){
        getSupportActionBar().setTitle(plate.get_titlePlate());
        getSupportActionBar().setSubtitle(plate.get_descriptionPlate());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_point, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return false;
    }

    // Обработка результата выбора папки
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> errorFileList = new ArrayList<String>();
        ArrayList<String> updateReportList = new ArrayList<String>();
        ArrayList<String> noPointsList = new ArrayList<String>();
        pointList = new ArrayList<Point>(db.pointDao().getAllPlate(plate.get_id()));

        if (requestCode == REQUEST_CODE_PICK_FOLDER && resultCode == RESULT_OK) {
            // Получение URI выбранной папки
            Uri uri = data.getData();
            String path = uri.getPath();
            int index = path.indexOf(":");
            path = path.substring(index + 1);
            File folder = new File(path);
            File[] files = folder.listFiles();
            if (pointList.size() < folder.listFiles().length) {
                Toast.makeText(this, R.string.text_CountPoint, Toast.LENGTH_SHORT).show();
                return;
            }

            // X10_5Y10
            for (File file : files){
                String fileName =  file.getName();
                String extensionFile = null;
                String tmpX = null;
                String tmpY = null;
                double x;
                double y;
                int point_id = -1;
                if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
                    extensionFile = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (extensionFile != null && (extensionFile.equals("sgy") || extensionFile.equals("segy"))) {
                    // Получение координат из названия файла
                    index = fileName.indexOf("X");
                    if (index == -1){
                        errorFileList.add(fileName);
                        continue;
                    }
                    tmpX = fileName.substring(index + 1);
                    index = fileName.indexOf("Y");
                    if (index == -1){
                        errorFileList.add(fileName);
                        continue;
                    }
                    tmpY = tmpX.substring(index);
                    tmpX = tmpX.substring(0, index - 1);
                    index = tmpY.indexOf(".");
                    tmpY = tmpY.substring(0, index);
                    try {
                        x = stringToCoordinate(tmpX);
                        y = stringToCoordinate(tmpY);
                        for (int i = 0; i < pointList.size(); i++)
                            if (x == pointList.get(i).get_coordinate_X() && y == pointList.get(i).get_coordinate_Y())
                                point_id = pointList.get(i).get_id();
                        if (point_id >= 0){
                            String title_report = "X = " + x + " Y = "  + y;
                            if (db.reportDao().is_Exist(title_report, point_id)){
                                report = db.reportDao().getById(db.reportDao().getIdReportByTitleReport(title_report));
                                updateReportList.add(report.get_titleReport());
                                db.reportDao().delete(report);
                            }
                            report = new Report("X = " + x + " Y = " + y, point_id);
                        /*if (db.reportDao().is_Exist(report.get_titleReport(), point_id)){
                            updateReportList.add(report.get_titleReport());
                            db.reportDao().delete(report);
                        }*/

                            db.reportDao().insert(report);
                            //Trace trace = new Trace()
                            DataReaderTraceFile_Segy dataS = new DataReaderTraceFile_Segy(file);
                            dataS.Read();
                            int id = db.reportDao().getIdReportByTitleReport(title_report);
                            Trace trace = new Trace(dataS.getSamplesCount(), dataS.getTrace(0), fileName, db.reportDao().getIdReportByTitleReport(title_report), dataS.getFs(), dataS.getDt(), dataS.getTailLength());
                            //db.traceDao().deleteToReport(db.reportDao().getIdReportByTitleReport(title_report));
                            db.traceDao().insert(trace);
                            //db.reportDao().update(report);
                        }
                        else
                            noPointsList.add(fileName);
                    }
                    catch (IllegalArgumentException e){
                        errorFileList.add(fileName);

                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
            // ОКНО
            Bundle bundle = new Bundle();
            dialog = new AddSeveralFiles(errorFileList, updateReportList, noPointsList);
            dialog.setArguments(bundle);

            dialog.show(getSupportFragmentManager(), "c");
        }
    }

    private double stringToCoordinate(String str){
        int index = str.indexOf("_");
        if (index != -1){
            str = str.replace("_", ".");
        }
        return Double.parseDouble(str);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.buttonEdit){
            Bundle bundle = new Bundle();
            bundle.putSerializable("my_plate_key", plate);
            Add_EditPlate dialog = new Add_EditPlate();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(),"ewf");
            update();
        }
        else if(item.getItemId() == R.id.buttonDelete) {
            AlertDialog.Builder a_builder = new AlertDialog.Builder(PointsActivity.this);
            a_builder.setMessage(getString(R.string.questionDelete)).setCancelable(false).setPositiveButton(getString(R.string.answerYes), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.plateDao().delete(plate);
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
        else if(item.getItemId() == R.id.buttonAdd) {
            Log.d("g", "onClick: opening dialog.");
            Bundle bundle = new Bundle();
            bundle.putInt("ID_plate", plate.get_id());
            Add_ArrayPoints dialog = new Add_ArrayPoints();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "c");
            update();
        }
        else{
            onBackPressed();
        }
        return true;
    }
}
