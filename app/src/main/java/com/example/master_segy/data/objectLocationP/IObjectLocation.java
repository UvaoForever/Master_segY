package com.example.master_segy.data.objectLocationP;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IObjectLocation {
    @Insert
    void insert(ObjectLocation objectLocation);
    @Update
    void update(ObjectLocation objectLocation);
    @Delete
    void delete(ObjectLocation objectLocation);
    @Query("SELECT * FROM objectLocation")
    List<ObjectLocation> getAll();
    @Query("SELECT * FROM objectLocation WHERE _id = :id")
    ObjectLocation getById(long id);
    @Query("SELECT _id FROM objectLocation WHERE title_object = :title_object")
    int getIdByTitleObject(String title_object);
    @Query("SELECT * FROM objectLocation WHERE title_object=:title")
    List<ObjectLocation> getAllTitle(String title);
}
