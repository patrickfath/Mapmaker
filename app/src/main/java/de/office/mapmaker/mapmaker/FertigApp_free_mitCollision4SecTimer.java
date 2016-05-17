package de.office.mapmaker.mapmaker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.orbotix.ConvenienceRobot;
import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.Sphero;
import com.orbotix.async.CollisionDetectedAsyncData;
import com.orbotix.async.DeviceSensorAsyncMessage;
import com.orbotix.classic.RobotClassic;
import com.orbotix.command.ConfigureCollisionDetectionCommand;
import com.orbotix.command.RollCommand;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.ResponseListener;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.common.internal.AsyncMessage;
import com.orbotix.common.internal.DeviceResponse;
import com.orbotix.common.sensor.Acceleration;
import com.orbotix.common.sensor.DeviceSensorsData;
import com.orbotix.common.sensor.LocatorData;
import com.orbotix.common.sensor.SensorFlag;
import com.orbotix.subsystem.SensorControl;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FertigApp_free_mitCollision4SecTimer extends AppCompatActivity {

    private ConvenienceRobot mRobot;
    private SeekBar seekbar;
    private Button button_start;
    private Button startButton;
    private Button stopButton;
    private Button pauseButton;

    private float canvasCenterX;
    private float canvasCenterY;
    private ImageView drawView;
    private Bitmap bitmap;
    private Canvas canvas;
    ConfigureCollisionDetectionCommand configureCollisionDetectionCommand;
    Timer timer;
    LocatorData locatorData;
    Paint paint;
    boolean collisionFlag = false;
    ArrayList<Float> positionXList = new ArrayList<>();
    ArrayList<Float> positionYList = new ArrayList<>();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);




        DualStackDiscoveryAgent.getInstance().addRobotStateListener(new RobotChangedStateListener() {
            @Override
            public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
                switch (type) {
                    case Online:
                        if (robot instanceof RobotClassic) {
                            mRobot = new Sphero(robot);
                            createAppContentStart(mRobot, type);
                        }
                        break;
                    case Disconnected:
                        break;
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void setSpherosStartCoords() {
        canvasCenterY = canvas.getHeight() / 2;
        canvasCenterX = canvas.getWidth() / 2;
        paint.setColor(Color.BLACK);
        canvas.drawText("+", canvasCenterX, canvasCenterY, paint);
    }



    public void drawSpherosWay(int xNew, int yNew) {
        paint.setColor(Color.RED);
        canvas.drawPoint(canvasCenterX + (xNew * 4), canvasCenterY + (yNew * 4), paint);

        drawView.setImageBitmap(bitmap);
    }

    private void createAppContentStart(final ConvenienceRobot mRobot, RobotChangedStateListener.RobotChangedStateNotificationType type) {
        seekbar = (SeekBar) findViewById(R.id.seekbar_aiming);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //TODO Ausgabe progress + aiming rausnehmen
                Log.i("Progress: ", progress + "");
                //seekbar progress = 0 - 100
                //aiming degrees = 0 - 360
                mRobot.setBackLedBrightness(255);
                float aimingProgress = (float) (progress * 3.6);
                mRobot.drive(aimingProgress, 0);
                Log.i("Aiming: ", aimingProgress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //on stop Tracking Touch
                mRobot.setZeroHeading();
            }
        });


        button_start = (Button) findViewById(R.id.button_start);
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAppContentDrive();
            }
        });
    }

    public void createAppContentDrive() {
        //if (robot instanceof RobotClassic) {
        setContentView(R.layout.activity_main);
        drawView = (ImageView) this.findViewById(R.id.imageView);
        bitmap = Bitmap.createBitmap((int) getWindowManager()
                .getDefaultDisplay().getWidth(), (int) getWindowManager()
                .getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        drawView.setImageBitmap(bitmap);
        paint = new Paint();



        paint.setStrokeWidth(10);
        paint.setTextSize(30);
        setSpherosStartCoords();

            startButton = (Button) findViewById(R.id.button_startAction);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    on(v);
                }
            });

            pauseButton = (Button) findViewById(R.id.button_pauseAction);
            pauseButton.setEnabled(false);
            pauseButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    pause(v);
                }
            });


            stopButton = (Button) findViewById(R.id.button_stopAction);
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    off(v);
                }
            });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        // This line assumes that thxis object is a Context
        try {
            DualStackDiscoveryAgent.getInstance().startDiscovery(this);
        } catch (DiscoveryException e) {
            //handle exception
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://de.office.mapmaker.mapmaker/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onStop() {
        if (mRobot != null)
            mRobot.disconnect();
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://de.office.mapmaker.mapmaker/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    public void pause(View view) {
        if(mRobot.isConnected()) {
            pauseButton.setEnabled(false);
           final Thread threaaa = new Thread() {
               @Override
               public void run() {
                   while(true) {
                       if(pauseButton.isEnabled()==false) {
                           mRobot.drive(mRobot.getLastHeading(), 0);
                       }
                   }
               }
           };
            threaaa.start();

        }

    }

    public void off(View view) {
        if (mRobot.isConnected()) {
            mRobot.sleep();
            this.onStop();
        }
    }


    public void on(View view) {
        if (mRobot.isConnected()) {
           this.pauseButton.setEnabled(true);

            timer = new Timer();
            mRobot.setZeroHeading();

            mRobot.enableCollisions(true);
            configureCollisionDetectionCommand = new ConfigureCollisionDetectionCommand(30, 0, 45, 0, 100);

            final TimerTask myTask = new TimerTask() {
                @Override
                public void run() {
                    if (collisionFlag == false) {
                        mRobot.sendCommand(configureCollisionDetectionCommand);
                        mRobot.sendCommand(new RollCommand(mRobot.getLastHeading(), 0.1f, RollCommand.State.GO));
                    } else {
                        mRobot.sendCommand(new RollCommand(mRobot.getLastHeading(), 0, RollCommand.State.STOP));
                        collisionFlag = false;
                        mRobot.drive(mRobot.getLastHeading() + 135, 0);
                    }
                }
            };
            timer.schedule(myTask, 0, 4000);


            long sensorFlag = SensorFlag.VELOCITY.longValue() | SensorFlag.LOCATOR.longValue();
            mRobot.enableSensors(sensorFlag, SensorControl.StreamingRate.STREAMING_RATE10);
            mRobot.addResponseListener(new ResponseListener() {
                @Override
                public void handleResponse(DeviceResponse deviceResponse, Robot robot) {

                }

                @Override
                public void handleStringResponse(String s, Robot robot) {

                }

                @Override
                public void handleAsyncMessage(AsyncMessage asyncMessage, Robot robot) {
                    if (asyncMessage instanceof DeviceSensorAsyncMessage) {
                        DeviceSensorAsyncMessage sensorsData = (DeviceSensorAsyncMessage) asyncMessage;
                        ArrayList<DeviceSensorsData> sensorDataArray = sensorsData.getAsyncData();
                        DeviceSensorsData dsd = sensorDataArray.get(sensorDataArray.size() - 1);
                        locatorData = dsd.getLocatorData();

                        positionXList.add(locatorData.getPositionX());
                        positionYList.add(locatorData.getPositionY());

                        Log.i("Pos: X: ", locatorData.getPositionX() + "");
                        Log.i("Pos: Y: ", locatorData.getPositionY() + "");

                    }
                }
            });



            mRobot.enableCollisions(true);
            mRobot.addResponseListener(new ResponseListener() {
                @Override
                public void handleResponse(DeviceResponse deviceResponse, Robot robot) {

                }

                @Override
                public void handleStringResponse(String s, Robot robot) {

                }

                @Override
                public void handleAsyncMessage(AsyncMessage asyncMessage, Robot robot) {
                    if (asyncMessage instanceof CollisionDetectedAsyncData) {

                        drawSpherosWay((int) locatorData.getPositionX(), (int) locatorData.getPositionY());

                        CollisionDetectedAsyncData.CollisionPower impactPower = ((CollisionDetectedAsyncData) asyncMessage).getImpactPower();
                        Acceleration impactAcceleration = ((CollisionDetectedAsyncData) asyncMessage).getImpactAcceleration();
                        float impactSpeed = ((CollisionDetectedAsyncData) asyncMessage).getImpactSpeed();
                        collisionFlag = true;

                    } else {
                        //nothing to happen jet
                    }
                }
            });

        }
        }
    }
