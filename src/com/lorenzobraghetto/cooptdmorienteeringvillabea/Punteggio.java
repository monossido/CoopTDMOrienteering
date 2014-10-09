package com.lorenzobraghetto.cooptdmorienteeringvillabea;

public class Punteggio {

	private String mNome;
	private String mTempo;
	private String mDifficolta;

	public Punteggio(String nome, String tempo, String difficolta) {
		this.mNome = nome;
		this.mTempo = tempo;
		this.mDifficolta = difficolta;
	}

	public String getmNome() {
		return mNome;
	}

	public void setmNome(String mNome) {
		this.mNome = mNome;
	}

	public String getmTempo() {
		return mTempo;
	}

	public void setmTempo(String mTempo) {
		this.mTempo = mTempo;
	}

	public String getmDifficolta() {
		return mDifficolta;
	}

	public void setmDifficolta(String mDifficolta) {
		this.mDifficolta = mDifficolta;
	}
}
