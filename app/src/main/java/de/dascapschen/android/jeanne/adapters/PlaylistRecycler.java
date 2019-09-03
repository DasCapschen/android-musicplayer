package de.dascapschen.android.jeanne.adapters;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.data.QueryHelper;

public class PlaylistRecycler extends RecyclerAdapter
{
    public PlaylistRecycler(Context context, OnItemClickListener listener, ArrayList<Integer> data, boolean useEndPadding)
    {
        super(context, listener, data, useEndPadding);
    }

    @Override
    void setupViewholder(ViewHolder viewHolder, int position)
    {
        int id = mData.get(position);

        MediaMetadataCompat metadata = QueryHelper.getAlbumMetadataFromID(mContext, id);
        if(metadata == null) return;


        viewHolder.title.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
        viewHolder.subtitle.setText(String.format("%d songs", metadata.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)));

        //load a default image if no art exists
        viewHolder.image.setImageResource( R.drawable.ic_launcher_background );
    }
}