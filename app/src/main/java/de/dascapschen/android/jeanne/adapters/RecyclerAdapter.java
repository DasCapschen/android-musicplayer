package de.dascapschen.android.jeanne.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_item, viewGroup, false);

        ViewHolder holder = new ViewHolder(view, mListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        if(mTitles.size() > i)
        {
            viewHolder.title.setText(mTitles.get(i));
        }

        if( mSubtitles.size() > i )
        {
            viewHolder.subtitle.setText(mSubtitles.get(i));
        }

        try
        {
            viewHolder.image.setImageURI(mImageUris.get(i));
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
        return mTitles.size();
    }


    public interface OnItemClickListener {
        void onItemClicked(int position);
    }
}

