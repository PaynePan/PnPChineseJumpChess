package com.pnp.jpchess.mode;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import com.pnp.jpchess.common.Config;
import com.pnp.jpchess.common.Step;
import com.pnp.jpchess.common.Util;
import com.pnp.jpchess.common.WeightStep;

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

abstract public class UserAI extends User {
    protected int[][] weightBox;
    protected int[][] counterWeightBox;

    private Handler handler;

    private void initBox() {
        weightBox = new int[Config.BOX_RANG][Config.BOX_RANG];
        counterWeightBox = new int[Config.BOX_RANG][Config.BOX_RANG];

        /* construct weight box to get heuristic score */
        Util.init_weight_box(weightBox);
        /* also construct counterWeightBox, because the counter side will use it to get heuristic */
        Util.init_weight_box(counterWeightBox);
        Util.flip_box(counterWeightBox);
    }

    public UserAI(int side){
        initBox();
        this.side = side;

        if ( side == 1) { /* default box is for player 2 */
            Util.flip_box(weightBox);
            Util.flip_box(counterWeightBox);
        }

        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (UserAI.this.getGame() == null) { /* User already remove from game */
                    return;
                }

                long begin = System.currentTimeMillis();
                Step step = calculate_step(UserAI.this.getGame().getBox());
                long dur = System.currentTimeMillis() - begin;
                if (dur < 800) {  /* every step take a least 0.8 second */
                    try {
                        Thread.sleep(800-dur);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (step != null) {
                    UserAI.this.getGame().acceptStep(step);
                }
            }
        };
    }

    void getStep(int box[][],int side, ArrayList<WeightStep> steps) {

        for( int i=0; i < Config.BOX_RANG; i++){
            for( int j=0; j < Config.BOX_RANG; j++){
                if ( box[i][j]== side ){
                    if ( side == getSide())
                        Util.get_path( i,j,box,weightBox,steps);
                    else
                        Util.get_path( i,j,box,counterWeightBox,steps);
                }
            }
        }
    }

    abstract protected Step calculate_step(int box[][]);

    public void statusUpdate(Game game){
        if ( game.getStatus() > 10) /* game is over */
            return;

        if ( this.getSide() == game.getPlayingSide() ) {
            handler.sendEmptyMessageDelayed(0,50); /* to avoid multi-thread, we use Android looper-hander */
        }
    }
}
