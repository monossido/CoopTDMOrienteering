package com.lorenzobraghetto.cooptdmorienteeringvillabea;

public class Punteggio {

    private String mTempoRisultante;
    private String mNome;
    private String mTempo;
    private String mDifficolta;

    public Punteggio(String nome, String tempo, String difficolta, String tempoRisultante) {
        this.mNome = nome;
        this.mTempo = tempo;
        this.mDifficolta = difficolta;
        this.mTempoRisultante = tempoRisultante;
    }

    public String getmNome() {
        return mNome;
    }

    public String getmTempo() {
        return mTempo;
    }

    public String getmDifficolta() {
        return mDifficolta;
    }

    public String getmTempoRisultante() {
        return mTempoRisultante;
    }
}
