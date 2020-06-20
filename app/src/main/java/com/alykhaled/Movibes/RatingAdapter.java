package com.alykhaled.Movibes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ExampleViewHolder> {
    private Context mContext;
    private ArrayList<RaingDetails> mExampleList;

    public RatingAdapter(Context context, ArrayList<RaingDetails> exampleList) {
        mContext = context;
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.rating, parent, false);
        return new ExampleViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        RaingDetails currentItem = mExampleList.get(position);
        String source = currentItem.getSource();
        String value = currentItem.getValue();
        holder.mRateSource.setText(source);
        holder.mRateValue.setText(value);
    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView mRateSource;
        public TextView mRateValue;
        public ExampleViewHolder(View itemView) {
            super(itemView);
            mRateSource = itemView.findViewById(R.id.ratingSource);
            mRateValue = itemView.findViewById(R.id.rateValue);

        }
    }
}