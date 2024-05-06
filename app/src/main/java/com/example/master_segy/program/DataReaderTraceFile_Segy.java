package com.example.master_segy.program;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataReaderTraceFile_Segy {
    public List<double[]> trails;
    private byte[] allBytes;
    private double Fs;
    private int channelCount;
    private int samplesCount;
    private short dt;
    private int tailLength;
    public DataReaderTraceFile_Segy(File source) throws IOException {
        trails = new ArrayList<>();
        allBytes = Files.readAllBytes(Paths.get(source.getPath()));
    }
    public double getChannelCount(){
        return channelCount;
    }
    public double getFs(){
        return Fs;
    }
    public int getSamplesCount(){
        return samplesCount;
    }
    public double[] getTrace(int Number){
        return trails.get(Number);
    }
    public short getDt() { return dt; }
    public int getTailLength(){
        return tailLength;
    }
    public static int toInt32(byte[] bytes, int index) {
        return (0xff & bytes[index]) | (0xff & bytes[index + 1]) << 8 | (0xff & bytes[index + 2]) << 16 | (0xff & bytes[index + 3]) << 24;
    }

    public void Read() {
        int commonHeader = 3600;
        int traceHeader = 240;
        //Количество трасс данных на ансамбль
        ByteBuffer wrapped = ByteBuffer.wrap(allBytes).order(ByteOrder.LITTLE_ENDIAN);
        short tracesNum = wrapped.get(3213);
        Log.i("traceNumFromFile", String.valueOf(tracesNum));
        //Интервал выборки. Микросекунды (мкс) для данных о времени, Герц (Гц) для данных о частоте, метры (м) или футы (футы) для данных о глубине.
        dt = wrapped.get(3217);
        Log.i("dt", String.valueOf(dt));

        //Количество выборок на трассу данных.
        // Примечание. Интервал выборки и количество выборок в заголовке двоичного файла должны соответствовать основному набору трасс сейсмических данных в файле.
        //  Количество выборок на трассу данных
        //Integer integer = new Integer(toInt32(allBytes,3221));
        int samplesNum = wrapped.getShort(3221);
        Log.i("samplesNumFromFile1", String.valueOf(samplesNum));
        //Код формата выборки данных
        short dataMask = wrapped.get(3225);
        Log.i("dataMask", String.valueOf(dataMask));
        //вес одной точки
        int nb = GetSampleBitWidth(dataMask);
        Log.i("nb", String.valueOf(nb));
        //Длина трасс
        tailLength = allBytes.length - commonHeader;
        Log.i("tailLength", String.valueOf(tailLength));
        int position = commonHeader + traceHeader;
        Log.i("position", String.valueOf(position));
        for (int i = 0; i < tracesNum; i++) {
            double[] trace = GetTraceData(GetBytesAtPosition(allBytes, position, nb * samplesNum), dataMask, samplesNum);
            trails.add(trace);
            position += traceHeader + nb * samplesNum;
        }
        Fs = GetSamplingFrequency(dt); // Частота дискретизации
        channelCount = trails.size();
        samplesCount = trails.get(0).length;
    }

    //позиции в трассировке
    private byte[] GetBytesAtPosition(byte[] source, int position, int length) {
        byte[] result = new byte[length];
        System.arraycopy(source, position, result, 0, length);
        return result;
    }

    //Частота дискретизации
    private double GetSamplingFrequency(short dt) {
        switch (dt) {
            case 2:
                return 500;
            case 4:
                return 250;
            case 10:
                return 96000;
            case 31:
                return 32000;
            case 62:
            case 63:
                return 16000;
            default:
                return 1000000.0 / dt;
        }
    }

    private int GetSampleBitWidth(short datamask)
    {
        switch (datamask)
        {
            case 3:
            case 4:
                return 2;
            default: return 4;
        }
    }
    private double[] GetTraceData(byte[] source, int dataMask, int numsmp)
    {
        double[] traceData = new double[numsmp];
        int valByteWidth;
        switch (dataMask)
        {
            case 1:
                // 4-byte IBM floating point
                break;
            case 2:
            case 4:
                // 4-byte fixed-point with gain (obsolete)
                // 4-byte integer
                for (int i = 0; i < numsmp; i++)
                {
                    valByteWidth = 4;
                    byte[] bytes = GetBytesAtPosition(source, i * valByteWidth, valByteWidth);
                    ByteBuffer wrapped = ByteBuffer.wrap(bytes); // big-endian by default
                    traceData[i] = wrapped.getInt(0);
                }
                break;
            case 3:
                // 2-byte integer
                for (int i = 0; i < numsmp; i++)
                {
                    valByteWidth = 2;
                    byte[] bytes = GetBytesAtPosition(source, i * valByteWidth, valByteWidth);
                    traceData[i] = bytes[0];
                }
                break;
            case 5:
                // 4-byte IEEE floatig-point
                for (int i = 0; i < numsmp; i++)
                {
                    valByteWidth = 4;
                    byte[] bytes = GetBytesAtPosition(source, i * valByteWidth, valByteWidth);
                    ByteBuffer wrapped = ByteBuffer.wrap(bytes); // big-endian by default
                    traceData[i] = wrapped.getDouble(0);
                }
                break;
            case 6:
            case 7:
                // Not currently used
                break;
            case 8:
                // 1-byte integer
                break;
        }
        return traceData;
    }
}