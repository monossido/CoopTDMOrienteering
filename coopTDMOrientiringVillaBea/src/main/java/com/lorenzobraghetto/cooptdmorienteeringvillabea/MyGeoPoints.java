package com.lorenzobraghetto.cooptdmorienteeringvillabea;

import java.util.ArrayList;
import java.util.List;

public class MyGeoPoints {

    private List<GeoPoint> listEasyGeoPoints = new ArrayList<GeoPoint>();
    private List<GeoPoint> listDifficultGeoPoints = new ArrayList<GeoPoint>();

    public MyGeoPoints() {
        generateEasyPoints();
        generateDifficultPoints();
    }

    private void generateEasyPoints() {
        listEasyGeoPoints.add(new GeoPoint(45.276892, 11.678752, "Punto 1", "GUR4V", "Serve per la raccolta del rifiuto secco", R.drawable.cestino));
        listEasyGeoPoints.add(new GeoPoint(45.276544, 11.677841, "Punto 2", "HO07E", "Il cinghiale è ghiotto della faggiola, il frutto dell’albero che state cercando, che si trova anche in montagna", R.drawable.faggio));
        listEasyGeoPoints.add(new GeoPoint(45.276158, 11.677881, "Punto 3", "NNV4Q", "Il legno di questo albero è pregiato, con i noccioli del frutto si possono fare dei cuscinetti da scaldare in microonde", R.drawable.ciliegio));
        listEasyGeoPoints.add(new GeoPoint(45.274488, 11.676528, "Punto 4", "N07E9", "Forse ci sei proprio vicino, il punto si trova sotto a un albero chiamato...", R.drawable.pino));
        listEasyGeoPoints.add(new GeoPoint(45.275006, 11.677593, "Punto 5", "QYQGG", "Grazie al suo frutto si fa un ottimo prodotto per condire la verdura", R.drawable.olivo));
        listEasyGeoPoints.add(new GeoPoint(45.276198, 11.679078, "Punto 6", "T3QDW", "Il frutto dell’albero che state cercando è la ghianda", R.drawable.quercia));
        listEasyGeoPoints.add(new GeoPoint(45.276891, 11.680513, "Punto 7", "YHO7S", "Se la strada seguirai, il cartello troverai e sotto una quercia sostare dovrai", R.drawable.quercia));
        listEasyGeoPoints.add(new GeoPoint(45.278083, 11.680331, "Punto 8", "YTOPL", "Lo so che non ha un bell’aspetto, ma si tratta di un pozzetto", R.drawable.pozzetto));
        listEasyGeoPoints.add(new GeoPoint(45.277826, 11.679177, "Punto 9", "02NY7", "Attenzione alla strada accidentata, il punto si trova vicino una staccionata", R.drawable.staccionata));
        listEasyGeoPoints.add(new GeoPoint(45.277508, 11.679298, "Punto 10", "3TCT4", "Dai che alla fine sei arrivato ormai, su una porta in legno cercare dovrai", R.drawable.porta));
    }

    private void generateDifficultPoints() {
        listDifficultGeoPoints.add(new GeoPoint(45.276340, 11.678745, "Punto 1", "1LDPY", "", R.drawable.cipresso));
        listDifficultGeoPoints.add(new GeoPoint(45.276544, 11.677841, "Punto 2", "HO07E", "", R.drawable.faggio));
        listDifficultGeoPoints.add(new GeoPoint(45.274477, 11.676543, "Punto 3", "N07E9", "", R.drawable.pino));
        listDifficultGeoPoints.add(new GeoPoint(45.275013, 11.677582, "Punto 4", "QYQGG", "", R.drawable.olivo));
        listDifficultGeoPoints.add(new GeoPoint(45.276184, 11.679080, "Punto 5", "T3QDW", "", R.drawable.quercia));
        listDifficultGeoPoints.add(new GeoPoint(45.276356, 11.679946, "Punto 6", "UXHQ1", "", R.drawable.sambuco));
        listDifficultGeoPoints.add(new GeoPoint(45.278064, 11.681441, "Punto 7", "K8E6E", "", R.drawable.robinia));
        listDifficultGeoPoints.add(new GeoPoint(45.278253, 11.681499, "Punto 8", "TU79Z", "", R.drawable.orniello));
        listDifficultGeoPoints.add(new GeoPoint(45.278, 11.6791, "Punto 9", "UCKNS", "", R.drawable.olmo));//TODO
        listDifficultGeoPoints.add(new GeoPoint(45.277497, 11.679256, "Punto 10", "3TCT4", "", R.drawable.porta));
    }

    public List<GeoPoint> getGeoPointsLevel(OrientiringApplication app) {
        if (app.getLevel() == 0)
            return listEasyGeoPoints;
        else
            return listDifficultGeoPoints;
    }
}
