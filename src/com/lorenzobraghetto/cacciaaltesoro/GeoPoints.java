package com.lorenzobraghetto.cacciaaltesoro;

import org.mapsforge.core.model.LatLong;

public class GeoPoints {

	private double lat;
	private double lon;
	private String name;
	private String hint;
	private String code;
	private int position;

	public GeoPoints(double lat, double lon, String name, String code, String hint) {
		this.lat = lat;
		this.lon = lon;
		this.name = name;
		this.code = code;
		this.hint = hint;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public String getName() {
		return name;
	}

	public String getHint() {
		return hint;
	}

	public boolean hasHint() {
		return hint.length() != 0;
	}

	public String getCode() {
		return code;
	}

	public LatLong getLatLon() {
		return new LatLong(lat, lon);
	}

}
