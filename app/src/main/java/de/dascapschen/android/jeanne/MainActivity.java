package de.dascapschen.android.jeanne;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.transition.AutoTransition;
import android.support.transition.Scene;
import android.support.transition.TransitionManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import de.dascapschen.android.jeanne.adapters.OnItemClickListener;
import de.dascapschen.android.jeanne.adapters.SongRecycler;
import de.dascapschen.android.jeanne.data.MetaDatabase;
import de.dascapschen.android.jeanne.service.MusicService;

public class MainActivity extends AppCompatActivity implements NavigationRequest
{
    MediaBrowserCompat mediaBrowser;
    MediaBrowserConnectionCallbacks connectionCallbacks = new MediaBrowserConnectionCallbacks();
    MediaControllerCallbacks controllerCallbacks = new MediaControllerCallbacks();
    OnItemClickListener onQueueItemClickedListener = new OnItemClickListener()
    {
        @Override
        public void onItemClicked(int position)
        {
            MediaControllerCompat
                    .getMediaController(MainActivity.this)
                    .getTransportControls()
                    .skipToQueueItem( position );
        }
    };

    NavController navController;

    boolean isPlaying = false;

    ViewGroup bottomRoot;
    Scene bottomClosed;
    Scene bottomOpen;
    boolean isBottomOpen = false;

