package com.example.master_segy.data.pointP;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.example.master_segy.data.pointP.Point;

import java.util.List;
@Dao
public interface IPoint {
    @Insert
    void insert(Point point);
    @Update
    void update(Point point);
    @Delete
    void delete(Point point);
    @Query("SELECT * FROM point")
    List<Point> getAll();
    @Query("SELECT * FROM point WHERE id_Plate = :id")
    List<Point> getAllObject(long id);
    @Query("SELECT * FROM point WHERE _id = :id")
    Point getById(long id);
    @Query("SELECT * FROM point WHERE coordinate_X = :coordinate_X AND coordinate_Y = :coordinate_Y LIMIT 1")
    boolean is_Exist(double coordinate_X, double coordinate_Y);
}
