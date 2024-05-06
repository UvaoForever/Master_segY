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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.master_segy.BuildConfig;
import com.example.master_segy.R;
import com.example.master_segy.data.AppDataBase;
import com.example.master_segy.data.plateP.Plate;
import com.example.master_segy.data.pointP.Point;
import com.example.master_segy.data.reportP.Report;
import com.example.master_segy.data.traceP.Trace;
import com.example.master_segy.program.CustomDividerItemDecoration;
import com.example.master_segy.program.work_planning.plateP.Add_EditPlate;
import com.example.master_segy.program.work_planning.reportP.ReportsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PointsActivity extends AppCompatActivity {
    public static final String PLATE_ID = "plateId";
    Toolbar toolbar;
    private PointAdapter pointAdapter;
    TextView share;
    private FloatingActionButton addPoint;
    RecyclerView recyclerView;
    AppDataBase db;
    Plate plate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_points);
        toolbar = findViewById(R.id.toolbarPoints);
        setSupportActionBar(toolbar);
        db = AppDataBase.getInstance(getApplicationContext());
        plate = db.plateDao().getById(getIntent().getIntExtra(PLATE_ID,0));
        addPoint = findViewById(R.id.fabAddPoint);
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
        pointAdapter.setPointList(db.pointDao().getAllObject(plate.get_id()));
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
