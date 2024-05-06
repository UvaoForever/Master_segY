package com.example.master_segy.program;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.master_segy.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.master_segy.data.traceP.Trace;
import com.example.master_segy.databinding.ActivityPrimaryProcessingBinding;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class PrimaryProcessingActivity extends AppCompatActivity {

    private ArrayList<Trace> trails;
    private ActivityPrimaryProcessingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrimaryProcessingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_primaryprocessing, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.buttonFile) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(chooseFile, "Choose a file"), 1);
        }
        else{
            onBackPressed();
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        } else {
            Uri returnUri = data.getData();
            trails = new ArrayList<>();
            String fileName = getFileName(returnUri);
            String extensionFile = null;
            if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
                extensionFile = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (extensionFile != null && (extensionFile.equals("sgy") || extensionFile.equals("segy"))) {
                DataReaderTraceFile_Segy dataS = null;
                File source = null;
                try (InputStream inputStream = getContentResolver().openInputStream(returnUri)) {
                    source = File.createTempFile("tmp", ".sgy");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Files.copy(inputStream, source.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    dataS = new DataReaderTraceFile_Segy(source);
                    dataS.Read();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                for (int i = 0; i < dataS.getChannelCount(); i++) {
                    Trace trace = new Trace(dataS.getSamplesCount(), dataS.getTrace(i), getString(R.string.trace) + (i + 1), 0, dataS.getFs(), dataS.getDt(), dataS.getTailLength());
                    trails.add(trace);
                }
                Log.i("inf", String.valueOf(dataS.getChannelCount()));
                Log.i("inf", String.valueOf(dataS.getSamplesCount()));
                source.deleteOnExit();
                GraphFragment fragment = (GraphFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentGraph);
                if (fragment != null)
                    fragment.setTrails(trails);
            }
            else {
                Toast.makeText(getBaseContext(), R.string.errorExtension, Toast.LENGTH_SHORT).show();
            }
        }
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
}