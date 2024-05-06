package com.example.master_segy.data.objectLocationP;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity (tableName = "objectLocation", indices = {@Index(value = {"title_object"}, unique = true)})
public class ObjectLocation implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int _id;
    @ColumnInfo(name = "title_object") @NonNull
    private String _titleObject;
    @ColumnInfo(name = "date_plane_work" , typeAffinity = ColumnInfo.TEXT)
    @NonNull
    private String _datePlaneWork;
    @ColumnInfo(name = "address")
    private String _address;
    public ObjectLocation(String _titleObject, String _datePlaneWork, String _address) {
        this._titleObject = _titleObject;
        this._datePlaneWork = _datePlaneWork;
        this._address = _address;
    }
    public String get_titleObject() {
        return _titleObject;
    }
    public String get_datePlaneWork() {
        return _datePlaneWork;
    }
    public String get_address() {
        return _address;
    }
    public int get_id() {
        return _id;
    }
    public void set_id(int _id) {
        this._id = _id;
    }
    public void set_titleObject(String _titleObject) {
        this._titleObject = _titleObject;
    }
    public void set_datePlaneWork(String _datePlaneWork) {
        this._datePlaneWork = _datePlaneWork;
    }
    public void set_address(String _address) {
        this._address = _address;
    }
}
