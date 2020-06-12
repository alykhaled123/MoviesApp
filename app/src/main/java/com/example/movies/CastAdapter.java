package com.example.movies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.ExampleViewHolder> {
    private Context mContext;
    private ArrayList<CastMember> mExampleList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public CastAdapter(Context context, ArrayList<CastMember> exampleList) {
        mContext = context;
        mExampleList = exampleList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cast_item, parent, false);
        return new ExampleViewHolder(v);
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        CastMember currentItem = mExampleList.get(position);
        String posterurl = currentItem.getPic();
        String castName= currentItem.getName();
        String characterName = currentItem.getCharacter();
        holder.mCastName.setText(castName);
        holder.mCharacterName.setText(characterName);
        Picasso.get().load(posterurl).fit().centerInside().into(holder.mCastPic);
        //Picasso.get().load(backurl).fit().centerInside().into(holder.mMovieBack);
//j
    }
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mCastPic;
        public TextView mCastName;
        public TextView mCharacterName;
        public ExampleViewHolder(View itemView) {
            super(itemView);
            mCastPic = itemView.findViewById(R.id.cast_pic);
            mCastName = itemView.findViewById(R.id.cast_name);
            mCharacterName = itemView.findViewById(R.id.character_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}