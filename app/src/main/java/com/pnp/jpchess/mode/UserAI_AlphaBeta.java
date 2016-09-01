package com.pnp.jpchess.mode;

import com.pnp.jpchess.common.Config;
import com.pnp.jpchess.common.Step;
import com.pnp.jpchess.common.Util;
import com.pnp.jpchess.common.WeightStep;

import java.util.ArrayList;
import java.util.Collections;

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

public class UserAI_AlphaBeta extends UserAI {
    public UserAI_AlphaBeta(int side){
        super(side);
    }

    class Node implements Comparable<Node>  {
        int box[][];
        ArrayList<Node> links = new ArrayList<Node>();
        Step step;
        Node prev;
        int level; /* search level */
        int side;
        int a = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;
        int h;
        int h_level = Integer.MAX_VALUE; /* indicate the search level where h score is acquired */

        public Node(int[][] b) {
            box = b;
        }

        public int compareTo(Node arg0) {
            if (this.h > arg0.h ) {
                return 1;
            } else if (this.h < arg0.h ) {
                return -1;
            }  else { /* smaller is better */
                if (this.h_level < arg0.h_level )
                    return 1;
                else if (this.h_level > arg0.h_level )
                    return -1;
            }
            return 0;
        }
    }

    void calculate_Node_Score(Node cur) {
        int[][] tryBox = cur.box;
        if ((cur.level == Config.AI_LEVEL)
                || (cur.side != getSide() && Game.instance().checkWin(tryBox, getSide()))
                || (cur.side == getSide() && Game.instance().checkWin(tryBox, 3-getSide()))){
            cur.h = Util.get_box_score(tryBox, weightBox, getSide()) - Util.get_box_score(tryBox, counterWeightBox, 3 - getSide());
            cur.h_level = cur.level;
            return;
        }

        ArrayList<WeightStep> steps = new ArrayList<WeightStep>();
        getStep(tryBox,cur.side,steps);

        /* expand node */
        for (Step step :steps) {
            Util.moveStep(step, tryBox);
            //String s = Util.box2str(tryBox);
            int[][] b = Util.clone_box(tryBox);
            Util.revertStep(step, tryBox);
            Node node = new Node(b);
            node.prev = cur;
            node.step = step;
            node.a = cur.a;
            node.b = cur.b;
            node.level = cur.level + 1;
            node.side = 3 - cur.side;
            calculate_Node_Score(node);

            if (cur.side == getSide()) { //max
                if ( cur.a < node.h || ( cur.a == node.h && cur.h_level > node.h_level )) {
                    cur.a = node.h;
                    cur.h_level = node.h_level;
                    cur.links.add(node);
                }
                if ( cur.a >= cur.b) {
                    cur.h = cur.b;
                    return;
                }
            } else {
                if (cur.b > node.h  || (cur.b == node.h &&  cur.h_level > node.h_level )) {
                    cur.b = node.h;
                    cur.h_level = node.h_level;
                    cur.links.add(node);
                }
                if ( cur.a >= cur.b) {
                    cur.h = cur.a;
                    return;
                }
            }
        }
        if (cur.side == getSide()) { //max
            cur.h = cur.a;
        } else {
            cur.h = cur.b;
        }
    }

    @Override
    protected Step calculate_step(int box[][]){
        Node root = new Node(box);
        root.level = 0;
        root.side = getSide();
        calculate_Node_Score(root);
        Collections.sort(root.links, Collections.<Node>reverseOrder());

        if (root.links.isEmpty())
            return null;
        return root.links.get(0).step; // return max;
    }
}