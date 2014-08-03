package com.lorenzobraghetto.cooptdmorienteeringvillabea;

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

		listEasyGeoPoints.add(new GeoPoints(45.276892, 11.678752, "Punto 1", "GUR4V", "Serve per la raccolta del rifiuto secco"));
		listEasyGeoPoints.add(new GeoPoints(45.276544, 11.677841, "Punto 2", "HO07E", "Il cinghiale è ghiotto della faggiola, il frutto dell’albero che state cercando, che si trova anche in montagna"));
		listEasyGeoPoints.add(new GeoPoints(45.276158, 11.677881, "Punto 3", "NNV4Q", "Il legno di questo albero è pregiato, con i noccioli del frutto si possono fare dei cuscinetti da scaldare in microonde"));
		listEasyGeoPoints.add(new GeoPoints(45.274488, 11.676528, "Punto 4", "N07E9", "Forse ci sei proprio vicino, il punto si trova sotto a un albero chiamato..."));
		listEasyGeoPoints.add(new GeoPoints(45.275006, 11.677593, "Punto 5", "QYQGG", "Grazie al suo frutto si fa un ottimo prodotto per condire la verdura"));
		listEasyGeoPoints.add(new GeoPoints(45.276198, 11.679078, "Punto 6", "T3QDW", "Il frutto dell’albero che state cercando è la ghianda"));
		listEasyGeoPoints.add(new GeoPoints(45.276891, 11.680513, "Punto 7", "YHO7S", "Se la strada seguirai, il cartello troverai e sotto una quercia sostare dovrai"));
		listEasyGeoPoints.add(new GeoPoints(45.278083, 11.680331, "Punto 8", "YTOPL", "Lo so che non ha un bell’aspetto, ma si tratta di un pozzetto"));
		listEasyGeoPoints.add(new GeoPoints(45.277826, 11.679177, "Punto 9", "02NY7", "Attenzione alla strada accidentata, il punto si trova vicino una staccionata"));
		listEasyGeoPoints.add(new GeoPoints(45.277508, 11.679298, "Punto 10", "3TCT4", "Dai che alla fine sei arrivato ormai, su una porta in legno cercare dovrai"));
		//listGeoPoints.add(new GeoPoints(45.276956, 11.679168, "INFO", "ABCDEF", "Hint prova"));
	}

	private void generateDifficultPoints() {
		listDifficultGeoPoints.add(new GeoPoints(45.276340, 11.678745, "Punto 1", "1LDPY", "Cipresso"));
		listDifficultGeoPoints.add(new GeoPoints(45.276294, 11.677632, "Punto 2", "HO07E", "Faggio"));
		listDifficultGeoPoints.add(new GeoPoints(45.274477, 11.676543, "Punto 3", "N07E9", "Pino"));
		listDifficultGeoPoints.add(new GeoPoints(45.275013, 11.677582, "Punto 4", "QYQGG", "Olivo"));
		listDifficultGeoPoints.add(new GeoPoints(45.276184, 11.679080, "Punto 5", "T3QDW", "Quercia"));
		listDifficultGeoPoints.add(new GeoPoints(45.276356, 11.679946, "Punto 6", "UXHQ1", "Cancello"));
		listDifficultGeoPoints.add(new GeoPoints(45.278064, 11.681441, "Punto 7", "K8E6E", "Robinia"));
		listDifficultGeoPoints.add(new GeoPoints(45.278253, 11.681499, "Punto 8", "TU79Z", "Frassino"));
		listDifficultGeoPoints.add(new GeoPoints(45.277950, 11.679360, "Punto 9", "UCKNS", "Olmo"));
		listDifficultGeoPoints.add(new GeoPoints(45.277497, 11.679256, "Punto 10", "3TCT4", "Porta"));

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
