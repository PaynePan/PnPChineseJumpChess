package com.pnp.jpchess.android;


import com.pnp.jpchess.R;

import com.pnp.jpchess.common.Config;
import com.pnp.jpchess.mode.Game;
import com.pnp.jpchess.mode.GameStatusListener;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

/*
 * This file is part of "PnP Chinese Jump Chess".
 *
 * Author 2016 Payne Pan
 *
 * "PnP Chinese Jump Chess" is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * "PnP Chinese Jump Chess" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * see <http://www.gnu.org/licenses/lgpl.html>
 */

/**
 * This is main Activity responsible for showing View and Menus, capturing user input,
 * playing sounds .etc
 */
public class JumpChessActivity extends AppCompatActivity implements OnTouchListener, GameStatusListener {
    static final String TAG = "JumpChessActivity"; 
    static final int GAME_UPDATE = 1; 
    static final int GAME_RESULT = 2;
    
    private JumpChessView jcview;
    public int screenHeight;
    public int screenWidth;
    private Handler handler;

    private SoundPool soundPool;
    private int soundIdStep, soundIdWin;

    private void initSoundPool()
    {
        soundPool= new SoundPool(10, AudioManager.STREAM_MUSIC,5);
        soundIdStep = soundPool.load( this,R.raw.step,1);
        soundIdWin = soundPool.load( this,R.raw.win,1);
    }

    private void soundStep() {
        soundPool.play(soundIdStep,1, 1, 0, 0, 1);
    }

    private void soundWin() {
        soundPool.play(soundIdWin,1, 1, 0, 0, 1);
    }

    private void help() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        jcview =  (JumpChessView)findViewById( R.id.jumpchessview );
        jcview.setOnTouchListener(this);
        
        
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;

        jcview.DOT_WIDTH = 40 * screenWidth/400;

        ViewGroup.LayoutParams params  = jcview.getLayoutParams();
        params.height = (int) (screenHeight * 0.75);

        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Game.instance().startGame();
                jcview.invalidate();
            }
        });
        
        handler = new Handler(){
        	public void handleMessage (Message msg){
        		switch(msg.what) {
        		case GAME_UPDATE:
        		    jcview.invalidate();
                    soundStep();
        			break;
        	    case GAME_RESULT:
                    soundWin();
                    jcview.invalidate();
        	        showGameResult((String)msg.obj);
        	        break;
                }
        	}
        };
        
        Game.instance().setStatusListener(this);
        Game.instance().startGame(Game.MODE_LEVEL_NOMAL | Game.MODE_PLAYER_ONE);
    }

    public void statusUpdate(Game game){
        if ( game.getStatus() > 10) {
            String s;
            if ( game.getStatus() == 11)
                s = game.getUsers().get(0).getName() + " Win";
            else
                s = game.getUsers().get(1).getName() + " Win";

            Message msg = new Message();
            msg.what = JumpChessActivity.GAME_RESULT;
            msg.obj = s;
            handler.sendMessage(msg);
            return;
        }

        Message msg = new Message();
        msg.what = JumpChessActivity.GAME_UPDATE;
        handler.sendMessage(msg);
    }

    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        if ( actionCode == MotionEvent.ACTION_DOWN )
        	 ((JumpChessView)v ).onSelect( (int) event.getX(0), (int)event.getY(0)); 
		return true;
	}
    
    void showGameResult(String msg) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplicationContext(), msg, duration);
        toast.show();        
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        initSoundPool();
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        soundPool.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       //Toast.makeText(this, "你选的是" + item.getItemId(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.menu_undo:
                Game.instance().undo();
                jcview.invalidate();
                return true;
            case R.id.menu_advanced:
                Game.instance().startGame(Game.MODE_LEVEL_ADVANCED|Game.MODE_PLAYER_ONE);
                jcview.resetView();
                return true;
            case R.id.menu_normal:
                Game.instance().startGame(Game.MODE_LEVEL_NOMAL|Game.MODE_PLAYER_ONE);
                jcview.resetView();
                return true;
            case R.id.menu_watch:
                Game.instance().startGame(Game.MODE_LEVEL_NOMAL|Game.MODE_PLAYER_ALLAI);
                jcview.resetView();
                return true;
            case R.id.menu_help:
                help();
                return true;
            case R.id.menu_share:
                shareScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareScreen() {
        View view = getWindow().getDecorView();
        Bitmap b = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        view.draw(canvas);
        //Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.blueball);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                b, "Title", null);
        Uri imageUri =  Uri.parse(path);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, getString(R.string.share_best)));
    }
}
