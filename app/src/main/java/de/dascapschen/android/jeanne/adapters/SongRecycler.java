package de.dascapschen.android.jeanne.adapters;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.data.QueryHelper;

public class SongRecycler extends RecyclerAdapter
{
    public SongRecycler(Context context, OnItemClickListener listener, ArrayList<Integer> data, boolean useEndPadding)
    {
        super(context, listener, data, useEndPadding);
    }

    @Override
    void setupViewholder(ViewHolder viewHolder, int position)
    {
        int id = mData.get(position);

        //FIXME: takes about 8ms
        //QUESTION: why is song metadata ~8ms, and artist metadata ~35ms !?
        MediaMetadataCompat metadata = QueryHelper.getSongMetadataFromID(mContext, id);
        if(metadata == null) return;

        viewHolder.title.setText(metadata.getDescription().getTitle());
        viewHolder.subtitle.setText(metadata.getDescription().getSubtitle());

        //load a default image if no art exists
        viewHolder.image.setImageResource( R.drawable.ic_launcher_background );
    }
}