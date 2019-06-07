package de.dascapschen.android.jeanne.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.dascapschen.android.jeanne.R;

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    LinearLayout layout;
    ImageView image;
    TextView title;
    TextView subtitle;

    RecyclerAdapter.OnItemClickListener listener;

    public ViewHolder(@NonNull View itemView, RecyclerAdapter.OnItemClickListener listener)
    {
        super(itemView);

        layout = itemView.findViewById(R.id.recylcer_item_root_layout);
        if( layout == null )
            return;

        image = itemView.findViewById(R.id.recycler_item_image);
        title = itemView.findViewById(R.id.recycler_item_title);
        subtitle = itemView.findViewById(R.id.recycler_item_subtitle);

        this.listener = listener;

        layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        listener.onItemClicked( getAdapterPosition() );
    }
}
