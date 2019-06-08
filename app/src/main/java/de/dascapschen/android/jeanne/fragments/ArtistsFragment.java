package de.dascapschen.android.jeanne.fragments;

import android.net.Uri;
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

import java.util.ArrayList;
import java.util.Locale;

import de.dascapschen.android.jeanne.NavigationRequest;
import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.RecyclerAdapter;
import de.dascapschen.android.jeanne.data.Artist;
import de.dascapschen.android.jeanne.singletons.AllArtists;


public class ArtistsFragment extends Fragment implements RecyclerAdapter.OnItemClickListener
{
    public ArtistsFragment()
    {
        // Required empty public constructor
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

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> subtitles = new ArrayList<>();
        ArrayList<Uri> images = new ArrayList<>();

        AllArtists artists = AllArtists.instance();

        RecyclerView artistView = (RecyclerView)view.findViewById(R.id.recyclerView);
        RecyclerAdapter<Artist> artistAdapter
                = new RecyclerAdapter<>(getContext(), this, artists.data());

        artistView.setAdapter(artistAdapter);
        artistView.setLayoutManager( new LinearLayoutManager(getContext()) );
    }

    @Override
    public void onItemClicked(int position)
    {
        Toast.makeText(getContext(), String.format(Locale.getDefault(),
                "Clicked on Artist %d", position), Toast.LENGTH_SHORT).show();

        ((NavigationRequest)getActivity()).navigate(R.id.action_to_artist);
    }
}
