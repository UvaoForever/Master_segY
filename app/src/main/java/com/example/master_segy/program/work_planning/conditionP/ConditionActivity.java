package com.example.master_segy.program.work_planning.conditionP;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.master_segy.R;
import com.example.master_segy.data.AppDataBase;
import com.example.master_segy.data.objectLocationP.ObjectLocation;
import com.example.master_segy.data.plateP.Plate;
import com.example.master_segy.data.pointP.Point;
import com.example.master_segy.data.reportP.Report;
import com.example.master_segy.data.traceP.Trace;
import com.example.master_segy.databinding.ActivityConditionBinding;
import com.example.master_segy.program.DataReaderTraceFile_Segy;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolator;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

public class ConditionActivity extends AppCompatActivity {

    private ImageView imageView;
    private ArrayList<Trace> trails;
    TreeSet<Double> arrayX = new TreeSet<Double>();
    TreeSet<Double> arrayY = new TreeSet<Double>();
    private ActivityConditionBinding binding;
    AppDataBase db;
    ArrayList<ObjectLocation> objectList;
    ArrayList<Plate> plateList;
    ArrayList<Plate> plateNameList = new ArrayList<Plate>();
    ArrayList<Point> pointList;
    ArrayList<Report> reportList;
    ArrayList<Trace> traceList;
    boolean itemSelected = false;
    private EditText editText_X, editText_Y;
    private TextInputLayout textInputLayoutX, textInputLayoutY;
    private TextView mActionOk;
    private long attribute_id = 0;
    String item, titleObject, titlePlate;
    ArrayList<String> spinnerList = new ArrayList<String>();
    ArrayList<String> attributeList = new ArrayList<String>();
    ArrayList<Trace> normalTraceList; // Возможно удалить
    private static final int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);
        binding = ActivityConditionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        db = AppDataBase.getInstance(getApplicationContext());
        plateList = new ArrayList<Plate>(db.plateDao().getAll()); // Получение всех плит
        pointList = new ArrayList<Point>(db.pointDao().getAll()); // Получение координат ВСЕХ точек
        reportList = new ArrayList<Report>(db.reportDao().getAll()); // Получение всех отчётов
        traceList = new ArrayList<Trace>(db.traceDao().getAll()); // Получение всех трасс
        itemSelected = false;
        editText_X = findViewById(R.id.editTextX);
        editText_Y = findViewById(R.id.editTextY);
        textInputLayoutX = findViewById(R.id.editTextLayoutX);
        textInputLayoutY = findViewById(R.id.editTextLayoutY);
        mActionOk = findViewById(R.id.buttonCalculate);
        createSpinnerList(); // Формирование списка для spinnerPlate
        createAttributeList(); // Формирование списка для spinnerAttribute

        Spinner spinnerPlate = findViewById(R.id.spinnerPlate);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapterPlate = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerList);
        // Определяем разметку для использования при выборе элемента
        adapterPlate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinnerPlate.setAdapter(adapterPlate);

        // Обработчик выбора плиты
        AdapterView.OnItemSelectedListener plateSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id != 0){
                    // Получаем выбранный объект
                    item = (String)parent.getItemAtPosition(position);
                    titleObject = item.substring(0, item.indexOf("|") - 1);
                    titlePlate= item.substring(item.indexOf("|") + 2);
                    correctionTraceList(titleObject, titlePlate);
                    creatingArrays();
                    editText_X.setText(String.valueOf(arrayX.size()));
                    editText_Y.setText(String.valueOf(arrayY.size()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinnerPlate.setOnItemSelectedListener(plateSelectedListener);

        Spinner spinnerAttribute = findViewById(R.id.spinnerAttribute);
        ArrayAdapter<String> adapterAttribute = new ArrayAdapter(this, android.R.layout.simple_spinner_item, attributeList);
        adapterAttribute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAttribute.setAdapter(adapterAttribute);

        // Обработчик выбора атрибута
        AdapterView.OnItemSelectedListener attributeSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                attribute_id = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinnerAttribute.setOnItemSelectedListener(attributeSelectedListener);

        // Обработчик нажатия на кнопку
        mActionOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    correctionTraceList(titleObject, titlePlate);
                    creatingArrays();
                    dataReadingFromBD();
                }
            }

        });
    }

    private void createAttributeList(){
        attributeList.clear();
        attributeList.add(getResources().getString(R.string.selectAttribute));
        attributeList.add(getResources().getString(R.string.attr1));
        attributeList.add(getResources().getString(R.string.attr2));
        attributeList.add(getResources().getString(R.string.attr3));
        attributeList.add(getResources().getString(R.string.attr4));
        attributeList.add(getResources().getString(R.string.attr5));
        attributeList.add(getResources().getString(R.string.attr6));
    }

    private void createSpinnerList(){
        spinnerList.clear();
        spinnerList.add(getResources().getString(R.string.selectPlate));
        for (int i = 0; i < plateList.size(); i++){
            int objectId = plateList.get(i).get_id_Object();
            String titleObject = db.objectDao().getById(objectId).get_titleObject();
            spinnerList.add(titleObject + " | " + plateList.get(i).get_titlePlate());
        }
    }

    private void correctionTraceList(String titleObject, String titlePlate){
        ArrayList deleted_traces = new ArrayList();
        ArrayList deleted_reports = new ArrayList();
        traceList = new ArrayList<Trace>(db.traceDao().getAll()); // Получение всех трасс
        reportList = new ArrayList<Report>(db.reportDao().getAll());
        int idObject = db.objectDao().getIdByTitleObject(titleObject);
        int correctIdPlate = db.plateDao().getIdPlateByTitlePlate(titlePlate);
        //ObjectLocation objectLocation = db.objectDao().getById(idObject);
        for (int i = 0; i < traceList.size(); i++){
            int idReport = reportList.get(i).get_id();
            int idPoint = db.reportDao().getById(idReport).get_id_Point();
            int idPlate = db.pointDao().getById(idPoint).get_id_Plate();
            Plate plate = db.plateDao().getById(idPlate);
            int id_test = plate.get_id_Object();
            if (id_test != idObject || idPlate != correctIdPlate){
                deleted_traces.add(traceList.get(i));
                deleted_reports.add(reportList.get(i));
            }
        }
        for (int i = 0; i < deleted_traces.size(); i++){
            traceList.remove(deleted_traces.get(i));
            reportList.remove(deleted_reports.get(i));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_primaryprocessing, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.buttonFile) {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(chooseFile, "Choose a file"), 1);
        }
        else{
            onBackPressed();
        }
        return true;
    }

    private void dataReadingFromBD(){
        Collections.sort(traceList, Comparator.comparing(Trace::getTitle_trace)); // Сортировка списка по имени файлов
        double[][] points;

        switch ((int) attribute_id){
            case 1:
                points = attribyteOne(traceList, traceList.get(0).get_dt());
                break; // Энергия сигнала, атрибут 1
            case 2:
                traceList = normalization(traceList); // Нормирование сигнала, для атрибута 2
                points = attribyteOne(traceList, traceList.get(0).get_dt());
                break;
            case 3:
                points = attribyteThree(traceList); // Расчёт частоты максимума спектра Фурье, атрибут 3
                break;
            case 4:
                points = attribyteFour(traceList, false); // Расчёт площади спектра, атрибут 4
                break;
            case 5:
                points = attribyteFour(traceList, true); // Нормирование спектра Фурье, для атрибута 5
                break;
            case 6:
                //points = attribyteThree(traceList);
                points = attribyteSix(traceList);
                break;
            default: return;
        }
        interpolation(points);
    }

    // Возможно удалить
    private double arraySize(/*ArrayList<Double>*/TreeSet<Double> array){
        /*double max = array.get(0).doubleValue();
        for (int i = 0; i < array.size(); i++)
            if (max < array.get(i).doubleValue())
                max = array.get(i).doubleValue();*/
        double max = Double.MIN_VALUE;
        for (double value : array){
            if (max < value)
                max = value;
        }

        if (max < array.size())
            max = array.size();
        else
            max++;

        return max;
    }

    private double minCoordinate(TreeSet<Double> array){
        double min = Double.MAX_VALUE;
        for (double value : array){
            if (min > value)
                min = value;
        }
        return min;
    }

    private double maxCoordinate(TreeSet<Double> array){
        double max = Double.MIN_VALUE;
        for (double value : array){
            if (max < value)
                max = value;
        }
        return max;
    }

    // Нормализация, массив, спектр Фурье (Не используется)
    public double[][] normalization(double[][] points){
        double max = points[0][0];
        for (int i = 0; i < points.length; i++)
            for (int j = 0; j < points[0].length; j++)
                if (max < points[i][j])
                    max = points[i][j];

        for (int i = 0; i < points.length; i++)
            for (int j = 0; j < points[0].length; j++)
                points[i][j] /= max;

        return points;
    }
    // Нормализация, сигнал (переопределение для первого атрибута)
    public ArrayList<Trace> normalization(ArrayList<Trace> trails){
        for (int i = 0; i < trails.size(); i++) {
            double[] signal = trails.get(i).get_signal();
            double max = signal[0];
            for (int j = 0; j < signal.length; j++)
                if (max < signal[j])
                    max = signal[j];
            for (int k = 0; k < signal.length; k++)
                signal[k] /= max;
            trails.get(i).set_signal(signal);
        }

        return trails;
    }

    private void creatingArrays(){
        arrayX.clear();
        arrayY.clear();

        for (int i = 0; i < traceList.size(); i++) {
            int idReport = reportList.get(i).get_id();
            int idPoint = db.reportDao().getById(idReport).get_id_Point();
            arrayX.add(db.pointDao().getById(idPoint).get_coordinate_X());
            arrayY.add(db.pointDao().getById(idPoint).get_coordinate_Y());
        }
    }

    // Энергия сигнала
    public double[][] attribyteOne(ArrayList<Trace> trails, int dt){
        double e = 0;
        //creatingArrays();
        double[][] points = new double[arrayX.size()][arrayY.size()];

        for (int i = 0; i < trails.size(); i++) { // НЫНЕШНИЙ ЦИКЛ
            int idReport = reportList.get(i).get_id();
            int idPoint = db.reportDao().getById(idReport).get_id_Point();
            int x = (int) db.pointDao().getById(idPoint).get_coordinate_X(); // Подумать над типом
            int y = (int) db.pointDao().getById(idPoint).get_coordinate_Y(); // Подумать над типом
            int index_x = 0;
            int index_y = 0;
            e = 0;
            double[] signal = trails.get(i).get_signal();
            for (int j = 0; j < signal.length; j++)
                e += Math.pow(signal[j], 2) * dt;

            for (double one : arrayY){
                if ((int)one == y)
                    break;
                index_y++;
            }
            for (double two : arrayX){
                if ((int)two == x)
                    break;
                index_x++;
            }

            points[index_x][index_y] = e;
        }
        return points;
    }

    // Спектр Фурье
    public double[][] attribyteThree(ArrayList<Trace> trails){
        //creatingArrays();
        double[][] points = new double[arrayX.size()][arrayY.size()];
        for (int i = 0; i < trails.size(); i++){
            int idReport = reportList.get(i).get_id();
            int idPoint = db.reportDao().getById(idReport).get_id_Point();
            int x = (int) db.pointDao().getById(idPoint).get_coordinate_X(); // Подумать над типом
            int y = (int) db.pointDao().getById(idPoint).get_coordinate_Y(); // Подумать над типом
            int index_x = 0;
            int index_y = 0;
            double[] signal = trails.get(i).get_signal();

            FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
            Complex[] transformedData = transformer.transform(signal, TransformType.FORWARD);

            // Находим частоту, соответствующую максимуму спектра
            double maxAmplitude = Double.MIN_VALUE;
            double minAmplitude = Double.MAX_VALUE;
            int maxFrequencyIndex = 0;

            for (int j = 0; j < transformedData.length / 2; j++) {
                double amplitude = Math.pow(transformedData[j].abs(), 2);
                if (amplitude > maxAmplitude) {
                    maxAmplitude = amplitude;
                    maxFrequencyIndex = j;
                }
                if (amplitude < minAmplitude)
                    minAmplitude = amplitude;
            }

            for (double one : arrayY){
                if ((int)one == y)
                    break;
                index_y++;
            }
            for (double two : arrayX){
                if ((int)two == x)
                    break;
                index_x++;
            }
            double df = (maxAmplitude - minAmplitude) * 1.0 / (trails.get(i).get_samples_count());
            points[index_x][index_y] = maxFrequencyIndex * df; //maxFrequencyIndex * trails.get(i).get_FS() / signal.length;//maxAmplitude;
        }
        return points;
    }

    // Шаг дискретизации по частоте
    public double[][] attribyteFour(ArrayList<Trace> trails, boolean normalize){
        double e = 0;
        double[][] points = new double[arrayX.size()][arrayY.size()];
        for (int i = 0; i < trails.size(); i++) {
            int idReport = reportList.get(i).get_id();
            int idPoint = db.reportDao().getById(idReport).get_id_Point();
            int x = (int) db.pointDao().getById(idPoint).get_coordinate_X();
            int y = (int) db.pointDao().getById(idPoint).get_coordinate_Y();
            int index_x = 0;
            int index_y = 0;
            double[] signal = trails.get(i).get_signal();

            FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
            Complex[] transformedData = transformer.transform(signal, TransformType.FORWARD);
            double maxAmplitude = Double.MIN_VALUE;
            double minAmplitude = Double.MAX_VALUE;

            for (int j = 0; j < transformedData.length / 2; j++) {
                //double amplitude = Math.pow(transformedData[j].abs(), 2);
                double amplitude = Math.pow(transformedData[j].abs(), 2);
                if (amplitude > maxAmplitude)
                    maxAmplitude = amplitude;
                if (amplitude < minAmplitude)
                    minAmplitude = amplitude;
            }

            //double df = 1 * 1.0 / (traceList.get(i).get_dt() * (traceList.get(i).get_samples_count() - 1)); // Шаг дискретизации по частоте
            double df = (maxAmplitude - minAmplitude) * 1.0 / (trails.get(i).get_samples_count());
            for (int j = 0; j < transformedData.length / 2; j++) {
                double data = Math.pow(transformedData[j].abs(), 2);
                if (normalize == true)
                    data = Math.pow(transformedData[j].abs(), 2) * 1.0 / maxAmplitude;
                e += Math.pow(data, 2) * df;
            }

            for (double one : arrayY) {
                if ((int) one == y)
                    break;
                index_y++;
            }
            for (double two : arrayX) {
                if ((int) two == x)
                    break;
                index_x++;
            }

            points[index_x][index_y] = e;
        }
        return points;

    }

    public double[][] attribyteSix(ArrayList<Trace> trails){
        double[][] points = new double[arrayX.size()][arrayY.size()];
        for (int i = 0; i < trails.size(); i++) {
            int idReport = reportList.get(i).get_id();
            int idPoint = db.reportDao().getById(idReport).get_id_Point();
            int x = (int) db.pointDao().getById(idPoint).get_coordinate_X();
            int y = (int) db.pointDao().getById(idPoint).get_coordinate_Y();
            int index_x = 0;
            int index_y = 0;
            double[] signal = trails.get(i).get_signal();

            FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
            Complex[] transformedData = transformer.transform(signal, TransformType.FORWARD);
            double maxAmplitude = Double.MIN_VALUE;
            double minAmplitude = Double.MAX_VALUE;

            for (int j = 0; j < transformedData.length / 2; j++) {
                //double amplitude = Math.pow(transformedData[j].abs(), 2);
                double amplitude = Math.pow(transformedData[j].abs(), 2);
                if (amplitude > maxAmplitude)
                    maxAmplitude = amplitude;
                if (amplitude < minAmplitude)
                    minAmplitude = amplitude;
            }

            double df = (maxAmplitude - minAmplitude) * 1.0 / (trails.get(i).get_samples_count());
            double numerator = 0; // Числитель
            double denumerator = 0; // Знаменатель
            for (int j = 0; j < transformedData.length / 2; j++) {
                numerator += Math.pow(transformedData[j].abs(), 2) * df;
                denumerator += Math.pow(transformedData[j].abs(), 2);
            }

            double F = numerator * 1.0 / denumerator;

            for (double one : arrayY) {
                if ((int) one == y)
                    break;
                index_y++;
            }
            for (double two : arrayX) {
                if ((int) two == x)
                    break;
                index_x++;
            }

            points[index_x][index_y] = F;
        }

        return points;

    }

    public void interpolation(double[][] points){
        TextView t1 = findViewById(R.id.resultText);

        // Создание объекта интерполятора
        BicubicInterpolator interpolator = new BicubicInterpolator();

        double[] interpolatorArrayOne = new double[points.length];
        double[] interpolatorArrayTwo = new double[points[0].length];

        int index = 0;
        for (double x : arrayX){
            interpolatorArrayOne[index] = x;
            index++;
        }

        index = 0;
        for (double y : arrayY){
            interpolatorArrayTwo[index] = y;
            index++;
        }

        Arrays.sort(interpolatorArrayOne);
        Arrays.sort(interpolatorArrayTwo);

        int countX = Integer.parseInt(String.valueOf(editText_X.getText()));
        int countY = Integer.parseInt(String.valueOf(editText_Y.getText()));
        double[] xNew = new double[countX]; // В каких точках искать
        double[] yNew = new double[countY]; // В каких точках искать
        for (int i = 0; i < xNew.length; i++)
            xNew[i] = Double.MAX_VALUE; // В каких точках искать
        for (int i = 0; i < yNew.length; i++)
            yNew[i] = Double.MAX_VALUE; // В каких точках искать

        double minX = minCoordinate(arrayX);
        double maxX = maxCoordinate(arrayX);
        double minY = minCoordinate(arrayY);
        double maxY = maxCoordinate(arrayY);
        double dx = (maxX - minX) * 1.0 / (countX - 1);
        double dy = (maxY - minY) * 1.0 / (countY - 1);
        /*for (int i = 1; i <= countX; i++){
            xNew[i-1] = i * dx;
        }
        xNew[countX - 1] = maxX;
        for (int i = 1; i <= countY; i++){
            yNew[i-1] = i * dy;
        }*/
        Log.i("dx = ", String.valueOf(dx));
        int k = 0;
        int repeat = 0;
        int position = 0;
        for (int i = 0; i < countX; i++){
            if (i < interpolatorArrayOne.length - 1)
                xNew[i] = interpolatorArrayOne[i] + ((interpolatorArrayOne[i + 1] - interpolatorArrayOne[i]) * 1.0 / 2);
            else {
                    xNew[i] = xNew[position] + (interpolatorArrayOne[++k] - xNew[position]) * 1.0 / 2;
                    position++;
                    xNew[countX - 1] = maxX;
                    Arrays.sort(xNew);
                    if (k >= interpolatorArrayOne.length - 1){
                        position = ++repeat;
                        k = 0;
                        Arrays.sort(xNew);
                    }
            }
        }
        xNew[countX - 1] = maxX;

        k = 0;
        repeat = 0;
        position = 0;
        for (int i = 0; i < countY; i++){
            if (i < interpolatorArrayOne.length - 1)
                yNew[i] = interpolatorArrayTwo[i] + ((interpolatorArrayTwo[i + 1] - interpolatorArrayTwo[i]) * 1.0 / 2);
            else {
                yNew[i] = yNew[position] + (interpolatorArrayTwo[++k] - yNew[position]) * 1.0 / 2;
                position++;
                yNew[countY - 1] = maxY;
                Arrays.sort(yNew);
                if (k >= interpolatorArrayTwo.length - 1){
                    position = ++repeat;
                    k = 0;
                    Arrays.sort(yNew);
                }
            }
        }
        yNew[countY - 1] = maxY;
        imageView = (ImageView) findViewById(R.id.imageViewPlate);
        Bitmap resultBitmap = Bitmap.createBitmap(xNew.length, yNew.length, Bitmap.Config.ARGB_8888);


        // Вычисление коэффициентов интерполяции
        BicubicInterpolatingFunction function = interpolator.interpolate(
                interpolatorArrayOne, // Столько же, сколько и СТРОК (Чёткие координаты X)
                interpolatorArrayTwo, // Для количества элементов В СТРОКЕ (Чёткие координаты Y)
                points);

        String s = "";

        for(int i = 0; i < xNew.length; i++)
        {
            for(int j = 0; j < yNew.length; j++)
            {
                Log.i("i = ", String.valueOf(xNew[i]));
                Log.i("j = ", String.valueOf(yNew[j]));
                // Вычисление значения интерполяции в определенной точке
                double interpolatedValue = function.value(xNew[i], yNew[j]);
                //interpolatedValue = Math.round(interpolatedValue * 100.00) / 100.00;
                interpolatedValue = Double.parseDouble(String.valueOf(interpolatedValue));
                //        System.out.println("Interpolated value: " + interpolatedValue);
                s = s + Double.toString(interpolatedValue) + "    ";
                //t1.setText(String.valueOf(s));
                int color = getColorForTile((int) Math.round(interpolatedValue));
                resultBitmap.setPixel(i, j, color);
            }
            s += "\n";
        }
        // Создание объекта Paint для установки цвета и других параметров отрисовки
// Получение объекта Canvas для рисования
        Canvas canvas = new Canvas(resultBitmap);
// Установка размера точки

        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        //canvas.drawPoint(1, 1, paint);
        //canvas.drawPoint(2, 2, paint);
        imageView.setImageBitmap(resultBitmap);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public String getFileName(Uri uri) {
        String result = null;
        String[] projection = {MediaStore.Files.FileColumns.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
            }
        } catch (IllegalArgumentException e){
            Log.d("Error", e.getMessage());
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private int getColorForTile(int strength) {
        if (strength >= 10) {
            return Color.GREEN;
        } else if (strength >= 8) {
            return Color.YELLOW;
        } else if (strength >= 6){
            return ContextCompat.getColor(this, R.color.orange);
        } else if (strength >= 3) {
            return ContextCompat.getColor(this, R.color.light_red);
        }
        else {
            return Color.RED;
        }
    }
    private boolean isValid(){
        boolean flag = true;
        if (editText_X.getText().toString().isEmpty()) {
            textInputLayoutX.setError(getString(R.string.stepX_Error));
            flag = false;
        } else {
            textInputLayoutX.setErrorEnabled(false);
        }
        if (editText_Y.getText().toString().isEmpty()) {
            textInputLayoutY.setError(getString(R.string.stepY_Error));
            flag = false;
        } else {
            textInputLayoutY.setErrorEnabled(false);
        }
        return flag;
    }
}
