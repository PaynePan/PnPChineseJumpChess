package com.pnp.jpchess.mode;

import com.pnp.jpchess.common.Config;
import com.pnp.jpchess.common.Step;
import com.pnp.jpchess.common.Util;
import com.pnp.jpchess.common.WeightStep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

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

public class UserAI_BreadthFirst extends UserAI {
    public UserAI_BreadthFirst(int side) {
        super(side);
    }

    class Node implements Comparable<Node>  {
        int box[][];
        ArrayList<Node> links = new ArrayList<Node>();
        Step step;
        Node prev;
        int h;
        int level;
        int side;

        public Node(int[][] b) {
            box = b;
        }

        public int compareTo(Node arg0) {
            if (this.h < arg0.h)
                return -1;
            else if (this.h > arg0.h)
                return 1;
            return 0;
        }
    }

    protected Step calculate_step(int box[][]){
        /* construct tree to evaluate */
        Node root = new Node(box);
        root.level = 0;
        root.side = this.getSide();
        LinkedList<Node> que = new LinkedList<Node>();
        que.add(root);
        LinkedList<Node>  stack = new LinkedList<Node>();

        while ( !que.isEmpty()) {
            Node cur = que.removeFirst();
            stack.push(cur);
            int[][] tryBox = cur.box;
            ArrayList<WeightStep> steps = new ArrayList<WeightStep>();
            getStep(tryBox,cur.side,steps);

            for (Step step : steps) {
                Util.moveStep(step, tryBox);
                Node node = new Node(Util.clone_box(tryBox));
                node.prev = cur;
                node.step = step;
                node.level = cur.level+1;
                node.side = 3 - cur.side;
                cur.links.add(node);

                if ((node.side != getSide() && Game.instance().checkWin(tryBox, getSide()))
                        || (node.side == getSide() && Game.instance().checkWin(tryBox, 3-getSide()))) {
                    node.h = Util.get_box_score(tryBox, weightBox, getSide()) - Util.get_box_score(tryBox, counterWeightBox, 3 - getSide());
                } else {
                    if (node.level < Config.AI_LEVEL - 1) { /* reduce the level, or it runs too much slow */
                        que.addLast(node);
                    } else { /* read max level */
                        node.h = Util.get_box_score(tryBox, weightBox, getSide()) - Util.get_box_score(tryBox, counterWeightBox, 3 - getSide());
                    }
                }
                Util.revertStep(step, tryBox);
            }
        }

        /* calculate score of un-leaf */
        while ( !stack.isEmpty()) {
            Node cur = stack.pop();
            if ( cur.links.isEmpty()) {
                continue;
            }

            Collections.sort(cur.links, Collections.reverseOrder());
            if ( cur.side == this.getSide()) {
                cur.h = cur.links.get(0).h;
            } else {
                cur.h = cur.links.get(cur.links.size() - 1).h;
            }
        }
        return root.links.get(0).step; /* return max h step*/
    }
}