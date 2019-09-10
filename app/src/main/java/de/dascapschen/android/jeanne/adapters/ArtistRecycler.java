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

        viewHolder.title.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        viewHolder.subtitle.setText(String.format("%d albums", metadata.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)));

        //load a default image if no art exists
        viewHolder.image.setImageResource( R.drawable.ic_launcher_background );

        Bitmap thumbnail = metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
        if(thumbnail != null)
        {
            viewHolder.image.setImageBitmap(thumbnail);
        }
    }
}