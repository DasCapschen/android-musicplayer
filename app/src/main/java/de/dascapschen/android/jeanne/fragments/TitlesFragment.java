package de.dascapschen.android.jeanne.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Locale;

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.SongController;
import de.dascapschen.android.jeanne.adapters.OnItemClickListener;
import de.dascapschen.android.jeanne.adapters.RecyclerAdapter;
import de.dascapschen.android.jeanne.data.Song;
import de.dascapschen.android.jeanne.singletons.AllSongs;


/**
 * A simple {@link Fragment} subclass.
 */
public class TitlesFragment extends Fragment implements OnItemClickListener
{
    AllSongs songs;

    public TitlesFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_layout, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        songs = AllSongs.instance();

        RecyclerView titleView = view.findViewById(R.id.recyclerView);
        RecyclerAdapter<Song> adapter
                = new RecyclerAdapter<>(getContext(), this, songs.data(), true);

        titleView.setAdapter(adapter);
        titleView.setLayoutManager( new LinearLayoutManager(getContext()) );
    }

    @Override
    public void onItemClicked(int position)
    {
        Toast.makeText(getContext(), String.format(Locale.getDefault(),
                "Playing Title %d", position), Toast.LENGTH_SHORT).show();

        SongController sc = (SongController)getActivity();
        sc.startNewSong( songs.getByIndex(position) );
    }
}
