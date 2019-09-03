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

import java.util.ArrayList;
import java.util.Locale;

import de.dascapschen.android.jeanne.NavigationRequest;
import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.AlbumRecycler;
import de.dascapschen.android.jeanne.adapters.OnItemClickListener;
import de.dascapschen.android.jeanne.data.QueryHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends Fragment implements OnItemClickListener
{
    AlbumRecycler adapter;

    public AlbumsFragment()
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

        ArrayList<Integer> albums = QueryHelper.getAllAlbumIDs(getContext());

        RecyclerView albumsView = view.findViewById(R.id.recyclerView);
        adapter = new AlbumRecycler(getContext(), this, albums, true);

        albumsView.setAdapter(adapter);
        albumsView.setLayoutManager( new LinearLayoutManager(getContext()));
    }

    @Override
    public void onItemClicked(int position)
    {
        Toast.makeText(getContext(), String.format(Locale.getDefault(),
                "Clicked on Album %d", position), Toast.LENGTH_SHORT).show();


        Bundle args = new Bundle();
        args.putInt("albumID", adapter.getIDAtPos(position));

        //TODO: USE INTENTS
        ((NavigationRequest)getActivity()).navigate(R.id.action_to_album, args);
    }
}
