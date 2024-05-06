package com.example.master_segy.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;



import com.example.master_segy.data.objectLocationP.IObjectLocation;
import com.example.master_segy.data.objectLocationP.ObjectLocation;
import com.example.master_segy.data.plateP.Plate;
import com.example.master_segy.data.plateP.IPlate;
import com.example.master_segy.data.pointP.IPoint;
import com.example.master_segy.data.pointP.Point;
import com.example.master_segy.data.reportP.IReport;
import com.example.master_segy.data.reportP.Report;
import com.example.master_segy.data.traceP.ITrace;
import com.example.master_segy.data.traceP.Trace;

@Database(entities = {ObjectLocation.class, Plate.class, Point.class, Report.class, Trace.class}, version = 1, exportSchema = true)
public abstract class AppDataBase extends RoomDatabase {
    public abstract IObjectLocation objectDao();
    public abstract IPlate plateDao();
    public abstract IPoint pointDao();
    public abstract IReport reportDao();
    public abstract ITrace traceDao();
    private static AppDataBase INSTANCE;
    public static AppDataBase getInstance(Context context){

        if(INSTANCE==null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, "PlaningWorkDB").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        }
        return INSTANCE;
    }
}
