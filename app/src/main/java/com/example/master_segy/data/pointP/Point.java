package com.example.master_segy.data.pointP;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.master_segy.data.objectLocationP.ObjectLocation;
import com.example.master_segy.data.plateP.Plate;

import java.io.Serializable;

@Entity(tableName = "point", foreignKeys = @ForeignKey(entity = Plate.class, parentColumns = "_id", childColumns = "id_Plate", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
public class Point implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @Nullable
    private int _id;
    @ColumnInfo(name = "coordinate_X")
    @NonNull
    private double _coordinate_X;
    @ColumnInfo(name = "coordinate_Y")
    private double _coordinate_Y;
    @ColumnInfo(name = "id_Plate")
    private int _id_Plate;

    public Point(double _coordinate_X, double _coordinate_Y, int _id_Plate) {
        this._coordinate_X = _coordinate_X;
        this._coordinate_Y = _coordinate_Y;
        this._id_Plate = _id_Plate;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public double get_coordinate_X() {
        return _coordinate_X;
    }

    public void set_coordinate_X(double _coordinate_X) {
        this._coordinate_X = _coordinate_X;
    }

    public double get_coordinate_Y() {
        return _coordinate_Y;
    }

    public void set_coordinate_Y(double _coordinate_Y) {
        this._coordinate_Y = _coordinate_Y;
    }

    public int get_id_Plate() {
        return _id_Plate;
    }

    public void set_id_Plate(int _id_Plate) {
        this._id_Plate = _id_Plate;
    }
}
