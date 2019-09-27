package bku.com.example.lab3;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
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
    private boolean onrun = false;
    private int counter = 0;
    private int number1 = 0;
    private int number2 = 0;
    private Button btn1;
    private Button btn2;
    private final Timer timer = new Timer();
    private Random rd = new Random();
    private ArrayList<DataPoint> SetSum1 = new ArrayList<>();
    private ArrayList<DataPoint> SetSum2 = new ArrayList<>();
    private LineGraphSeries<DataPoint> series1 = null;
    private LineGraphSeries<DataPoint> series2 = null;
    private GraphView graph1;
    private GraphView graph2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        btn1 = findViewById(R.id.btn_start);
        btn2 = findViewById(R.id.btn_stop);
        graph1 = (GraphView) findViewById(R.id.graph1);
        graph2 = (GraphView) findViewById(R.id.graph2);
        graph1.getViewport().setMinY(20);
        graph1.getViewport().setMaxY(70);
        graph1.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setMinY(20);
        graph2.getViewport().setMaxY(70);
        graph2.getViewport().setYAxisBoundsManual(true);
        graph1.getViewport().setScrollable(true); // enables horizontal scrolling
        graph2.getViewport().setScrollable(true); // enables horizontal scrolling
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!onrun) {setupThingSpeakTimer();Toast.makeText(MainActivity.this, "Start!", Toast.LENGTH_LONG).show(); onrun = true;}
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onrun) {
                    onrun = false;
                    Toast.makeText(MainActivity.this, "Stop!", Toast.LENGTH_LONG).show();
                    timer.cancel();
                }
            }
        });
    }

    void setupThingSpeakTimer(){
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                number1 = 20 + rd.nextInt(50);
                number2 = 20 + rd.nextInt(40);
                sendDataToThingSpeak(number1,number2);
            }
        };
//        JSONObject jsonObject = new JSONObject();
//        JSONArray = jsonObject.
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                getDataFromThingSpeak();
            }
        };
        timer.schedule(task1,0,20000);
        timer.schedule(task2,5000,20000);
    }
    private void getDataFromThingSpeak() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        String API_KEY_READ ="C8EDLTG4TMZ73WCN";
        Request request = builder.url("http://api.thingspeak.com/channels/867302/feeds.json?api_key=" + API_KEY_READ + "&results=1").build();
        counter++;
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final String error = e.toString();
                Log.e("Get",error);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Get" + error, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String jsonString = response.body().string();
                LineGraphSeries<DataPoint> series1;
                LineGraphSeries<DataPoint> series2;
                try {
                    JSONObject res = new JSONObject(jsonString);
                    JSONArray feeds = res.getJSONArray("feeds");
                    if(feeds != null && feeds.length()!=0) {
                        JSONObject field = feeds.getJSONObject(0);
                        final int field1 = field.getInt("field1");
                        final int field2 = field.getInt("field2");
                        DataPoint temp1 = new DataPoint(counter, field1);
                        DataPoint temp2 = new DataPoint(counter, field2);
                        SetSum1.add(temp1);
                        SetSum2.add(temp2);
                        DataPoint[] a = new DataPoint[counter];
                        DataPoint[] b = new DataPoint[counter];
                        for (int i = 0; i < counter; i++) {
                            a[i] = SetSum1.get(i);
                            b[i] = SetSum2.get(i);
                        }
                        series1 = new LineGraphSeries<>(a);
                        series2 = new LineGraphSeries<>(b);
                        series1.setDrawDataPoints(true);
                        series1.setDataPointsRadius(5);
                        series1.setThickness(2);
                        series2.setDrawDataPoints(true);
                        series2.setDataPointsRadius(5);
                        series2.setThickness(2);
                        series1.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(MainActivity.this, "Series1: On Data Point clicked: "+dataPoint, Toast.LENGTH_SHORT).show();
                            }
                        });
                        series2.setOnDataPointTapListener(new OnDataPointTapListener() {
                            @Override
                            public void onTap(Series series, DataPointInterface dataPoint) {
                                Toast.makeText(MainActivity.this, "Series1: On Data Point clicked: "+dataPoint, Toast.LENGTH_SHORT).show();
                            }
                        });
                        graph1.addSeries(series1);
                        graph2.addSeries(series2);
                    }


//                    final String kt = Integer.toString(field1) + " + " + Integer.toString(field2);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(MainActivity.this,kt,Toast.LENGTH_LONG).show();
//                        }
//                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this,jsonString,Toast.LENGTH_LONG).show();
//                    }
//                });


            }
        });
    }

    private void sendDataToThingSpeak(int counter1, int counter2) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        String API_KEY_WRITE = "1P2C1EHA4YWA1ZSX";
        String c1 = Integer.toString(counter1);
        String c2 = Integer.toString(counter2);
        Request request = builder.url("http://api.thingspeak.com/update?api_key=" + API_KEY_WRITE + "&field1=" + c1 +"&field2=" +c2).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final String error =  e.toString();
                Log.e("Send",error);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Send" + error, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String jsonString = response.body().string();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(),jsonString,Toast.LENGTH_LONG).show();
//                    }
//                });
            }
        });

    }
}
