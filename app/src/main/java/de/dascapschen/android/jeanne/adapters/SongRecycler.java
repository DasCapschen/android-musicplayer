package de.dascapschen.android.jeanne.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.data.QueryHelper;

public class SongRecycler extends RecyclerAdapter
{
    int selected = -1;

    public SongRecycler(Context context, OnItemClickListener listener, ArrayList<Integer> data, boolean useEndPadding)
    {
        super(context, listener, data, useEndPadding);
    }

    @Override
    void setupViewholder(ViewHolder viewHolder, int position)
    {
        int id = mData.get(position);

        MediaMetadataCompat metadata = QueryHelper.getSongMetadataFromID(mContext, id);
        if(metadata == null) return;

        viewHolder.title.setText(metadata.getDescription().getTitle());
        viewHolder.subtitle.setText(metadata.getDescription().getSubtitle());

        //load a default image if no art exists
        viewHolder.image.setImageResource( R.drawable.ic_launcher_background );

        Bitmap thumbnail = metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
        if(thumbnail != null)
        {
            viewHolder.image.setImageBitmap(thumbnail);
        }

        if(selected != -1 && id == selected)
        {
            viewHolder.layout.setBackgroundResource(R.color.colorPrimaryDark);
        }
        else
        {
            viewHolder.layout.setBackgroundResource(R.color.transparent);
        }
    }

    public void setSelected(int id)
    {
        int oldSelected = selected;
        selected = id;

        notifyItemChanged(mData.indexOf(oldSelected));
        notifyItemChanged(mData.indexOf(id));
    }
}