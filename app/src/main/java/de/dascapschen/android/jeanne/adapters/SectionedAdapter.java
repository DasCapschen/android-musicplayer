package de.dascapschen.android.jeanne.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.data.Album;
import de.dascapschen.android.jeanne.data.Song;
import de.dascapschen.android.jeanne.singletons.AllSongs;

public class SectionedAdapter extends RecyclerView.Adapter<SectionViewHolder>
{
    Context context;
    ArrayList<Album> sections;
    OnItemClickListener listener;
    boolean endPadding;

    public SectionedAdapter(Context context, OnItemClickListener listener, ArrayList<Album> sections, boolean useEndPadding)
    {
        this.context = context;
        this.sections = sections;
        this.listener = listener;
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

        Album album = sections.get(position);

        sectionViewHolder.albumArt.setImageResource( R.drawable.ic_launcher_background ); //TODO: album art
        sectionViewHolder.albumTitle.setText( album.getDescriptionTitle() );

        sectionViewHolder.nestedRecycler.setHasFixedSize(true);
        sectionViewHolder.nestedRecycler.setNestedScrollingEnabled(false);

        ArrayList<Song> songList = new ArrayList<>();

        AllSongs allSongs = AllSongs.instance();

        for( int songID : album.getSongIds() )
        {
            songList.add( allSongs.getByKey(songID) );
        }

        OnItemClickListener nestedClickListener = new OnItemClickListener() {
            @Override
            public void onItemClicked(int position)
            {
                Toast.makeText(context,
                        String.format("Clicked on Song %d in section %d",
                                position, sectionViewHolder.getAdapterPosition()),
                        Toast.LENGTH_SHORT).show();
            }
        };

        RecyclerAdapter<Song> nestedAdapter = new RecyclerAdapter<>(context, nestedClickListener, songList, false);
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
}
