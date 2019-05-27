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

import de.dascapschen.android.jeanne.R;
import de.dascapschen.android.jeanne.adapters.RecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoldersFragment extends Fragment implements RecyclerAdapter.OnItemClickListener
{


    public FoldersFragment()
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

        for(int i = 0; i < 12; i++)
        {
            titles.add("FOLDERS");
            subtitles.add("FRAGMENT");
        }

        RecyclerView folderView = (RecyclerView)view.findViewById(R.id.recyclerView);
        RecyclerAdapter folderAdapter = new RecyclerAdapter(getContext(), this, titles, subtitles, images);

        folderView.setAdapter(folderAdapter);
        folderView.setLayoutManager( new LinearLayoutManager(getContext()) );
    }

    @Override
    public void onItemClicked(int position)
    {
        Toast.makeText(getContext(), String.format(Locale.getDefault(),
                "Clicked on Folder %d", position), Toast.LENGTH_SHORT).show();
    }
}
