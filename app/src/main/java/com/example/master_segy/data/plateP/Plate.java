package com.example.master_segy.data.plateP;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.master_segy.data.objectLocationP.ObjectLocation;

import java.io.Serializable;

@Entity(tableName = "plate", foreignKeys = @ForeignKey(entity = ObjectLocation.class, parentColumns = "_id", childColumns = "id_Object", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))

public class Plate implements Serializable{
    @PrimaryKey(autoGenerate = true)
    @Nullable
    private int _id;
    @ColumnInfo(name = "title_plate")
    @NonNull
    private String _titlePlate;
    @ColumnInfo(name = "description")
    private String _descriptionPlate;
    @ColumnInfo(name = "id_Object")
    private int _id_Object;
    public Plate(String _titlePlate, String _descriptionPlate, int _id_Object) {
        this._titlePlate = _titlePlate;
        this._descriptionPlate = _descriptionPlate;
        this._id_Object = _id_Object;
    }
    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }
    public String get_descriptionPlate() {
        return _descriptionPlate;
    }
    public void set_descriptionPlate(String _descriptionPlate) {
        this._descriptionPlate = _descriptionPlate;
    }
    public int get_id_Object() {
        return _id_Object;
    }
    public void set_id_Object(int _id_Object) {
        this._id_Object = _id_Object;
    }

    @NonNull
    public String get_titlePlate() {
        return _titlePlate;
    }

    public void set_titlePlate(@NonNull String _titlePlate) {
        this._titlePlate = _titlePlate;
    }
}
