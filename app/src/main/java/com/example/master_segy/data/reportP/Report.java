package com.example.master_segy.data.reportP;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.master_segy.data.pointP.Point;

@Entity(tableName = "report", foreignKeys = @ForeignKey(entity = Point.class, parentColumns = "_id", childColumns = "id_point", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
public class Report {
    @PrimaryKey(autoGenerate = true)
    private int _id;
    @ColumnInfo(name = "title_report")
    @NonNull
    private String _titleReport;
    @ColumnInfo(name = "id_point")
    private int _id_Point;
    @ColumnInfo(name = "count_trace")
    private int _countTrace;
    @ColumnInfo(name = "marker_one")
    private double _markerOne;
    @ColumnInfo(name = "marker_two")
    private double _markerTwo;
    @ColumnInfo(name = "speed_wave")
    private double _speedWave;
    @ColumnInfo(name = "length_pile")
    private double _lengthPile;
    public double get_markerOne() {
        return _markerOne;
    }
    public void set_markerOne(double _markerOne) {
        this._markerOne = _markerOne;
    }
    public double get_markerTwo() {
        return _markerTwo;
    }
    public void set_markerTwo(double _markerTwo) {
        this._markerTwo = _markerTwo;
    }
    public double get_speedWave() {
        return _speedWave;
    }
    public void set_speedWave(double _speedWave) {
        this._speedWave = _speedWave;
    }
    public double get_lengthPile() {
        return _lengthPile;
    }
    public void set_lengthPile(double _lengthPile) {
        this._lengthPile = _lengthPile;
    }
    public int get_countTrace() {
        return _countTrace;
    }
    public void set_countTrace(int _countTrace) {
        this._countTrace = _countTrace;
    }
    public Report(String _titleReport, int _id_Point) {
        this._titleReport = _titleReport;
        this._id_Point = _id_Point;
    }
    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }
    public String get_titleReport() {
        return _titleReport;
    }
    public void set_titleReport(String _titleReport) {
        this._titleReport = _titleReport;
    }
    public int get_id_Point() {
        return _id_Point;
    }
    public void set_id_Point(int _id_Point) {
        this._id_Point = _id_Point;
    }
}
