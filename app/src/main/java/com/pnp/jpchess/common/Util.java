package com.pnp.jpchess.common;


import android.util.Log;

import java.util.ArrayList;
import java.util.Stack;

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


public class Util {
	private static final String TAG = "Util";

	public static void print_box(int box[][])
	{
		Log.e(TAG,"----------------");
		for( int i=0; i < box.length; i++){
			StringBuffer sb = new StringBuffer();
			for( int j=0; j < box[i].length; j++){
				sb.append(' ');
				sb.append(String.format("%3d",box[i][j]));
			}
			Log.e(TAG,sb.toString());
		}
	}

	public static void copy_box(int box1[][], int box2[][])
	{
		for( int i=0; i < box1.length; i++) {
            for (int j = 0; j < box1[i].length; j++) {
                box2[i][j] = box1[i][j];
            }
        }
	}

	public static int[][] clone_box(int box1[][])
	{
		int box2[][] = new int[box1.length][box1.length];
		for( int i=0; i < box1.length; i++) {
            for (int j = 0; j < box1[i].length; j++) {
                box2[i][j] = box1[i][j];
            }
        }
		return box2;
	}

	public static void negtive_box(int box[][]){
		for( int i=0; i < box.length; i++){
			for( int j=0; j < box[i].length; j++){
				box[i][j] = - box[i][j];
			}
		}
	}

	public static void add_box(int box[][], int box2[][]){
		for( int i=0; i < box.length; i++){
			for( int j=0; j < box[i].length; j++){
				box[i][j] += box2[i][j];
			}
		}
	}

