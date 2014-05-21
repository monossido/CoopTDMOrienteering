package com.lorenzobraghetto.cacciaaltesoro;

import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class CompassActivity extends SherlockActivity {

	private static final int COORDINATES_OFFSET = 10;

	protected TextView navType;
	protected TextView navAccuracy;
	protected TextView navSatellites;
	protected TextView navLocation;
	protected TextView distanceView;
	protected TextView headingView;
	protected CompassView compassView;
	protected TextView destinationTextView;
	protected TextView cacheInfoView;

	private static final String EXTRAS_COORDS = "coords";
	private static final String EXTRAS_NAME = "name";
	private static final String EXTRAS_GEOCODE = "geocode";
	private static final String EXTRAS_CACHE_INFO = "cacheinfo";
	//private static final List<IWaypoint> coordinates = new ArrayList<IWaypoint>();

	/**
	 * Destination of the compass, or null (if the compass is used for a
	 * waypoint only).
	 */
	Location cache = null;
	private Location dstCoords = null;
	private float cacheHeading = 0;
	private String title = null;
	private String info = null;
	private boolean hasMagneticFieldSensor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compass_activity);

		navType = (TextView) findViewById(R.id.nav_type);
		navAccuracy = (TextView) findViewById(R.id.nav_accuracy);
		navSatellites = (TextView) findViewById(R.id.nav_satellites);
		navLocation = (TextView) findViewById(R.id.nav_location);
		distanceView = (TextView) findViewById(R.id.distance);
		headingView = (TextView) findViewById(R.id.heading);
		compassView = (CompassView) findViewById(R.id.rose);
		destinationTextView = (TextView) findViewById(R.id.destination);
		cacheInfoView = (TextView) findViewById(R.id.cacheinfo);

		final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		hasMagneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null;
		if (!hasMagneticFieldSensor) {
			//	Settings.setUseCompass(false);
		}

		// get parameters
		Bundle extras = getIntent().getExtras();
		//if (extras != null) {
		//final String geocode = extras.getString(EXTRAS_GEOCODE);
		//if (StringUtils.isNotEmpty(geocode)) {
		//	cache = DataStore.loadCache(geocode, LoadFlags.LOAD_CACHE_OR_DB);
		//}
		//TODO
		cache = new Location("myprovider");
		title = "prova";

		//dstCoords = extras.getParcelable(EXTRAS_COORDS);
		dstCoords = new Location("myprovider");
		//info = extras.getString(EXTRAS_CACHE_INFO);
		info = "asd";

		//if (StringUtils.isNotBlank(name)) {
		//	if (StringUtils.isNotBlank(title)) {
		//		title += ": " + name;
		//	} else {
		//		title = name;
		//	}
		//}
		//} else {
		//	Intent pointIntent = new Intent(this, NavigateAnyPointActivity.class);
		//	startActivity(pointIntent);

		//	finish();
		//	return;
		//}

		// set header
		setTitle();
		setDestCoords();
		setCacheInfo();

		// make sure we can control the TTS volume
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	public void onResume() {
		//super.onResume(geoDirHandler.start(GeoDirHandler.UPDATE_GEODIR));
		super.onResume();
	}

	@Override
	public void onDestroy() {
		compassView.destroyDrawingCache();
		//SpeechService.stopService(this);
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		setContentView(R.layout.compass_activity);

		setTitle();
		setDestCoords();
		setCacheInfo();

		// Force a refresh of location and direction when data is available.
		//final CgeoApplication app = CgeoApplication.getInstance();
		//final IGeoData geo = app.currentGeo();
		//if (geo != null) {
		//	geoDirHandler.updateGeoDir(geo, app.currentDirection());
		//}
	}

	private void setTitle() {
		if (title.length() > 0) {
			setTitle(title);
		} else {
			setTitle("Prova");
		}
	}

	private void setDestCoords() {
		if (dstCoords == null) {
			return;
		}

		destinationTextView.setText(dstCoords.toString());
	}

	private void setCacheInfo() {
		if (info == null) {
			cacheInfoView.setVisibility(View.GONE);
			return;
		}
		cacheInfoView.setVisibility(View.VISIBLE);
		cacheInfoView.setText(info);
	}

	private void updateDistanceInfo(final Location geo) {
		if (geo.getLatitude() == 0.0 || dstCoords == null) {
			return;
		}

		//distanceView.setText(getDistanceFromKilometers(geo.getCoords().distanceTo(dstCoords)));
		//TODO
	}

	//	private GeoDirHandler geoDirHandler = new GeoDirHandler() {
	//TODO

	private void updateNorthHeading(final float northHeading) {
		if (compassView != null) {
			compassView.updateNorth(northHeading, cacheHeading);
		}
	}

	public static String getDistanceFromKilometers(final Float distanceKilometers) {
		if (distanceKilometers == null) {
			return "?";
		}

		final Object[] scaled = scaleDistance(distanceKilometers);
		String formatString;
		if ((Double) scaled[0] >= 100) {
			formatString = "%.0f";
		} else if ((Double) scaled[0] >= 10) {
			formatString = "%.1f";
		} else {
			formatString = "%.2f";
		}

		return String.format(formatString + " %s", scaled[0], scaled[1]);
	}

	public static Object[] scaleDistance(final double distanceKilometers) {
		double distance;
		String units;

		if (distanceKilometers >= 1) {
			distance = distanceKilometers;
			units = "km";
		} else {
			distance = distanceKilometers * 1000;
			units = "m";
		}

		Object[] objects = new Object[2];
		objects[0] = distance;
		objects[1] = units;

		return objects;
	}

}
