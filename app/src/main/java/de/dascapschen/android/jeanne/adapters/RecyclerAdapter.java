package de.dascapschen.android.jeanne.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder>
{
    Context mContext;

    ArrayList<String> mTitles;
    ArrayList<String> mSubtitles;
    ArrayList<Uri> mImageUris;      /* use: ContentResolver.loadThumbnail() */

    private OnItemClickListener mListener;

    public RecyclerAdapter(Context context, OnItemClickListener listener, ArrayList<String> titles,
                           ArrayList<String> subtitles, ArrayList<Uri> imageUris)
    {
        mContext = context;
        mListener = listener;
        mTitles = titles;
        mSubtitles = subtitles;
        mImageUris = imageUris;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position >= getItemCount()-1)
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
        if( position >= mTitles.size() ) { return; } //if this is last item

        viewHolder.title.setText(mTitles.get(position));

        if( mSubtitles.size() > position )
        {
            viewHolder.subtitle.setText(mSubtitles.get(position));
        }

        try
        {
            viewHolder.image.setImageURI(mImageUris.get(position));
        }
        catch(Exception e)
        {
            //load a default image if no art exists
            viewHolder.image.setImageResource( R.drawable.ic_launcher_background );
        }
    }

    @Override
    public int getItemCount()
    {
        return mTitles.size()+1;
    }


    public interface OnItemClickListener {
        void onItemClicked(int position);
    }
}

