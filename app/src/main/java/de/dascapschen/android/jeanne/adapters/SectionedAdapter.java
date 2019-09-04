package de.dascapschen.android.jeanne.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.MainActivity;
import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.data.QueryHelper;
import de.dascapschen.android.jeanne.service.MusicService;

public class SectionedAdapter extends RecyclerView.Adapter<SectionViewHolder>
{
    Context context;
    ArrayList<Integer> sections;
    OnItemClickListener listener;
    NestedItemClickListener nestedListener;
    boolean endPadding;

    public SectionedAdapter(Context context, OnItemClickListener listener, NestedItemClickListener nestedListener, ArrayList<Integer> sections, boolean useEndPadding)
    {
        this.context = context;
        this.sections = sections;
        this.listener = listener;
        this.nestedListener = nestedListener;
        this.endPadding = useEndPadding;
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
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        View view;
        SectionViewHolder holder;


        if( viewType == 1 )
        {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_end_padding, viewGroup, false);
            holder = new SectionViewHolder(view, null);
        }
        else
        {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_header, viewGroup, false);
            holder = new SectionViewHolder(view, listener);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SectionViewHolder sectionViewHolder, int position)
    {
        if( position >= sections.size() ) { return; } //if this is end padding

        int albumID = sections.get(position);

        MediaMetadataCompat metadata = QueryHelper.getAlbumMetadataFromID(context, albumID);
        if(metadata == null) return;

        sectionViewHolder.albumArt.setImageResource( R.drawable.ic_launcher_background ); //TODO: album art
        sectionViewHolder.albumTitle.setText( metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM) );

        sectionViewHolder.nestedRecycler.setHasFixedSize(true);
        sectionViewHolder.nestedRecycler.setNestedScrollingEnabled(false);

        ArrayList<Integer> songIDs = QueryHelper.getSongIDsForAlbum(context, albumID);

        OnItemClickListener nestedClickListener = new OnItemClickListener() {
            @Override
            public void onItemClicked(int position)
            {
                nestedListener.onItemClicked(position, sectionViewHolder.getAdapterPosition());
            }
        };

        SongRecycler nestedAdapter = new SongRecycler(context, nestedClickListener, songIDs, false);
        sectionViewHolder.nestedRecycler.setLayoutManager(new LinearLayoutManager(context));
        sectionViewHolder.nestedRecycler.setAdapter(nestedAdapter);
    }

    @Override
    public int getItemCount()
    {
        if(endPadding)
            return sections.size()+1;
        return sections.size();
    }

    public int getIDAtPos(int position)
    {
        return sections.get(position);
    }

    public interface NestedItemClickListener
    {
        void onItemClicked(int position, int section);
    }
}
