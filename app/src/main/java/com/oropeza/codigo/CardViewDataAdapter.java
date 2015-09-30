package com.oropeza.codigo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oropeza.chatsafe.R;
import com.oropeza.utilidades.ImageUtil;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;

public class CardViewDataAdapter extends RecyclerView.Adapter<CardViewDataAdapter.ViewHolder>{

	private ArrayList<ItemContacto> dataSource;
	static OnItemClickListener mItemClickListener;

	public CardViewDataAdapter(ArrayList<ItemContacto> dataArgs){
		dataSource = dataArgs;
		Log.i("Bandera","New Adapter");
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_row, parent, false);
		Log.i("Bandera","Inflando");

		ViewHolder viewHolder = new ViewHolder(view);

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.textView.setText(dataSource.get(position).getNombre());
		holder.textView2.setText(dataSource.get(position).getMail());

		holder.imageView.setImageBitmap(ImageUtil.getInstance().getBitmap(dataSource.get(position).getImagenString()));
	}

	@Override
	public int getItemCount() {
		//Log.i("Bandera","Size");
		return dataSource.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		protected TextView textView;
		protected TextView textView2;
		protected CircularImageView imageView;

		public ViewHolder(View itemView) {
			super(itemView);
			textView =  (TextView) itemView.findViewById(R.id.nomcontacto);
			textView2 = (TextView) itemView.findViewById(R.id.telcontacto);
			imageView = (CircularImageView)itemView.findViewById(R.id.imagencontacto);

			itemView.setOnClickListener(this);

		}

		@Override
		public void onClick(View v) {
			if (mItemClickListener != null) {
				mItemClickListener.onItemClick(v, getPosition());
			}
		}

	}

	public interface OnItemClickListener {
		public void onItemClick(View view, int position);
	}

	public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
		this.mItemClickListener = mItemClickListener;
	}

	}
