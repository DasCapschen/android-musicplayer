package de.dascapschen.android.jeanne.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.data.MusicalData;

public class RecyclerAdapter<T extends MusicalData> extends RecyclerView.Adapter<ViewHolder>
{
    Context mContext;
    ArrayList<T> mData;
    boolean endPadding;

    private OnItemClickListener mListener;

    public RecyclerAdapter(Context context, OnItemClickListener listener, ArrayList<T> data, boolean useEndPadding)
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

        T datum = mData.get(position);

        viewHolder.title.setText( datum.getDescriptionTitle() );
        viewHolder.subtitle.setText( datum.getDescriptionSubtitle() );

        //load a default image if no art exists
        viewHolder.image.setImageResource( R.drawable.ic_launcher_background );
    }

    @Override
    public int getItemCount()
    {
        if(endPadding)
            return mData.size()+1;
        return mData.size();
    }
}

