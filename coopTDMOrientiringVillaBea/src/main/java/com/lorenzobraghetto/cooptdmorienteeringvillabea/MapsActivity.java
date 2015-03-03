package com.lorenzobraghetto.cooptdmorienteeringvillabea;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lorenzobraghetto.compasslibrary.Geopoint;
import com.lorenzobraghetto.utils.MyLocationOverlay;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.AndroidPreferences;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.common.PreferencesFacade;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends ActionBarActivity implements SensorEventListener {

    private static final String STATE_TIME = "time";
    private static final String STATE_POINT = "point";
    private static final String STATE_CURRENTPOINT = "current_point";
    protected MapView mapView;
    protected PreferencesFacade preferencesFacade;
    protected TileCache tileCache;
    private TileDownloadLayer downloadLayer;
    private MyLocationOverlay myLocationOverlay;
    private TextView timerText;
    private List<GeoPoint> listGeoPoints;
    private ScrollView hint_overlay;
    private TextView hint;
    private EditText codeEditText;
    public static final double MINIMUM_DISTANCE_TO_TRIGGER = 20;
    private LayerManager layerManager;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GeoPoint currentPoint;
    protected Location currentLocation;
    private Marker marker;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mNFCTechLists;
    private boolean doubleBackToExitPressedOnce;
    private String user;
    private ImageView btn_myl;
    private MapViewPosition mapViewPosition;
    private String tempo;
    private TextView hint_titolo;
    private int ipoint = 0;
    private boolean hintVisible = false;
    protected boolean toastNearShowed;
    private ImageView point_image;
    private SensorManager mSensorManager;
    private ImageView compass;
    private float currentDegree;
    private TextView points_status;
    private int level;
    private int itime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        level = ((OrientiringApplication) getApplication()).getLevel();

        Bundle extra = getIntent().getExtras();
        if (extra != null)
            user = extra.getString("user");
        else
            user = "";
        SharedPreferences sharedPreferences = this.getSharedPreferences(getPersistableId(), MODE_PRIVATE);
        preferencesFacade = new AndroidPreferences(sharedPreferences);

        MyGeoPoints mgp = new MyGeoPoints();
        listGeoPoints = mgp.getGeoPointsLevel((OrientiringApplication) getApplication());

        init();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                currentLocation = location;
                GeoPoint point = isNearTo(location);
                if (point != null && !toastNearShowed) {
                    toastNearShowed = true;
                    Toast.makeText(MapsActivity.this, R.string.testo_vicino_alpunto, Toast.LENGTH_LONG).show();
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
                if (provider.equals(LocationManager.GPS_PROVIDER))
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

                    builder.setMessage(R.string.dialog_message)
                            .setTitle(R.string.dialog_title).setPositiveButton("OK", null);

                    builder.create().show();
                }
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        btn_myl.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentLocation != null)
                    mapViewPosition.animateTo(new LatLong(currentLocation.getLatitude(),
                            currentLocation.getLongitude()));
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1)
            setNfc();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void setNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            if (!mNfcAdapter.isEnabled()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                AlertDialog dialog;
                builder.setMessage(R.string.dialog_nfc_message)
                        .setTitle(R.string.dialog_nfc_title).setPositiveButton("OK", null)
                        .setPositiveButton("Ok", new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                            }
                        }).setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
            } else {

                // create an intent with tag data and deliver to this activity
                mPendingIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

                // set an intent filter for all MIME data
                IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
                try {
                    ndefIntent.addDataType("*/*");
                    mIntentFilters = new IntentFilter[]{ndefIntent};
                } catch (Exception e) {
                    Log.e("TagDispatch", e.toString());
                }

                mNFCTechLists = new String[][]{new String[]{NfcF.class.getName()}};
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    public void onNewIntent(Intent intent) {
        String s = "";

        // parse through all NDEF messages and their records and pick text type only
        Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (data != null) {
            try {
                for (int i = 0; i < data.length; i++) {
                    NdefRecord[] recs = ((NdefMessage) data[i]).getRecords();
                    for (int j = 0; j < recs.length; j++) {
                        if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
                                Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
                            byte[] payload = recs[j].getPayload();
                            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                            int langCodeLen = payload[0] & 0077;

                            s += new String(payload, langCodeLen + 1, payload.length - langCodeLen - 1,
                                    textEncoding);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("TagDispatch", e.toString());
            }
        }
        if (s.equalsIgnoreCase(currentPoint.getCode())) {
            pointFound();
            codeEditText.setText("");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_TIME, itime);
        outState.putInt(STATE_POINT, ipoint);
        outState.putSerializable(STATE_CURRENTPOINT, currentPoint);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        itime = savedInstanceState.getInt(STATE_TIME);
        ipoint = savedInstanceState.getInt(STATE_POINT);
        currentPoint = (GeoPoint) savedInstanceState.getSerializable(STATE_CURRENTPOINT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myLocationOverlay.disableMyLocation();
        this.mapView.destroy();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        super.onPause();
        if (downloadLayer != null && downloadLayer.isVisible())
            downloadLayer.onPause();
        myLocationOverlay.disableMyLocation();
        locationManager.removeUpdates(locationListener);

        if (mNfcAdapter != null && mNfcAdapter.isEnabled())
            mNfcAdapter.disableForegroundDispatch(this);

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        if (isNetworkAvailable() && downloadLayer != null)
            this.downloadLayer.onResume();
        myLocationOverlay.enableMyLocation(false);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            if (mPendingIntent == null)
                setNfc();
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
        }

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem timerItem = menu.findItem(R.id.break_timer);
        MenuItemCompat.setActionView(timerItem, R.layout.text_timer);
        timerText = (TextView) MenuItemCompat.getActionView(timerItem);

        timerText.setPadding(10, 0, 10, 0); //Or something like that...

        startTimer(); //One tick every second for 30 seconds
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.guida:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.divulgando.collieuganei")));
                break;
            case R.id.bussola:
                CompassActivity.startActivity(this, new Geopoint(currentPoint.getLocation()), currentPoint.getName());
                break;
            case R.id.screen_on:
                if (!item.isChecked()) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    item.setChecked(true);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    item.setChecked(false);
                }
                break;
            case com.lorenzobraghetto.cooptdmorienteeringvillabea.R.id.lock_screen:
                if (!item.isChecked()) {
                    int currentOrientation = getResources().getConfiguration().orientation;
                    if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    }
                    item.setChecked(true);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    item.setChecked(false);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!hintVisible) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.on_back_pressed, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            hideHint();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void addLayers(TileCache tileCache, MapViewPosition mapViewPosition) {
        if (isNetworkAvailable()) {
            downloadLayer = new TileDownloadLayer(this.tileCache,
                    mapViewPosition, OpenStreetMapMapnik.INSTANCE,
                    AndroidGraphicFactory.INSTANCE);
            layerManager.getLayers().add(downloadLayer);
        } else {
            Layer layer = Utils.createTileRendererLayer(tileCache, mapViewPosition, getMapFile());
            layerManager.getLayers().add(layer);
        }

        // a marker to show at the position
        Drawable drawable = getResources().getDrawable(R.drawable.pin);
        Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);

        // create the overlay and tell it to follow the location
        myLocationOverlay = new MyLocationOverlay(this,
                mapViewPosition, bitmap, Utils.createPaint(AndroidGraphicFactory.INSTANCE.createColor(90, 15, 20, 25), 0,
                Style.FILL), null, user);
        myLocationOverlay.setSnapToLocationEnabled(false);

        layerManager.getLayers().add(this.myLocationOverlay);
    }

    /**
     * initializes the map view, here from source
     */
    protected void init() {
        mapView = (MapView) findViewById(R.id.mapview);
        btn_myl = (ImageView) findViewById(R.id.btn_myl);
        hint_overlay = (ScrollView) findViewById(R.id.hint_overlay);
        hint_titolo = (TextView) findViewById(R.id.hint_titolo);
        TextView hint_desc = (TextView) findViewById(R.id.hint_desc);
        hint = (TextView) findViewById(R.id.hint);
        point_image = (ImageView) findViewById(R.id.point_image);
        codeEditText = (EditText) findViewById(R.id.code);
        compass = (ImageView) findViewById(R.id.compass);
        points_status = (TextView) findViewById(R.id.points_status);

        if (level == OrientiringApplication.LEVEL_EXTREME)
            compass.setVisibility(View.GONE);

        if (level == OrientiringApplication.LEVEL_DIFFICULT || level == OrientiringApplication.LEVEL_EXTREME) {
            hint_desc.setText(R.string.hint_desc_diff);
            hint.setVisibility(View.GONE);
        }

        initializeMapView(mapView, preferencesFacade);

        tileCache = createTileCache();

        layerManager = mapView.getLayerManager();

        mapViewPosition = mapView.getModel().mapViewPosition;
        mapViewPosition.setMapPosition(getInitialPosition());

        addLayers(tileCache, mapViewPosition);

        showNextPoint();
    }

    private void showNextPoint() {
        if (ipoint < listGeoPoints.size()) {
            currentPoint = listGeoPoints.get(ipoint);

            if (marker != null)
                layerManager.getLayers().remove(marker);

            Marker markerTemp = Utils.createTappableMarker(this,
                    R.drawable.ic_transit_notice_information, currentPoint.getLatLon());
            marker = new Marker(markerTemp.getLatLong(), markerTemp.getBitmap(), markerTemp.getHorizontalOffset(), markerTemp.getVerticalOffset()) {

                @Override
                public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
                    if (this.contains(layerXY, tapXY))
                        drawHint();
                    return super.onTap(tapLatLong, layerXY, tapXY);
                }
            };

            layerManager.getLayers().add(marker);
            points_status.setText(String.format(getString(R.string.points_status), ipoint + 1 + ""));
            ipoint++;
        } else {
            Intent end = new Intent(this, EndActivity.class);
            end.putExtra("user", user);
            end.putExtra("tempo", tempo);
            end.putExtra("difficolta", ((OrientiringApplication) getApplication()).getLevel());
            finish();
            startActivity(end);
        }
    }

    private TextWatcher codeWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().equalsIgnoreCase(currentPoint.getCode())) {
                pointFound();
                codeEditText.setText("");
            }
        }
    };

    protected void drawHint() {
        hintVisible = true;
        hint_overlay.setVisibility(View.VISIBLE);
        hint_titolo.setText(currentPoint.getName());
        hint.setText(currentPoint.getHint());
        point_image.setImageResource(currentPoint.getImg_id());
        codeEditText.addTextChangedListener(codeWatcher);
    }

    private void hideHint() {
        codeEditText.removeTextChangedListener(codeWatcher);
        hint_overlay.setVisibility(View.GONE);
        hintVisible = false;
        point_image.setImageDrawable(null);
    }

    protected void pointFound() {
        if (toastNearShowed)
            toastNearShowed = false;

        hideHint();
        InputMethodManager mgr = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        mgr.hideSoftInputFromWindow(mapView.getWindowToken(), 0);
        showNextPoint();
        Toast.makeText(this, R.string.codice_esatto, Toast.LENGTH_LONG).show();
    }

    private TileCache createTileCache() {
        return Utils.createExternalStorageTileCache(this, getPersistableId());
    }

    protected MapPosition getInitialPosition() {
        return new MapPosition(new LatLong(45.276956, 11.679168), (byte) 17);
    }

    /**
     * @return a map file
     */
    protected File getMapFile() {
        return new File(getExternalFilesDir(null), this.getMapFileName());
    }

    /**
     * @return the map file name to be used
     */
    protected String getMapFileName() {
        return "monteGemola.map";
    }

    /**
     * @return the id that is used to save this mapview
     */
    protected String getPersistableId() {
        return this.getClass().getSimpleName();
    }

    /**
     * initializes the map view
     *
     * @param mapView the map view
     */
    protected void initializeMapView(MapView mapView, PreferencesFacade preferences) {
        mapView.getModel().init(preferences);
        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
    }

    protected GeoPoint isNearTo(Location location) {
        if (calculationDistance(location, currentPoint.getLat(), currentPoint.getLon()) < MINIMUM_DISTANCE_TO_TRIGGER)
            return currentPoint;
        return null;
    }

    public double calculationDistance(Location current,
                                      double finalLat, double finalLong) {
        Location locationB = new Location("point B");
        locationB.setLatitude(finalLat);
        locationB.setLongitude(finalLong);
        return current.distanceTo(locationB);
    }

    private void startTimer() {
        Timer timer = new Timer(); //timer

        //il metodo run viene eseguito ad ogni scadenza del timer
        timer.scheduleAtFixedRate(
                new TimerTask() {

                    public void run() {
                        itime++;
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                tempo = secondsToString(itime);
                                timerText.setText(tempo);
                            }
                        });
                    }
                },
                0, 1000
        );
    }

    private String secondsToString(int improperSeconds) {

        //Seconds must be fewer than are in a day

        Time secConverter = new Time();

        secConverter.hour = 0;
        secConverter.minute = 0;
        secConverter.second = 0;

        secConverter.second = improperSeconds;
        secConverter.normalize(true);

        String hours = String.valueOf(secConverter.hour);
        String minutes = String.valueOf(secConverter.minute);
        String seconds = String.valueOf(secConverter.second);

        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        if (minutes.length() < 2) {
            minutes = "0" + minutes;
        }
        if (hours.length() < 2) {
            hours = "0" + hours;
        }

        String timeString = hours + ":" + minutes + ":" + seconds;
        return timeString;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
// get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        compass.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
