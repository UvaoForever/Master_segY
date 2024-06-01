package com.example.master_segy.program.work_planning.plateP;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.master_segy.BuildConfig;
import com.example.master_segy.R;
import com.example.master_segy.data.AppDataBase;
import com.example.master_segy.data.objectLocationP.ObjectLocation;
import com.example.master_segy.data.plateP.Plate;
import com.example.master_segy.data.reportP.Report;
import com.example.master_segy.data.traceP.Trace;
import com.example.master_segy.program.CustomDividerItemDecoration;
import com.example.master_segy.program.work_planning.objectLocationP.Add_EditObjectLocation;
import com.example.master_segy.program.work_planning.pointP.Add_ArrayPoints;
import com.example.master_segy.program.work_planning.pointP.PointsActivity;
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

public class PlatesActivity extends AppCompatActivity {
    public static final String OBJECT_ID = "objectId";
    Toolbar toolbar;
    Plate plate;
    private PlateAdapter plateAdapter;
    private FloatingActionButton addPlate;
    RecyclerView recyclerView;
    AppDataBase db;
    ObjectLocation objectLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_plates);
        toolbar = findViewById(R.id.toolbarPlates);
        setSupportActionBar(toolbar);
        db = AppDataBase.getInstance(getApplicationContext());
        objectLocation = db.objectDao().getById(getIntent().getIntExtra(OBJECT_ID,0));
        addPlate = findViewById(R.id.fabAddPlate);
        recyclerView = findViewById(R.id.recyclerViewPlate);
        PlateAdapter.OnPlateClickListener stateClickListener = new PlateAdapter.OnPlateClickListener() {
            @Override
            public void onPlateClick(Plate plate, int position) {
                Intent intent = new Intent(getBaseContext(), PointsActivity.class/*ReportsActivity.class*/);
                intent.putExtra(PointsActivity.PLATE_ID, plate.get_id()/*ReportsActivity.PLATE_ID, plate.get_id()*/);
                startActivity(intent);
            }
        };
        plateAdapter = new PlateAdapter(getBaseContext(), stateClickListener);
        loadPlates();
        initRecyclerView();
        addPlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("g", "onClick: opening dialog.");
                Bundle bundle = new Bundle();
                bundle.putInt("ID_object", objectLocation.get_id());
                Add_EditPlate dialog = new Add_EditPlate();
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
        recyclerView.setAdapter(plateAdapter);
    }

    @Override
    protected void onResume() {
        loadPlates();
        super.onResume();
    }

    public void loadPlates() {
        plateAdapter.setPlateList(db.plateDao().getAllObject(objectLocation.get_id()));
    }

    public void update(){
        getSupportActionBar().setTitle(objectLocation.get_titleObject());
        getSupportActionBar().setSubtitle(objectLocation.get_address());
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
            bundle.putSerializable("my_object_key", objectLocation);
            Add_EditObjectLocation dialog = new Add_EditObjectLocation();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(),"ewf");
            update();
        }
        else if(item.getItemId() == R.id.buttonDelete) {
            AlertDialog.Builder a_builder = new AlertDialog.Builder(PlatesActivity.this);
            a_builder.setMessage(getString(R.string.questionDelete)).setCancelable(false).setPositiveButton(getString(R.string.answerYes), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.objectDao().delete(objectLocation);
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