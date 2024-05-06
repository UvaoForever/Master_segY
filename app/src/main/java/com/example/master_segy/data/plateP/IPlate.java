package com.example.master_segy.data.plateP;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import java.util.List;
@Dao
public interface IPlate {
    @Insert
    void insert(Plate plate);
    @Update
    void update(Plate plate);
    @Delete
    void delete(Plate plate);
    @Query("SELECT * FROM plate")
    List<Plate> getAll();
    @Query("SELECT * FROM plate WHERE id_Object = :id")
    List<Plate> getAllObject(long id);
    @Query("SELECT * FROM plate WHERE _id = :id")
    Plate getById(long id);

    @Query("SELECT _id FROM plate WHERE title_plate = :title_plate")
    int getIdPlateByTitlePlate(String title_plate);
}
