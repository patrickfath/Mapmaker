package de.office.mapmaker.mapmaker;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.orbotix.ConvenienceRobot;
import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.Sphero;
import com.orbotix.classic.RobotClassic;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;

public class MainActivity_StartScreen extends AppCompatActivity {

    private ConvenienceRobot mRobot;
    public ConvenienceRobot aRoboter;
    private SeekBar seekbar;
    private Button button_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        BluetoothDevice devices;


        DualStackDiscoveryAgent.getInstance().addRobotStateListener(new RobotChangedStateListener() {
            @Override
            public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
                switch (type) {
                    case Online:
                        if(robot instanceof RobotClassic) {
                            mRobot = new Sphero(robot);
                            aRoboter = mRobot;
                            createAppContent(mRobot, type);
                        }
                        break;
                    case Disconnected:
                        break;
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // This line assumes that this object is a Context
        try {
            DualStackDiscoveryAgent.getInstance().startDiscovery(this);
        } catch( DiscoveryException e ) {
            //handle exception
        }


    }

    @Override
    protected void onStop() {
        if( mRobot != null )
            mRobot.disconnect();
        super.onStop();
    }


    public ConvenienceRobot robotor () {
        return aRoboter;
    }

    private void createAppContent(final ConvenienceRobot mRobot, RobotChangedStateListener.RobotChangedStateNotificationType type) {
        seekbar = (SeekBar) findViewById(R.id.seekbar_aiming);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //TODO Ausgabe progress + aiming rausnehmen
                Log.i("Progress: ", progress+"");
                //seekbar progress = 0 - 100
                //aiming degrees = 0 - 360
                float aimingProgress= (float) (progress*3.6);
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
              setContentView(R.layout.activity_main);
             Intent i = new Intent( MainActivity_StartScreen.this, FertigeApp_free_ohneCollision4SecTimer.class );
             startActivity(i);




            }
        });
    }


}
