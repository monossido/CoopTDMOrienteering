package com.lorenzobraghetto.cooptdmorientiringvillabea;

import java.util.ArrayList;
import java.util.List;

public class MyGeoPoints {

	private List<GeoPoints> listEasyGeoPoints = new ArrayList<GeoPoints>();
	private List<GeoPoints> listDifficultGeoPoints = new ArrayList<GeoPoints>();

	public MyGeoPoints() {
		generateEasyPoints();
		generateDifficultPoints();
	}

	private void generateEasyPoints() {
		//listEasyGeoPoints.add(new GeoPoints(45.408999, 11.867802, "Punto casa1", "ABCDEF", "Hint prova casa"));

		listEasyGeoPoints.add(new GeoPoints(45.276932, 11.678822, "Punto 1", "GUR4V", "Non può mancare in un pic-nic"));
		listEasyGeoPoints.add(new GeoPoints(45.276544, 11.677841, "Punto 2", "HO07E", "Il cinghiale è ghiotto del frutto di questa pianta che si chiama faggiola"));
		listEasyGeoPoints.add(new GeoPoints(45.276158, 11.677881, "Punto 3", "NNV4Q", "Il legno di questo albero è pregiato, con i noccioli del frutto si possono fare dei cuscinetti da scaldare in microonde"));
		listEasyGeoPoints.add(new GeoPoints(45.274488, 11.676528, "Punto 4", "N07E9", "Forse ci sei proprio vicino, il punto si trova sotto a un..."));
		listEasyGeoPoints.add(new GeoPoints(45.275056, 11.677543, "Punto 5", "QYQGG", "Grazie al suo frutto si fa un ottimo prodotto per condire la verdura"));
		listEasyGeoPoints.add(new GeoPoints(45.276198, 11.679078, "Punto 6", "T3QDW", "Il frutto di quest’albero è la ghianda"));
		listEasyGeoPoints.add(new GeoPoints(45.276891, 11.680513, "Punto 7", "YHO7S", "Se la strada seguirai, il cartello troverai e sotto una quercia sostare dovrai"));
		listEasyGeoPoints.add(new GeoPoints(45.278083, 11.680331, "Punto 8", "YTOPL", "Lo so che non ha un bell’aspetto, ma si tratta di un pozzetto"));
		listEasyGeoPoints.add(new GeoPoints(45.277826, 11.679177, "Punto 9", "02NY7", "Attenzione alla strada accidentata, il punto si trova su una staccionata"));
		listEasyGeoPoints.add(new GeoPoints(45.277548, 11.679278, "Punto 10", "3TCT4", "Dai che alla fine sei arrivato ormai, su una porta cercare dovrai"));
		//listGeoPoints.add(new GeoPoints(45.276956, 11.679168, "INFO", "ABCDEF", "Hint prova"));
	}

	private void generateDifficultPoints() {
		listDifficultGeoPoints.add(new GeoPoints(45.276340, 11.678745, "Punto 1", "1LDPY", "Cerca il cipresso"));
		listDifficultGeoPoints.add(new GeoPoints(45.276294, 11.677632, "Punto 2", "HO07E", "Cerca il faggio"));
		listDifficultGeoPoints.add(new GeoPoints(45.274477, 11.676543, "Punto 3", "N07E9", "Cerca il pino"));
		listDifficultGeoPoints.add(new GeoPoints(45.275013, 11.677582, "Punto 4", "QYQGG", "Cerca l'olivo"));
		listDifficultGeoPoints.add(new GeoPoints(45.276184, 11.679080, "Punto 5", "T3QDW", "Cerca la quercia"));
		listDifficultGeoPoints.add(new GeoPoints(45.276356, 11.679946, "Punto 6", "UXHQ1", "Cerca il cancello"));
		listDifficultGeoPoints.add(new GeoPoints(45.278064, 11.681441, "Punto 7", "K8E6E", "Cerca la robinia"));
		listDifficultGeoPoints.add(new GeoPoints(45.278253, 11.681499, "Punto 8", "TU79Z", "Cerca il frassino"));
		listDifficultGeoPoints.add(new GeoPoints(45.277950, 11.679360, "Punto 9", "UCKNS", "Cerca l'olmo"));
		listDifficultGeoPoints.add(new GeoPoints(45.277497, 11.679256, "Punto 10", "3TCT4", "Cerca la porta"));

		//listGeoPoints.add(new GeoPoints(45.276956, 11.679168, "INFO", "ABCDEF", "Hint prova"));
	}

	public List<GeoPoints> getEasyGeoPoints() {
		return listEasyGeoPoints;
	}

	public List<GeoPoints> getDifficultGeoPoints() {
		return listDifficultGeoPoints;
	}

	public List<GeoPoints> getGeoPointsLevel(OrientiringApplication app) {
		if (app.getLevel() == 0)
			return listEasyGeoPoints;
		else
			return listDifficultGeoPoints;
	}
}
