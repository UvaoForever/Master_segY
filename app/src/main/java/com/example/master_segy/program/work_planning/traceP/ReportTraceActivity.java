package com.example.master_segy.program.work_planning.traceP;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.master_segy.program.DataReaderTraceFile_Segy;
import com.example.master_segy.program.GraphFragment;
import com.example.master_segy.R;
import com.example.master_segy.data.AppDataBase;

import com.example.master_segy.data.reportP.Report;
import com.example.master_segy.data.traceP.Trace;
import com.example.master_segy.program.CustomDividerItemDecoration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class ReportTraceActivity extends AppCompatActivity {
    Toolbar toolbar;
    public static final String REPORT_ID = "report_ID";
    RecyclerView recyclerView;
    Button addFile;
    private TraceAdapter traceAdapter;
    AppDataBase db;
    Report report;
    GraphFragment fragment;
    TextView viewFiles;
    private ArrayList<Trace> trails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_trace);
        fragment = (GraphFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentGraphTrace);
        toolbar = findViewById(R.id.toolbarReportTrace);
        setSupportActionBar(toolbar);
        trails = new ArrayList<>();
        addFile = findViewById(R.id.buttonAddFile);
        recyclerView = findViewById(R.id.recyclerViewTrace);
        db = AppDataBase.getInstance(getApplicationContext());
        viewFiles = findViewById(R.id.textViewFiles);
        viewFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isShown = recyclerView.getVisibility() == View.VISIBLE;
                recyclerView.setVisibility(isShown ? View.GONE : View.VISIBLE);
            }
        });
        report = db.reportDao().getById(getIntent().getIntExtra(REPORT_ID,0));
        getSupportActionBar().setTitle(report.get_titleReport());
        if(db.traceDao().getAllReport(report.get_id()).size()!=0){
            for(Trace trace : db.traceDao().getAllReport(report.get_id())){
                trails.add(trace);
            }
            fragment.setMarkerOne(report.get_markerOne());
            fragment.setMarkerTwo(report.get_markerTwo());
            fragment.setSpeed(report.get_speedWave());
            viewGraph();

        }

        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                chooseFile.setType("*/*");
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(chooseFile, "Choose a file"), 1);
            }
        });
        TraceAdapter.OnFileClickListener clickListener = new TraceAdapter.OnFileClickListener() {
            @Override
            public void onFileClick(Trace trace, int position) {
                trails.remove(trace);
                loadFile();
                viewGraph();
            }

        };
        traceAdapter = new TraceAdapter(getBaseContext(), clickListener);
        loadFile();
        initRecyclerView();
    }

    // Вызов диалогового окна для выбора папки

    private void readFile(Uri returnUri) {

        String fileName = getFileName(returnUri);
        String extensionFile = null;
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            extensionFile = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (extensionFile != null && (extensionFile.equals("sgy") || extensionFile.equals("segy"))) {
            DataReaderTraceFile_Segy dataS = null;
            File source = null;

            try (InputStream inputStream = getContentResolver().openInputStream(returnUri)) {

                source = File.createTempFile("tmp", ".sgy");
                Files.copy(inputStream, source.toPath(), StandardCopyOption.REPLACE_EXISTING);
                dataS = new DataReaderTraceFile_Segy(source);
                dataS.Read();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < dataS.getChannelCount(); i++) {
                Trace trace = new Trace(dataS.getSamplesCount(), dataS.getTrace(i), getString(R.string.trace)+" " + (i + 1) + "_" + getFileName(returnUri), report.get_id(), dataS.getFs(), dataS.getDt(), dataS.getTailLength());
                if (trails.stream()
                        .filter(test -> ((test.getTitle_trace().equals(trace.getTitle_trace())))).count() == 0) {
                    trails.add(trace);
                }
            }
            Log.i("inf", String.valueOf(dataS.getChannelCount()));
            Log.i("inf", String.valueOf(dataS.getSamplesCount()));
            source.deleteOnExit();

        }
        else {
            Toast.makeText(getBaseContext(), R.string.errorExtension, Toast.LENGTH_SHORT).show();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK){return;}
        else {
            ClipData clipData = data.getClipData(); // Если выбрано несколько файлов, то они будут в ClipData
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    readFile(uri);
                }
            }
            else {
                Uri returnUri = data.getData();
                readFile(returnUri);
            }
            loadFile();
            viewGraph();
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return false;
    }
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        int dividerColor = ContextCompat.getColor(getApplicationContext(), R.color.table_border);
        recyclerView.addItemDecoration(new CustomDividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation(), dividerColor));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(traceAdapter);
    }
    public void loadFile(){
        traceAdapter.setTraceList(trails);
    }
    public String getFileName(Uri uri) {
        String result = null;
        String[] projection = {MediaStore.Files.FileColumns.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
            }
        } catch (IllegalArgumentException e){
            Log.d("Error", e.getMessage());
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
    private void save(){
        db.traceDao().deleteToReport(report.get_id());
        for(Trace trace: trails) {
            db.traceDao().insert(trace);
        }
        report.set_countTrace(trails.size());
        report.set_markerOne(fragment.getMarkerOne());
        report.set_markerTwo(fragment.getMarkerTwo());
        report.set_speedWave(fragment.getSpeed());
        report.set_lengthPile(fragment.getLength());
        db.reportDao().update(report);}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.buttonSaveReportTrace){
            save();
            Toast.makeText(getBaseContext(), getString((R.string.resultSave)), Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId() == R.id.buttonDelete) {
            AlertDialog.Builder a_builder = new AlertDialog.Builder(ReportTraceActivity.this);
            a_builder.setMessage(getString(R.string.questionDelete)).setCancelable(false).setPositiveButton(getString(R.string.answerYes), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   db.reportDao().delete(report);
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
            AlertDialog.Builder a_builder = new AlertDialog.Builder(ReportTraceActivity.this);
            a_builder.setMessage(getString(R.string.questionSave)).setCancelable(false).setPositiveButton(getString(R.string.answerYes), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  save();
                    Toast.makeText(getBaseContext(), getString(R.string.resultSave), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }).setNegativeButton(getString(R.string.answerNo),new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getBaseContext(),getString(R.string.resultNo) +" "+getString(R.string.resultSave), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            } );
            AlertDialog alertDialog = a_builder.create();
            alertDialog.show();

        }
        return true;
    }
    void viewGraph(){
        if (fragment != null)
            fragment.setTrails(trails);
    }
}