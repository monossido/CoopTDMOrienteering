package com.lorenzobraghetto.cacciaaltesoro;

import java.util.ArrayList;
import java.util.List;

public class MyGeoPoints {

	private List<GeoPoints> listGeoPoints = new ArrayList<GeoPoints>();

	public MyGeoPoints() {
		generatePoints();
	}

	private void generatePoints() {
		GeoPoints g1 = new GeoPoints(45.27880, 11.68420, "Prova", "ABCDEF", "Hint prova");
		listGeoPoints.add(g1);
	}

	public List<GeoPoints> getGeoPoints() {
		return listGeoPoints;
	}
}
