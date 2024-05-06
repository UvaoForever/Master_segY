package com.example.master_segy.data.traceP;

import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;

public class ArrayTypeConverter{
    @TypeConverter
     public String toJSON_mass(double [] _signal){
        return new Gson().toJson(_signal);
    }
    @TypeConverter
    public double [] fromJSON_mass(String str) {
        Type itemsArrType = new TypeToken<double[]>() {}.getType();
        return new Gson().fromJson(str, itemsArrType);
    }
}
