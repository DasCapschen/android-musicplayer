package de.dascapschen.android.jeanne.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.data.QueryHelper;

public class AlbumRecycler extends RecyclerAdapter
{
    public AlbumRecycler(Context context, OnItemClickListener listener, ArrayList<Integer> data, boolean useEndPadding)
    {
        super(context, listener, data, useEndPadding);
    }

    @Override
    void setupViewholder(ViewHolder viewHolder, int position)
    {
        int id = mData.get(position);

        MediaMetadataCompat metadata = QueryHelper.getAlbumMetadataFromID(mContext, id);
        if(metadata == null) return;

        String artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
        String album = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
        if(artist.equals("<unknown>"))
        {
            artist = mContext.getString(R.string.unknown_replacement);
        }

        viewHolder.title.setText(album);
        viewHolder.subtitle.setText(artist);


        Bitmap thumbnail = metadata.getDescription().getIconBitmap();
        if(thumbnail != null)
        {
            viewHolder.image.setImageBitmap(thumbnail);
        }
        else
        {
            //load a default image if no art exists
            viewHolder.image.setImageResource( R.drawable.ic_launcher_background );
        }
    }
}