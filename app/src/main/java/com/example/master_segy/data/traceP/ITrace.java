package com.example.master_segy.data.traceP;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ITrace {
    @Insert
    void insert(Trace trace);
    @Update
    void update(Trace trace);
    @Delete
    void delete(Trace trace);
    @Query("SELECT * FROM trace")
    List<Trace> getAll();
    @Query("DELETE FROM trace WHERE id_report=:id")
    void deleteToReport(long id);
    @Query("SELECT * FROM trace WHERE _id = :id")
    Trace getById(long id);
    @Query("SELECT * FROM trace WHERE id_report = :id")
    List<Trace> getAllReport(long id);
}
