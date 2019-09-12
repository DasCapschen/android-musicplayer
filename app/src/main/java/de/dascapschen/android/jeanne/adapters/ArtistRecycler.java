package de.dascapschen.android.jeanne.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.data.QueryHelper;

public class ArtistRecycler extends RecyclerAdapter
{
    public ArtistRecycler(Context context, OnItemClickListener listener, ArrayList<Integer> data, boolean useEndPadding)
    {
        super(context, listener, data, useEndPadding);
    }

    @Override
    void setupViewholder(ViewHolder viewHolder, int position)
    {
        int id = mData.get(position);

        MediaMetadataCompat metadata = QueryHelper.getArtistMetadataFromID(mContext, id);
        if(metadata ==  null) return;

        String artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
        long numSongs = metadata.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS);
        String subtitle = mContext.getString(R.string.artist_subtitle, numSongs);

        if(artist.equals("<unknown>"))
        {
            artist = mContext.getString(R.string.unknown_replacement);
        }

        viewHolder.title.setText(artist);
        viewHolder.subtitle.setText(subtitle);

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