	public static boolean is_equal(int box1[][], int box2[][]){
		int len = box1.length;
		for( int i=0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (box1[i][j] != box2[i][j])
                    return false;
            }
        }
		return true;
	}

	// flip by diagonal
	public static void flip_box(int box[][]){
		int len = box.length;
		for(int i=0; i < box.length; i++) {
            for (int j = 0; j < box[i].length - i; j++) {
                int temp = box[i][j];
                int ii = len - i - 1;
                int jj = len - j - 1;
                box[i][j] = box[ii][jj];
                box[ii][jj] = temp;
            }
        }
	}

	// flip by vertical middle line
	public static void flip2_box(int box[][]){
		for(int i=0; i < box.length; i++) {
            for (int j = 0; j < box[i].length / 2; j++) {
                int temp = box[i][j];
                int jj = box[i].length - j - 1;
                box[i][j] = box[i][jj];
                box[i][jj] = temp;
            }
        }
	}

    /**
     * Init a game board
     *  For example a initial game board is just like below:
     *  The 1 side need to move all "1" to "2" positions,
     *  While 2 side need to move all "2" to "1" positions
     *      --------------
     *       2   2   0   0
     *       2   0   0   0
     *       0   0   0   1
     *       0   0   1   1
     *
     */
	public static void init_box(int box[][]) {
        int len = box.length;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                box[i][j] = 0;
            }
        }

        for (int i = 0; i < Config.CHESS_RANG; i++) {
            for (int j = 0; j < Config.CHESS_RANG - i; j++) {
                box[i][j] = 1;
            }
        }

		flip_box(box);
		for(int i=0; i < Config.CHESS_RANG; i++) {
            for (int j = 0; j < Config.CHESS_RANG - i; j++) {
                box[i][j] = 2;
            }
        }
	}

	/**
	 * For constructing the weight box
	 * every position in the board(or box) will give a score
	 * if a chessman is in the position, it can get the score
     *  a weight box is like below:
     *       --------------
     *       3   7  11  15
     *       7  13  17  21
     *       11  17  23  27
     *       15  21  27  33
     *
	 *  if you are in side "2" in below board
     *      --------------
     *       0   2   2   0
     *       0   0   0   0
     *       0   2   0   1
     *       0   0   1   1
     *
     *  you occupy below positions, you will get
     *  heuristic score of 7+11+17
     *       --------------
     *          7   11
     *
     *          17
	 */
	public static void init_weight_box(int box[][])
	{
		int len = box.length;
		for(int i=0; i < len; i++)
			for(int j =0; j < len ;  j++ ){
				box[i][j] =  (i+j)*5;
			}

		// the chess as near central will get higher score.
		int helpBox[][] = new int[len][len];
		for(int i=0; i< len; i++)
			for(int j =0; j < len ;  j++ )
				helpBox[i][j] = (i+j) < len?  i+j : 2*(len-1) - (i+j) ;
		flip2_box(helpBox);
		add_box(box,helpBox);
	}

	/**
	 * return heuristic score for board status
	 * the total value is just sum of the weight for all chessmen in given side
	 */
	public static int get_box_score(int box[][], int weight[][], int side){
		int score = 0;
		int len = box.length;
		for(int i=0; i < len; i++) {
			for(int j =0; j < len;  j++ ){
				if ( box[i][j]==side ){
					score += weight[i][j];
				}
			}
		}
		return score;
	}

	public static boolean in_range(int x, int box[][]){
		if ( x < 0 || x >= box.length)
			return false;
		return true;
	}

	public static void moveStep(Step step, int box[][]) {
		box[step.new_i][step.new_j] = box[step.i][step.j];
		box[step.i][step.j] = 0;
	}

	public static void revertStep(Step step, int box[][]) {
		box[step.i][step.j] = box[step.new_i][step.new_j];
		box[step.new_i][step.new_j] = 0;
	}

	private static int direct[][] = new int[][]{
			{0,-1},
			{1,-1},
			{1,0},
			{0,1},
			{-1,1},
			{-1,0}
	};

	private static void get_jump_path(int bi, int bj, int box[][], int weight_box[][], ArrayList<WeightStep> vec) {
		int side = box[bi][bj];
		int his_box[][] = new int[box.length][box[0].length];

		Stack<Integer> stack_i = new Stack<Integer>();
		Stack<Integer> stack_j = new Stack<Integer>();
		Stack<Integer> stack_dir = new Stack<Integer>();

		stack_i.push(bi);
		stack_j.push(bj);
		stack_dir.push(-1);

		while( ! stack_i.empty()){
			int i = stack_i.pop();
			int j = stack_j.pop();
			int dir = stack_dir.pop();
			dir++;

			for( ; dir < 6; dir++ ){
				int ii = i + direct[dir][0];
				int jj = j + direct[dir][1];

				if ( ! in_range(ii,box) ||  ! in_range(jj,box) )
					continue;
				if ( box[ii][jj] == 0)
					continue;

				else{ // box[ii][jj] == 1,  so jump it
					ii = ii + direct[dir][0];
					jj = jj + direct[dir][1];
					if ( ! in_range(ii,box) ||  ! in_range(jj,box) )
						continue;
					if ( his_box[ii][jj] != 0 ||  box[ii][jj] !=0 )
						continue;

					stack_i.push(i);
					stack_j.push(j);
					stack_dir.push(dir);

					i = ii;
					j = jj;
					dir = -1;

					his_box[i][j] = 3;
					int rating = weight_box[i][j] - weight_box[bi][bj];
					if (rating >= -2) // to reduce returned count, backward step won't returned
						vec.add(  new WeightStep( side, rating, bi, bj, i, j, 1) );
				}
			} // end for
		}// end while
	}

	private static void get_normal_path(int bi, int bj, int box[][], int weight_box[][], ArrayList<WeightStep> vec) {
		int side = box[bi][bj];
		for( int dir=0; dir < 6; dir++){
			int i = bi + direct[dir][0];
			int j = bj + direct[dir][1];

			if ( ! in_range(i,box) ||  ! in_range(j,box) )
				continue;

			if ( box[i][j] == 0){
				int rating = weight_box[i][j] - weight_box[bi][bj];
				if (rating >= -2)
					vec.add(  new WeightStep( side, rating, bi, bj, i, j, 1) );
			}
		}
	}

	/**
	 * Get all possible path for a chess.
	 * The Path is a list of Steps and returned in the last parameter
	 * weight_box is given to evaluate step ratings
	 */
	public static void get_path(int bi, int bj, int box[][], int weight_box[][], ArrayList<WeightStep> vec) {
		/* To reduce program complexity, it include get normal path and get jump path.
		*  get jump path is for jumping cross other chess.
		*/
		get_normal_path(bi,bj,box,weight_box,vec);
		get_jump_path(bi,bj,box,weight_box,vec);
	}
}