package de.dascapschen.android.jeanne.adapters;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.dascapschen.android.jeanne.R;

public class SectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    LinearLayout layout;
    ImageView albumArt;
    TextView albumTitle;
    ImageButton openBtn;
    RecyclerView nestedRecycler;
    OnItemClickListener listener;

    boolean opened = true;
    int originalHeight = 0;
    ValueAnimator anim;

    public SectionViewHolder(@NonNull View itemView, OnItemClickListener listener)
    {
        super(itemView);

        layout = itemView.findViewById(R.id.recycler_header_header_layout);
        if(layout == null)
            return;

        albumArt = itemView.findViewById(R.id.recycler_header_image);
        albumTitle = itemView.findViewById(R.id.recycler_header_title);
        openBtn = itemView.findViewById(R.id.recycler_header_open);
        nestedRecycler = itemView.findViewById(R.id.recycler_header_recycler);

        //originalHeight = nestedRecycler.getMeasuredHeight(); //seems 0 at this moment

        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                animateOpenClose();
            }
        });

        this.listener = listener;
        layout.setOnClickListener(this);
    }

    private void animateOpenClose()
    {
        if(originalHeight == 0) originalHeight = nestedRecycler.getMeasuredHeight();

        if(opened)
        {
            anim = ValueAnimator.ofInt(originalHeight, 0);
            openBtn.animate().scaleY(-1).setDuration(250).start();
            opened = false;
        }
        else
        {
            anim = ValueAnimator.ofInt(0, originalHeight);
            openBtn.animate().scaleY(1).setDuration(250).start();
            opened = true;
        }

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = nestedRecycler.getLayoutParams();
                params.height = val;
                nestedRecycler.setLayoutParams(params);
            }
        });
        anim.setDuration(250);
        anim.start();
    }

    @Override
    public void onClick(View v)
    {
        if(listener == null) return;
        listener.onItemClicked(getAdapterPosition());
    }
}
