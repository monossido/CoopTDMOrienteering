package com.lorenzobraghetto.cooptdmorientiringvillabea;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lorenzobraghetto.compasslibrary.Geopoint;
import com.lorenzobraghetto.utils.MyLocationOverlay;

public class MapsActivity extends SherlockActivity {

	protected MapView mapView;
	protected PreferencesFacade preferencesFacade;
	protected TileCache tileCache;
	private TileDownloadLayer downloadLayer;
	private MyLocationOverlay myLocationOverlay;
	private TextView timerText;
	private List<GeoPoints> listGeoPoints;
	private ScrollView hint_overlay;
	private TextView hint;
	private EditText codeEditText;
	public static final double MINIMUM_DISTANCE_TO_TRIGGER = 20;
	private int i = 0;
	private LayerManager layerManager;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private GeoPoints currentPoint;
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
	private boolean hintVisible = false;
	protected boolean toastNearShowed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);

		Bundle extra = getIntent().getExtras();
		if (extra != null)
			user = extra.getString("user");
		else
			user = "";
		SharedPreferences sharedPreferences = this.getSharedPreferences(getPersistableId(), MODE_PRIVATE);
		this.preferencesFacade = new AndroidPreferences(sharedPreferences);

		MyGeoPoints mgp = new MyGeoPoints();
		listGeoPoints = mgp.getGeoPointsLevel((OrientiringApplication) getApplication());

		init();

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {

			public void onLocationChanged(Location location) {
				currentLocation = location;
				GeoPoints point = isNearTo(location);
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

		btn_myl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (currentLocation != null)
					mapViewPosition.animateTo(new LatLong(currentLocation.getLatitude(),
							currentLocation.getLongitude()));
			}
		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1)
			setNfc();
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	private void setNfc() {
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter != null) {
			// create an intent with tag data and deliver to this activity
			mPendingIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

			// set an intent filter for all MIME data
			IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
			try {
				ndefIntent.addDataType("*/*");
				mIntentFilters = new IntentFilter[] { ndefIntent };
			} catch (Exception e) {
				Log.e("TagDispatch", e.toString());
			}

			mNFCTechLists = new String[][] { new String[] { NfcF.class.getName() } };
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
	protected void onDestroy() {
		super.onDestroy();
		this.mapView.destroy();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onPause() {
		super.onPause();
		//this.mapView.getModel().save(this.preferencesFacade);
		//this.preferencesFacade.save();
		if (downloadLayer.isVisible())
			this.downloadLayer.onPause();
		myLocationOverlay.disableMyLocation();
		locationManager.removeUpdates(locationListener);

		if (mNfcAdapter != null)
			mNfcAdapter.disableForegroundDispatch(this);
	}

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		super.onResume();
		if (isNetworkAvailable())
			this.downloadLayer.onResume();
		myLocationOverlay.enableMyLocation(false);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		if (mNfcAdapter != null)
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);

		MenuItem timerItem = menu.findItem(R.id.break_timer);
		timerText = (TextView) timerItem.getActionView();

		timerText.setPadding(10, 0, 10, 0); //Or something like that...

		startTimer(30000, 1000); //One tick every second for 30 seconds
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
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
		}

		return true;
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
		downloadLayer = new TileDownloadLayer(this.tileCache,
				mapViewPosition, OpenStreetMapMapnik.INSTANCE,
				AndroidGraphicFactory.INSTANCE);
		if (isNetworkAvailable())
			layerManager.getLayers().add(downloadLayer);
		else {
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
		hint = (TextView) findViewById(R.id.hint);
		codeEditText = (EditText) findViewById(R.id.code);

		initializeMapView(mapView, preferencesFacade);

		tileCache = createTileCache();

		layerManager = mapView.getLayerManager();

		mapViewPosition = mapView.getModel().mapViewPosition;
		mapViewPosition.setMapPosition(getInitialPosition());

		addLayers(tileCache, mapViewPosition);

		showNextPoint();
	}

	private void showNextPoint() {
		if (i < listGeoPoints.size()) {
			currentPoint = listGeoPoints.get(i);

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
			i++;
		} else {
			Intent end = new Intent(this, EndActivity.class);
			end.putExtra("user", user);
			end.putExtra("tempo", tempo);
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
		codeEditText.addTextChangedListener(codeWatcher);
	}

	private void hideHint() {
		codeEditText.removeTextChangedListener(codeWatcher);
		hint_overlay.setVisibility(View.GONE);
		hintVisible = false;
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
	 * @param mapView
	 *            the map view
	 */
	protected void initializeMapView(MapView mapView, PreferencesFacade preferences) {
		mapView.getModel().init(preferences);
		mapView.setClickable(true);
		mapView.getMapScaleBar().setVisible(true);
	}

	protected GeoPoints isNearTo(Location location) {
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

	private void startTimer(long duration, long interval) {
		Timer timer = new Timer(); //timer

		//il metodo run viene eseguito ad ogni scadenza del timer
		timer.scheduleAtFixedRate(
				new TimerTask() {
					int i = 0;

					public void run() {
						i++;
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								tempo = secondsToString(i);
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

}
