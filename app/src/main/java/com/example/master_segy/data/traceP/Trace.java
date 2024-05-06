package com.example.master_segy.data.traceP;

import androidx.annotation.NonNull;
import androidx.room.TypeConverters;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


import com.example.master_segy.data.reportP.Report;

@Entity(tableName = "trace", foreignKeys = @ForeignKey(entity = Report.class, parentColumns = "_id", childColumns = "id_report", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
public class Trace {
    @PrimaryKey(autoGenerate = true)
    private int _id;
    @ColumnInfo(name = "id_report")
    private int _id_Report;
    @ColumnInfo(name = "samples_count")
    private int _samples_count;
    @ColumnInfo(name = "title_trace")
    @NonNull
    private String title_trace;
    private int _dt;
    private int _tailLength;
    @ColumnInfo(name = "FS", typeAffinity = ColumnInfo.REAL) // Частота сигнала
    private double _FS;
    public String getTitle_trace() {
        return title_trace;
    }
    public void setTitle_trace(String title_trace) {
        this.title_trace = title_trace;
    }
    @TypeConverters({ArrayTypeConverter.class})
    private double[] _signal;
    public Trace(int _samples_count, double[] _signal, String title_trace, int _id_Report, double FS, int _dt, int _tailLength) {
        this._samples_count = _samples_count;
        this._signal = _signal;
        this.title_trace = title_trace;
        this._id_Report = _id_Report;
        this._FS = FS;
        this._dt = _dt;
        this._tailLength = _tailLength;
    }
    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }
    public int get_id_Report() {
        return _id_Report;
    }
    public void set_id_Report(int _id_Report) {
        this._id_Report = _id_Report;
    }
    public int get_samples_count() {
        return _samples_count;
    }
    public void set_samples_count(int _simple_Count) {
        this._samples_count = _simple_Count;
    }
    public double[] get_signal() {
        return _signal;
    }
    public void set_signal(double[] _signal) {
        this._signal = _signal;
    }
    public int get_dt() {
        return _dt;
    }
    public void set_dt(int _dt) {
        this._dt = _dt;
    }
    public double get_FS() {
        return _FS;
    }
    public void set_FS(double _FS) {
        this._FS = _FS;
    }
    public int get_tailLength() {
        return _tailLength;
    }
    public void set_tailLength(int _tailLength) {
        this._tailLength = _tailLength;
    }
}
