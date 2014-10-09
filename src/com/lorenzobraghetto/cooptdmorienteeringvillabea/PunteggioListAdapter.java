package com.lorenzobraghetto.cooptdmorienteeringvillabea;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PunteggioListAdapter extends BaseAdapter {

	private List<Punteggio> mPunteggi;
	private Context mContext;
	private LayoutInflater mInflater;

	public PunteggioListAdapter(List<Punteggio> punteggi, Context context) {
		this.mPunteggi = punteggi;
		this.mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mPunteggi.size();
	}

	@Override
	public Object getItem(int position) {
		return mPunteggi.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.rawpunteggi, null);
			viewHolder = new ViewHolder();
			viewHolder.nome = (TextView) convertView.findViewById(R.id.user_name);
			viewHolder.tempo = (TextView) convertView.findViewById(R.id.tempo);
			viewHolder.difficolta = (TextView) convertView.findViewById(R.id.difficolta);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final Punteggio punteggio = (Punteggio) getItem(position);

		viewHolder.nome.setText(punteggio.getmNome());
		viewHolder.tempo.setText(punteggio.getmTempo());
		viewHolder.difficolta.setText(punteggio.getmDifficolta());

		return convertView;
	}

	private class ViewHolder {
		public TextView nome;
		public TextView tempo;
		public TextView difficolta;
	}

}
