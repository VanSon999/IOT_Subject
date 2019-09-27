package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends AppCompatActivity {

    private static final String LED_PIN = "BCM18";
    private Gpio mLedGpio;
//    private int status = "INIT";

    private void initGPIO(){
        PeripheralManager manager = PeripheralManager.getInstance();
        try{
            mLedGpio = manager.openGpio(LED_PIN);
        }catch(Exception e){
            Log.d("TAG","Error");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGPIO();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            mLedGpio.close();
        }catch (Exception e){
            Log.e("Tag","Error");
        }
    }
    void setupTimer(){
        Timer aTimer = new Timer();
        TimerTask aTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    mLedGpio.setValue(!mLedGpio.getValue());
                }catch (Exception e){

                }
//                switch (status){
//                    case 0: {
//
//                    }
//                }
            }
        };
        aTimer.schedule(aTask,1000,1000);
    }
}
