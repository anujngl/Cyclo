package com.mabook.cyclo;

import android.app.Activity;
import android.location.Criteria;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mabook.cyclo.core.CycloConnector;
import com.mabook.cyclo.core.CycloProfile;


public class DashboardActivity extends Activity {

    private static final String TAG = "DashboardActivity";
    Location lastLocation;
    private Button buttonStart;
    private Button buttonStop;
    private Button buttonPause;
    private Button buttonResume;
    private TextView textUpdate;
    private CycloConnector gpsConn;
    private Spinner options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        buttonStart = (Button) findViewById(R.id.button_start);
        buttonStop = (Button) findViewById(R.id.button_stop);
        buttonPause = (Button) findViewById(R.id.button_pause);
        buttonResume = (Button) findViewById(R.id.button_resume);
        textUpdate = (TextView) findViewById(R.id.update);
        options = (Spinner) findViewById(R.id.options);

        gpsConn = new CycloConnector(this, new CycloConnector.StatusListener() {
            @Override
            public void onReceiveStatus(Bundle bundle) {
                int state = bundle.getInt("state", CycloConnector.STATE_STOPPED);
                Log.d(TAG, "state : " + state);
                switch (state) {
                    case CycloConnector.STATE_STOPPED:
                        buttonStart.setEnabled(true);
                        buttonStop.setEnabled(false);
                        buttonResume.setEnabled(false);
                        buttonPause.setEnabled(false);
                        options.setEnabled(true);
                        break;
                    case CycloConnector.STATE_STARTED:
                        buttonStart.setEnabled(false);
                        buttonStop.setEnabled(true);
                        buttonResume.setEnabled(false);
                        buttonPause.setEnabled(true);
                        options.setEnabled(false);
                        break;
                    case CycloConnector.STATE_PAUSED:
                        buttonStart.setEnabled(false);
                        buttonStop.setEnabled(true);
                        buttonResume.setEnabled(true);
                        buttonPause.setEnabled(false);
                        options.setEnabled(false);
                        break;
                }

            }

            @Override
            public void onReceiveUpdate(Bundle bundle) {
                Toast.makeText(DashboardActivity.this, bundle.getString("type"), Toast.LENGTH_LONG).show();
                Location location = bundle.getParcelable("location");
                if (location != null) {
                    textUpdate.setText(CycloConnector.dumpLocation(location, lastLocation));
                    lastLocation = location;
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gpsConn.bindService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gpsConn.unbindService();
    }

    public void onClickStart(View view) {
        int pos = options.getSelectedItemPosition();
        CycloProfile profile = null;
        Criteria criteria = null;
        Log.d(TAG, "onClickStart-" + pos);
        switch (pos) {
            case 0:
                break;
            case 1:
                criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                criteria.setAltitudeRequired(true);
                criteria.setBearingRequired(false);
                criteria.setSpeedRequired(true);
                criteria.setCostAllowed(true);
                profile = new CycloProfile();
                profile.setCriteria(criteria);
                profile.setMinTime(0);
                profile.setMinDistance(1);
                break;
            case 2:
                criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
                criteria.setAltitudeRequired(true);
                criteria.setBearingRequired(false);
                criteria.setSpeedRequired(true);
                criteria.setCostAllowed(true);
                profile = new CycloProfile();
                profile.setCriteria(criteria);
                profile.setMinTime(0);
                profile.setMinDistance(0);
                break;
            case 3:
                criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setSpeedRequired(true);
                criteria.setCostAllowed(true);
                profile = new CycloProfile();
                profile.setCriteria(criteria);
                profile.setMinTime(1000 * 30);
                profile.setMinDistance(1);
                break;
        }
        gpsConn.setProfile(profile);
        gpsConn.start();
    }

    public void onClickStop(View view) {
        gpsConn.stop();
    }

    public void onClickPause(View view) {
        gpsConn.pause();
    }

    public void onClickResume(View view) {
        gpsConn.resume();
    }
}
