package com.lorenzobraghetto.cacciaaltesoro;

import java.util.ArrayList;
import java.util.List;

public class MyGeoPoints {

	private List<GeoPoints> listGeoPoints = new ArrayList<GeoPoints>();

	public MyGeoPoints() {
		generatePoints();
	}

	private void generatePoints() {
		listGeoPoints.add(new GeoPoints(45.276158, 11.677881, "Punto 3", "ABCDEF", "Hint prova"));
		listGeoPoints.add(new GeoPoints(45.276932, 11.678822, "Punto 1", "ABCDEF", "Hint prova"));
		listGeoPoints.add(new GeoPoints(45.277548, 11.679278, "Punto 10", "ABCDEF", "Hint prova"));
		listGeoPoints.add(new GeoPoints(45.276544, 11.677841, "Punto 2", "ABCDEF", "Hint prova"));
		listGeoPoints.add(new GeoPoints(45.274488, 11.676528, "Punto 4", "ABCDEF", "Hint prova"));
		listGeoPoints.add(new GeoPoints(45.275056, 11.677543, "Punto 5", "ABCDEF", "Hint prova"));
		listGeoPoints.add(new GeoPoints(45.276198, 11.679078, "Punto 6", "ABCDEF", "Hint prova"));
		listGeoPoints.add(new GeoPoints(45.276891, 11.680513, "Punto 7", "ABCDEF", "Hint prova"));
		listGeoPoints.add(new GeoPoints(45.278083, 11.680331, "Punto 8", "ABCDEF", "Hint prova"));
		listGeoPoints.add(new GeoPoints(45.277826, 11.679177, "Punto 9", "ABCDEF", "Hint prova"));
		//listGeoPoints.add(new GeoPoints(45.276956, 11.679168, "INFO", "ABCDEF", "Hint prova"));
	}

	public List<GeoPoints> getGeoPoints() {
		return listGeoPoints;
	}
}
