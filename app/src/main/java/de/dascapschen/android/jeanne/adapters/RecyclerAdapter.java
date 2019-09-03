package de.dascapschen.android.jeanne.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;

public abstract class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder>
{
    Context mContext;
    ArrayList<Integer> mData;
    boolean endPadding;

    private OnItemClickListener mListener;

    public RecyclerAdapter(Context context, OnItemClickListener listener, ArrayList<Integer> data, boolean useEndPadding)
    {
        mContext = context;
        mListener = listener;
        mData = data;
        endPadding = useEndPadding;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(endPadding && position >= getItemCount()-1)
            return 1;
        else
            return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        View view;
        ViewHolder holder;

        if( viewType == 1 )
        {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_end_padding, viewGroup, false);
            holder = new ViewHolder(view, null);
        }
        else
        {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_item, viewGroup, false);

            holder = new ViewHolder(view, mListener);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position)
    {
        if( position >= mData.size() ) { return; } //if this is end padding

        setupViewholder(viewHolder, position);
    }

    abstract void setupViewholder(ViewHolder viewHolder, int position);

    @Override
    public int getItemCount()
    {
        if(endPadding)
            return mData.size()+1;
        return mData.size();
    }

    public int getIDAtPos(int position)
    {
        return mData.get(position);
    }
}

