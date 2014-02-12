package edu.upc.eetac.dsa.dsaqt1314g2.informer.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.upc.eetac.dsa.dsaqt1314g2.informer.android.informer.api.Comentario;

public class ComentarioAdapter extends BaseAdapter {
	//private final static String TAG = ComentarioAdapter.class.toString();
	private ArrayList<Comentario> data;
	private LayoutInflater inflater;
	private Context context;

	public ComentarioAdapter(Context context, ArrayList<Comentario> data) {
		super();
		inflater = LayoutInflater.from(context);
		this.data = data;
		this.context = context;
	}

	private static class ViewHolder { // elementos del View del elemento
		TextView tvUsername;
		TextView tvDate;
		TextView tvContent;
	}

	@Override
	public boolean isEnabled(int position) {
		if (data.get(position).getContenido() == context.getString(R.string.no_hay_comentarios))
			return false;
		return super.isEnabled(position);
	}

	// Metodos explicados en el PDF
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// return Long.parseLong(((Post)getItem(position)).getIdentificador());
		return ((Comentario) getItem(position)).getIdentificador();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) { // cuando no hay nada que reciclar
			convertView = inflater.inflate(R.layout.list_row_comentario, null); // inflamos
			viewHolder = new ViewHolder();
			viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
			viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			convertView.setTag(viewHolder); // le pone el tag a nivel de vista
		} else {
			viewHolder = (ViewHolder) convertView.getTag(); // recupera de la
															// vista qe toca
		}
		String username = data.get(position).getUsername();
		String content = data.get(position).getContenido();// +"("+data.get(position).getIdentificador()+")";
		viewHolder.tvUsername.setText(username);
		viewHolder.tvContent.setText(content);
		if (data.get(position).getPublicacion_date() != null) {
			String date = SimpleDateFormat.getInstance().format(data.get(position).getPublicacion_date());
			viewHolder.tvDate.setText(date);
		}
		return convertView;
	}
}
