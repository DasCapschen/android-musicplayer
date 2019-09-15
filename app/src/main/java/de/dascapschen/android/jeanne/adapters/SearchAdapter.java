package de.dascapschen.android.jeanne.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;

public class SearchAdapter extends RecyclerView.Adapter<SectionViewHolder>
{
    Context context;
    ArrayList<Integer> songs;
    ArrayList<Integer> albums;
    ArrayList<Integer> artists;
    SectionedAdapter.NestedItemClickListener nestedListener;

    public SearchAdapter(Context context,
                         SectionedAdapter.NestedItemClickListener nestedListener,
                         ArrayList<Integer> songs, ArrayList<Integer> albums, ArrayList<Integer> artists)
    {
        this.context = context;
        this.songs = songs;
        this.albums = albums;
        this.artists = artists;
        this.nestedListener = nestedListener;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type)
    {
        View view;
        SectionViewHolder holder;

        if(type < 3)
        {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_header, viewGroup, false);
            holder = new SectionViewHolder(view, null);
        }
        else //endpadding, 3
        {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_end_padding, viewGroup, false);
            holder = new SectionViewHolder(view, null);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SectionViewHolder sectionViewHolder, int position)
    {
        if(position > 2) return;

        sectionViewHolder.nestedRecycler.setHasFixedSize(true);
        sectionViewHolder.nestedRecycler.setNestedScrollingEnabled(false);
        sectionViewHolder.albumArt.setImageResource( R.drawable.ic_launcher_background );

        if(position == 0)
        {
            sectionViewHolder.albumTitle.setText( context.getString(R.string.tab_titles) );

            OnItemClickListener nestedClickListener = new OnItemClickListener() {
                @Override
                public void onItemClicked(int position)
                {
                    nestedListener.onItemClicked(position, sectionViewHolder.getAdapterPosition());
                }
            };

            SongRecycler nestedAdapter = new SongRecycler(context, nestedClickListener, songs, false);
            sectionViewHolder.nestedRecycler.setLayoutManager(new LinearLayoutManager(context));
            sectionViewHolder.nestedRecycler.setAdapter(nestedAdapter);
        }
        else if(position == 1)
        {
            sectionViewHolder.albumTitle.setText( context.getString(R.string.tab_albums) );

            OnItemClickListener nestedClickListener = new OnItemClickListener() {
                @Override
                public void onItemClicked(int position)
                {
                    nestedListener.onItemClicked(position, sectionViewHolder.getAdapterPosition());
                }
            };

            AlbumRecycler nestedAdapter = new AlbumRecycler(context, nestedClickListener, albums, false);
            sectionViewHolder.nestedRecycler.setLayoutManager(new LinearLayoutManager(context));
            sectionViewHolder.nestedRecycler.setAdapter(nestedAdapter);
        }
        else if(position == 2)
        {
            sectionViewHolder.albumTitle.setText( context.getString(R.string.tab_artists) );

            OnItemClickListener nestedClickListener = new OnItemClickListener() {
                @Override
                public void onItemClicked(int position)
                {
                    nestedListener.onItemClicked(position, sectionViewHolder.getAdapterPosition());
                }
            };

            ArtistRecycler nestedAdapter = new ArtistRecycler(context, nestedClickListener, artists, false);
            sectionViewHolder.nestedRecycler.setLayoutManager(new LinearLayoutManager(context));
            sectionViewHolder.nestedRecycler.setAdapter(nestedAdapter);
        }
    }

    @Override
    public int getItemCount()
    {
        return 4;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public ArrayList<Integer> getSongs() { return songs; }

    public int getIDAtPos(int position, int section)
    {
        switch (section)
        {
            case 0: return songs.get(position);
            case 1: return albums.get(position);
            case 2: return artists.get(position);
        }
        return -1;
    }
}
