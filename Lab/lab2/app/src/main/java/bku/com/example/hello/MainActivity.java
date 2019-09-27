package bku.com.example.hello;

import android.app.Activity;
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
public class MainActivity extends Activity {
    private String PIN_LED = "BCM18";
    private Gpio mLedGpio;
    private String status = "";
    private boolean check_timerun = false;

    private int intervaltime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGPIO();
        blink_led();
    }

    private void initStatus() {
        status = "INIT";
        intervaltime = 300;
        check_timerun = false;
    }

    private void setON() {
        status = "ON";
        intervaltime = 200;
    }

    private void setOFF() {
        status = "OFF";
        intervaltime = 100;
    }

    private void initGPIO() {
        PeripheralManager manager = PeripheralManager.getInstance();
        try {
            mLedGpio = manager.openGpio(PIN_LED);
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
//            mLedGpio.setActiveType(Gpio.ACTIVE_LOW);
            initStatus();
            Log.i("TAG", "Start blinking LED GPIO pin");
        } catch (Exception e) {
            Log.d("not led", "Error123");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mLedGpio.close();
        } catch (Exception e) {
            Log.e("can't close", "Error");
        }
    }

    void blink_led() {
        Timer aTime = new Timer();
        TimerTask aTask = new TimerTask() {
            @Override
            public void run() {
//                try{
//                    mLedGpio.setValue(!mLedGpio.getValue());
//                }catch (Exception e){
//
//                }
                switch (status) {
                    case "INIT":
                        setON();
                        setPIN_LED(true);
                        break;
                    case "ON":

                        if (check_timerun) {
                            setOFF();
                            setPIN_LED(false);
                            check_timerun = false;
                        }
                        break;
                    case "OFF":

                        if (check_timerun) {
                            setON();
                            setPIN_LED(true);
                            check_timerun = false;
                        }
                        break;
                }

                runtime();
            }
        };
        aTime.schedule(aTask, 0, 10);
    }

    private void setPIN_LED(boolean kt){
            try{
                mLedGpio.setValue(kt);
            }catch (Exception e){
                Log.e("Cant set", "Error321");
            }
            check_timerun = false;
    }
//    private void BlinkLed() {
//        while (true){
//            if(intervaltime > 0){
//                intervaltime = intervaltime - 1;
//            }else {
//                break;
//            }
//        }
//        try{
//            mLedGpio.setValue(!kt);
//        }catch (Exception e){
//            Log.e("Cant set", "Error321");
//        }
//    }

    private void runtime(){
        if(intervaltime > 0) {
            intervaltime = intervaltime - 1;
            if (intervaltime == 0) {
                check_timerun = true;
            }
        }
    }
}
