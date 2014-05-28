package com.lorenzobraghetto.cacciaaltesoro;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.android.AndroidPreferences;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.layer.MyLocationOverlay;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.common.PreferencesFacade;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.lorenzobraghetto.compasslibrary.Geopoint;

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
	private static final double MINIMUM_DISTANCE_TO_TRIGGER = 50;
	private int i = 0;
	private LayerManager layerManager;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private GeoPoints currentPoint;
	protected Location currentLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maps);

		SharedPreferences sharedPreferences = this.getSharedPreferences(getPersistableId(), MODE_PRIVATE);
		this.preferencesFacade = new AndroidPreferences(sharedPreferences);

		MyGeoPoints mgp = new MyGeoPoints();
		listGeoPoints = mgp.getGeoPoints();

		init();

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				currentLocation = location;
				GeoPoints point = isNearTo(location);
				if (point != null) {
					drawHint(point);
				}
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
				//TODO
			}
		};

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.mapView.destroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//this.mapView.getModel().save(this.preferencesFacade);
		//this.preferencesFacade.save();
		if (isNetworkAvailable())
			this.downloadLayer.onPause();
		myLocationOverlay.disableMyLocation();
		locationManager.removeUpdates(locationListener);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isNetworkAvailable())
			this.downloadLayer.onResume();
		myLocationOverlay.enableMyLocation(false);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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
			CompassActivity.startActivity(this, "asd", "asdlol", new Geopoint(currentPoint.getLocation()), null, "info");
			break;
		}

		return true;
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
				mapViewPosition, bitmap);
		myLocationOverlay.setSnapToLocationEnabled(false);
		layerManager.getLayers().add(this.myLocationOverlay);
	}

	/**
	 * initializes the map view, here from source
	 */
	protected void init() {
		this.mapView = (MapView) findViewById(R.id.mapview);
		hint_overlay = (ScrollView) findViewById(R.id.hint_overlay);
		hint = (TextView) findViewById(R.id.hint);
		codeEditText = (EditText) findViewById(R.id.code);

		initializeMapView(this.mapView, this.preferencesFacade);

		this.tileCache = createTileCache();

		layerManager = mapView.getLayerManager();

		MapViewPosition mapViewPosition = this.initializePosition(this.mapView.getModel().mapViewPosition);

		addLayers(this.tileCache, mapViewPosition);

		showNextPoint();
	}

	private void showNextPoint() {
		if (i < listGeoPoints.size()) {
			currentPoint = listGeoPoints.get(i);
			Marker marker1 = Utils.createTappableMarker(this,
					R.drawable.ic_transit_notice_information, currentPoint.getLatLon());
			layerManager.getLayers().add(marker1);
			i++;
		}
		//else TODO
	}

	protected void drawHint(final GeoPoints geoPoint) {
		hint_overlay.setVisibility(View.VISIBLE);
		hint.setText(geoPoint.getHint());
		codeEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().equalsIgnoreCase(geoPoint.getCode()))
					pointFound();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	protected void pointFound() {
		//TODO
		hint_overlay.setVisibility(View.GONE);
		InputMethodManager mgr = (InputMethodManager)
				getSystemService(Context.INPUT_METHOD_SERVICE);

		mgr.hideSoftInputFromWindow(mapView.getWindowToken(), 0);
		showNextPoint();
	}

	private TileCache createTileCache() {
		return Utils.createExternalStorageTileCache(this, getPersistableId());
	}

	protected MapPosition getInitialPosition() {
		return new MapPosition(new LatLong(45.27880, 11.68420), (byte) 15);
	}

	/**
	 * @return a map file
	 */
	protected File getMapFile() {
		return new File(Environment.getExternalStorageDirectory(), this.getMapFileName());
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

	/**
	 * initializes the map view position
	 * 
	 * @param mapViewPosition
	 *            the map view position to be set
	 * @return the mapviewposition set
	 */
	protected MapViewPosition initializePosition(MapViewPosition mapViewPosition) {
		LatLong center = mapViewPosition.getCenter();

		//if (center.equals(new LatLong(0, 0))) {
		mapViewPosition.setMapPosition(this.getInitialPosition());
		//}
		return mapViewPosition;
	}

	protected GeoPoints isNearTo(Location location) {
		for (GeoPoints geoPoint : listGeoPoints) {
			if (calculationDistance(location, geoPoint.getLat(), geoPoint.getLon()) < MINIMUM_DISTANCE_TO_TRIGGER)
				return geoPoint;
		}
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
								timerText.setText(secondsToString(i));
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
