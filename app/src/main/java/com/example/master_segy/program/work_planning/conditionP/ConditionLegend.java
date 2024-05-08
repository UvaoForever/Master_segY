package com.example.master_segy.program.work_planning.conditionP;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.master_segy.R;
import com.example.master_segy.data.AppDataBase;
import com.example.master_segy.data.pointP.Point;
import com.google.android.gms.location.LocationServices;

public class ConditionLegend extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.legend_layout, container, false);

        ImageView imageViewLegend = view.findViewById(R.id.imageViewLegend);
        TextView textGreen = view.findViewById(R.id.textViewLegendGreen);
        TextView textYellow = view.findViewById(R.id.textViewLegendYellow);
        TextView textOrange = view.findViewById(R.id.textViewLegendOrange);
        TextView textLigthRed = view.findViewById(R.id.textViewLegendLightRed);
        TextView textRed = view.findViewById(R.id.textViewLegendRed);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels / 2;
        int width = displayMetrics.widthPixels / 4;

        Bitmap legendBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int delimiter = legendBitmap.getHeight() / 5;

        for (int i = 0; i < legendBitmap.getWidth(); i++){
            for (int j = 0; j < delimiter; j++){
                legendBitmap.setPixel(i, j, Color.GREEN);
            }
        }

        for (int i = 0; i < legendBitmap.getWidth(); i++){
            for (int j = delimiter; j < delimiter * 2; j++){
                legendBitmap.setPixel(i, j, Color.YELLOW);
            }
        }

        for (int i = 0; i < legendBitmap.getWidth(); i++){
            for (int j = delimiter * 2; j < delimiter * 3; j++){
                legendBitmap.setPixel(i, j, ContextCompat.getColor(view.getContext(), R.color.orange));
            }
        }

        for (int i = 0; i < legendBitmap.getWidth(); i++){
            for (int j = delimiter * 3; j < delimiter * 4; j++){
                legendBitmap.setPixel(i, j, ContextCompat.getColor(view.getContext(), R.color.light_red));
            }
        }

        for (int i = 0; i < legendBitmap.getWidth(); i++){
            for (int j = delimiter * 4; j < delimiter * 5; j++){
                legendBitmap.setPixel(i, j, Color.RED);
            }
        }

        textGreen.setText(R.string.plate_green);
        textYellow.setText(R.string.plate_yellow);
        textOrange.setText(R.string.plate_orange);
        textLigthRed.setText(R.string.plate_light_red);
        textRed.setText(R.string.plate_red);

        imageViewLegend.setImageBitmap(legendBitmap);

        return view;
    }
}
