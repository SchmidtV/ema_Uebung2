package ema.uebung2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private TextView status, xText, yText, zText;
    private Button startButton, stopButton;
    private Sensor accSensor;
    private SensorManager sManager;
    private ArrayList<String[]> values;
    private boolean saveValues = false;
    private CSVWriter writer;
    private ProgressBar xProgressBar;
    private ProgressBar yProgressBar;
    private ProgressBar zProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accSensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);

        status = (TextView)findViewById(R.id.status);
        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);
        startButton = (Button)findViewById(R.id.startButton);
        stopButton = (Button)findViewById(R.id.stopButton);
        xProgressBar = (ProgressBar) findViewById(R.id.xProgressbar);
        yProgressBar = (ProgressBar) findViewById(R.id.yProgressbar);
        zProgressBar = (ProgressBar) findViewById(R.id.zProgressbar);

    }

    public void onStartCLicked(View v){
        values = new ArrayList<String[]>();
        saveValues = true;
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    public void onStopCLicked(View v){
        saveValues = false;
        //TODO: save values as csv
        if(values != null && values.size()>0){

            try {
                String root = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
//            File root = android.os.Environment.getExternalStorageDirectory();
                String fileName = "AccData.csv";
                String filePath = root + File.separator + fileName;
                File f = new File( filePath);
                writer = new CSVWriter(new FileWriter(filePath));
                writer.writeAll(values);
                writer.close();
                status.setText("File Saved! Length: " + values.size() + "");
            } catch (IOException e) {
                status.setText("Couldn't save. Length: " + values.size() + "");
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xText.setText(getString(R.string.xText, event.values[0]));
        if(event.values[0] >= 0){
            xProgressBar.setProgress(Math.round(event.values[0]*50));
//            xProgressBar.getIndeterminateDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        }else{
            xProgressBar.setProgress((int)(event.values[0]*-50));
//            xProgressBar.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        }

        yText.setText(getString(R.string.yText, event.values[1]));
        if(event.values[1] >= 0){
            yProgressBar.setProgress(Math.round(event.values[1]*50));
//            Drawable progressDrawable = yProgressBar.getProgressDrawable().mutate();
//            progressDrawable.setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
//            yProgressBar.setProgressDrawable(progressDrawable);
//            yProgressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        }else{
            yProgressBar.setProgress((int)(event.values[1]*-50));
//            Drawable progressDrawable = yProgressBar.getProgressDrawable().mutate();
//            progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
//            yProgressBar.setProgressDrawable(progressDrawable);
//            yProgressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }

        zText.setText(getString(R.string.zText, event.values[2]));
        if(event.values[1] >= 0){
            zProgressBar.setProgress(Math.round(event.values[2]*50));
        }else{
            zProgressBar.setProgress((int)(event.values[2]*-50));
        }
        if(saveValues && values.size() < 2000){
            String[] toStore = {Float.toString(event.values[0]), Float.toString(event.values[1]),Float.toString(event.values[2])};
            values.add(toStore);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