    MetaDatabase database;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        {
            new AlertDialog.Builder(this)
                    .setIcon( android.R.drawable.ic_dialog_alert )
                    .setTitle(R.string.alert_need_permission)
                    .setMessage(R.string.alert_need_permission_text)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            finish();
                        }
                    })
                    .show();
        }
        else
        {
            setupView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate( R.menu.popup_menu, menu );
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home: //back button
                back();
                break;
            case R.id.menu_item_settings:
                //navigate(R.id.action_to_settings, null, false); //TODO: settings
                MetaDatabase.getInstance().recreate();
                break;
            case R.id.menu_item_about:
                showAboutMessage();
                break;
            case R.id.app_bar_search:
                //navigate(R.id.action_to_search, null); //bundle with search query?
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void navigate(int actionID, Bundle arguments, boolean homeAsUp)
    {
        navController.navigate(actionID, arguments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUp);
    }

    @Override
    public void navigate(int actionID, Bundle arguments)
    {
        navigate(actionID, arguments, true);
    }

    @Override
    public void back()
    {
        navController.popBackStack(R.id.destination_main, false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle( R.string.app_name );
    }

    @Override
    public void onBackPressed()
    {
        if(isBottomOpen)
        {
            onBottomSheetHidePressed(findViewById(R.id.btnHide));
        }
        else if(navController.getCurrentDestination().getId() != R.id.destination_main)
        {
            back();
        }
        else super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //ask for permission first
        if( checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
        }
        else
        {
            setupView();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Log.i("ACTIVITY", "On Start Called!");

        mediaBrowser = new MediaBrowserCompat(
                this,
                new ComponentName(this, MusicService.class),
                connectionCallbacks,
                null);
        mediaBrowser.connect();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if( controller != null )
        {
            controller.unregisterCallback(controllerCallbacks);
            MediaControllerCompat.setMediaController(this, null);
        }

        if( mediaBrowser != null )
        {
            mediaBrowser.disconnect();
            mediaBrowser = null;
        }

        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    private void buildDatabase()
    {
        MetaDatabase.init(this);
        final MetaDatabase db = MetaDatabase.getInstance();
        //if( !db.exists() ) //TODO: reenable when done with DB code!
        {
            db.recreate();
        }
    }

    private void setupView()
    {
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController( findViewById(R.id.main_navHost) );

        buildDatabase();
        setupBottomSheet();
    }

    private void showAboutMessage()
    {
        SpannableString message = new SpannableString("Android Music Player made for educational purposes.\n\n"
                + "Copyright (c) Dominik \"DasCapschen\" Waurenschk 2019\n\n"
                + "Source code available at https://gitlab.com/DasCapschen/android-musicplayer");

        Linkify.addLinks(message, Linkify.WEB_URLS);

        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());

        int paddingDP = (int)(8.f * Resources.getSystem().getDisplayMetrics().density);

        messageView.setPadding(paddingDP, paddingDP, paddingDP, paddingDP);

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_info_outline)
                .setTitle("ABOUT JEANNE")
                .setView(messageView)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void setupBottomSheet()
    {
        TextView songText = findViewById(R.id.bottom_song_title_compact);
        songText.setSelected(true); // to make marquee scrolling work

        bottomRoot = findViewById(R.id.main_bottomSheetFrame);
        bottomOpen = Scene.getSceneForLayout(bottomRoot, R.layout.bottomsheet_open, this);
        bottomClosed = Scene.getSceneForLayout(bottomRoot, R.layout.bottomsheet_closed, this);
    }

    public void onBottomSheetPressed(View v)
    {
        bottomSheetAnimation(false);

        //update artist and song text
        TextView songText = findViewById(R.id.bottom_song_title);
        songText.setSelected(true); // to make marquee scrolling work

        //possible to select multiple??
        TextView artistText = findViewById(R.id.bottom_artist_name);
        artistText.setSelected(true);

        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);

        controllerCallbacks.onRepeatModeChanged( controller.getRepeatMode() );
        controllerCallbacks.onShuffleModeChanged( controller.getShuffleMode() );

        updateQueueRecycler();
    }

    public void onBottomSheetHidePressed(View v)
    {
        bottomSheetAnimation(true);

        TextView songText = findViewById(R.id.bottom_song_title_compact);
        songText.setSelected(true); // to make marquee scrolling work
    }

    void bottomSheetAnimation( boolean close )
    {
        /* animate bottom sheet down to the closed layout */
        AutoTransition transition = new AutoTransition();
        AccelerateDecelerateInterpolator interp = new AccelerateDecelerateInterpolator();

        transition.setInterpolator(interp);
        transition.setDuration(500);
        transition.setOrdering(TransitionSet.ORDERING_TOGETHER);

        TransitionManager.go( close ? bottomClosed : bottomOpen, transition );

        /* add rounded corners of background image */
        final GradientDrawable bottomBG = (GradientDrawable) getResources().getDrawable(R.drawable.bottomsheet_bg, getTheme());

        float dp = 50;
        float px = dp * Resources.getSystem().getDisplayMetrics().density;

        ValueAnimator bgAnim;
        if(close)
        {
            bgAnim = ValueAnimator.ofFloat(0, px);
        }
        else
        {
            bgAnim = ValueAnimator.ofFloat(px, 0);
        }

        bgAnim.setDuration(500);
        bgAnim.setInterpolator(interp);
        bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                bottomBG.setCornerRadius( (float)animation.getAnimatedValue() );
            }
        });

        bgAnim.start();

        isBottomOpen = !close;

        //update Metadata and State (because everything resets when changing layouts)
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        controllerCallbacks.onMetadataChanged( controller.getMetadata() );
        controllerCallbacks.onPlaybackStateChanged( controller.getPlaybackState() );
    }

    void updateQueueRecycler()
    {
        /* UPDATE RECYCLER VIEW FOR PLAY QUEUE */
        List<MediaSessionCompat.QueueItem> queue = MediaControllerCompat.getMediaController(this).getQueue();
        if(queue != null)
        {
            ArrayList<Integer> songIDs = new ArrayList<>();
            for(MediaSessionCompat.QueueItem item : queue)
            {
                songIDs.add( (int)item.getQueueId() );
            }

            SongRecycler adapter = new SongRecycler(this, onQueueItemClickedListener, songIDs, false);

            RecyclerView queueView = findViewById(R.id.bottom_queue_recycler);
            queueView.setAdapter(adapter);
            queueView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void onBtnPlayPressed(View v)
    {
        ImageButton playBtn = (ImageButton)v;
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        if(controller == null)
        {
            return;
        }

        if( isPlaying )
        {
            controller.getTransportControls().pause();
        }
        else
        {
            controller.getTransportControls().play();
        }
    }

    public void onBtnNextPressed(View v)
    {
        MediaControllerCompat.getMediaController(this).getTransportControls().skipToNext();
    }

    public void onBtnPrevPressed(View v)
    {
        MediaControllerCompat.getMediaController(this).getTransportControls().skipToPrevious();
    }

    public void onBtnShufflePressed(View v)
    {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);

        int shuffleMode = controller.getShuffleMode();

        //either shuffle or don't ; dunno what "shuffle group" would be...
        if( shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE )
        {
            shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_ALL;
        }
        else
        {
            shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE;
        }

        controller.getTransportControls().setShuffleMode(shuffleMode);
    }

    public void onBtnRepeatPressed(View v)
    {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(this);
        int repeatMode = controller.getRepeatMode();

        //cycle between repeat all, one, none
        switch (repeatMode)
        {
            //setPressed() changes colors!
            case PlaybackStateCompat.REPEAT_MODE_NONE:
                repeatMode = PlaybackStateCompat.REPEAT_MODE_ALL;
                break;
            case PlaybackStateCompat.REPEAT_MODE_ALL:
                repeatMode = PlaybackStateCompat.REPEAT_MODE_ONE;
                break;
            case PlaybackStateCompat.REPEAT_MODE_ONE:
                repeatMode = PlaybackStateCompat.REPEAT_MODE_NONE;
                break;
        }

        controller.getTransportControls().setRepeatMode(repeatMode);
    }

    class MediaBrowserConnectionCallbacks extends MediaBrowserCompat.ConnectionCallback
    {

        @Override
        public void onConnectionFailed()
        {
            super.onConnectionFailed();
            Log.e("CONNECTION", "Failed");
        }

        @Override
        public void onConnected()
        {
            super.onConnected();

            Log.i("CONNECTION", "Called");

            // Get the token for the MediaSession
            MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

            try
            {
                // Create a MediaControllerCompat
                MediaControllerCompat mediaController =
                        new MediaControllerCompat(MainActivity.this, // Context
                                token);

                // Save the controller
                MediaControllerCompat.setMediaController(MainActivity.this, mediaController);

                mediaController.registerCallback(controllerCallbacks);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            // Finish building the UI
            MediaMetadataCompat metadata = MediaControllerCompat.getMediaController(MainActivity.this).getMetadata();
            controllerCallbacks.onMetadataChanged(metadata);
        }
    }

    //callbacks for when we play a new song, or press pause etc
    class MediaControllerCallbacks extends MediaControllerCompat.Callback
    {
        @Override
        public void onRepeatModeChanged(int repeatMode)
        {
            super.onRepeatModeChanged(repeatMode);

            ImageButton btn = findViewById(R.id.btnRepeat);
            if(btn == null) return;

            switch (repeatMode)
            {
                //setPressed() changes colors!
                case PlaybackStateCompat.REPEAT_MODE_ALL:
                    btn.setImageResource(R.drawable.ic_repeat);
                    btn.setColorFilter( getColor(R.color.white), PorterDuff.Mode.SRC_IN );
                    break;
                case PlaybackStateCompat.REPEAT_MODE_ONE:
                    btn.setImageResource(R.drawable.ic_repeat_one);
                    btn.setColorFilter( getColor(R.color.white), PorterDuff.Mode.SRC_IN );
                    break;
                case PlaybackStateCompat.REPEAT_MODE_NONE:
                    btn.setImageResource(R.drawable.ic_repeat);
                    btn.setColorFilter(getColor(R.color.black), PorterDuff.Mode.SRC_IN);
                    break;
                default:
                    Log.e("REPEAT MODE", "Unexpected Mode");
                    break;
            }
        }

        @Override
        public void onShuffleModeChanged(int shuffleMode)
        {
            super.onShuffleModeChanged(shuffleMode);

            ImageButton btn = findViewById(R.id.btnShuffle);
            if(btn == null) return;

            //either shuffle or don't ; dunno what "shuffle group" would be...
            if( shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE )
            {
                btn.setColorFilter(getColor(R.color.black), PorterDuff.Mode.SRC_IN);
            }
            else
            {
                btn.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            }
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state)
        {
            super.onPlaybackStateChanged(state);

            ImageButton playBtn = findViewById(R.id.btnPlay);

            if(state != null)
            {
                isPlaying = state.getState() == PlaybackStateCompat.STATE_PLAYING;
            }
            else isPlaying = false;

            if(isPlaying)
            {
                playBtn.setImageResource(R.drawable.ic_pause);
            }
            else
            {
                playBtn.setImageResource(R.drawable.ic_play);
            }


        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata)
        {
            super.onMetadataChanged(metadata);

            if(metadata == null) return;

            String title = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
            String artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);

            TextView text = findViewById(R.id.bottom_song_title_compact);
            if(text!=null) text.setText( title.concat(" - ").concat(artist) );

            TextView songTitle = findViewById(R.id.bottom_song_title);
            if(songTitle != null) songTitle.setText( title );
            TextView songArtist = findViewById(R.id.bottom_artist_name);
            if(songArtist != null) songArtist.setText( artist );

            ImageView art = findViewById(R.id.bottom_album_image);
            art.setImageResource(R.drawable.ic_launcher_background); //fallback

            Bitmap thumbnail = metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART);
            if(thumbnail!=null)
            {
                art.setImageBitmap(thumbnail);
            }
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue)
        {
            super.onQueueChanged(queue);

            //if not open, no need to update, we update on open anyways
            if(isBottomOpen)
            {
                updateQueueRecycler();
            }
        }
    }

}
