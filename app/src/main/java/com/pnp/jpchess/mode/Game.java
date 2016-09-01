package com.pnp.jpchess.mode;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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

public class Game {
	private String name;
	
	private ArrayList<User> m_users = new ArrayList<User>();

	public int status = Config.PLAY_INIT;

	public void setStatus(int status){
	}
	public int getStatus(){
		return status;
	}

	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

    public int[][] getBox(){
        return box;
    }
	
	public void setUsers(String data){
	}
	
	public ArrayList<User> getUsers(){
		return m_users;
	}

	public String toString(){
		return "status: "+ status;
	}
	
	public void nofityUsers(Game game){
		if ( gameStatusListener != null)
			gameStatusListener.statusUpdate(this);

		Iterator<User> it = m_users.iterator();
		while ( it.hasNext()){
			it.next().statusUpdate(game);
		}
	}

	private GameStatusListener gameStatusListener;
	public void setStatusListener( GameStatusListener l ) { gameStatusListener = l; }

	public void join(User user){
		if (m_users.size() >= 2 )
			return;
		if (m_users.contains(user) )
			return;
		
		m_users.add(user);
		user.setGame(this);
	}

	public void leave(User user){
		removeUser(user);
		removeAI();
	}
	
	private void removeUser(User user) {
		m_users.remove(user);
		user.setGame(null);
	}


	private void removeAI(){
		ArrayList<User> clone = new ArrayList<User>();
		clone.addAll(m_users);
		for( int i=0; i < clone.size(); i++) {
			User user = clone.get(i);
			if ( user instanceof UserAI) {
				removeUser(user);
			}
		}
	}
	
	public List<User> listUsers(){
		return m_users;
	}

	private int[][] box = null;
	private int[][] finalBox = null;
	private LinkedList<Step> hisSteps = new LinkedList<Step>();

	private static Game instance = new Game();
	static public Game instance() {
		return instance;
	}

   private Game(){
	   reset();;
   }
   
   private void reset() {
		box = new int[Config.BOX_RANG][Config.BOX_RANG];
		finalBox = new int[Config.BOX_RANG][Config.BOX_RANG];
		Util.init_box(box);

		Util.init_box(finalBox);
		Util.flip_box(finalBox);
		status = Config.PLAY_INIT;

	   removeUser(UserHuman.instance());
	   this.removeAI();
	   hisSteps.clear();
   }

	public static final byte MODE_PLAYER_ONE = 0x01;
	public static final byte MODE_PLAYER_TWO = 0x02;
	public static final byte MODE_PLAYER_ALLAI = 0x04;
	public static final byte MODE_LEVEL_NOMAL = 0x10;
	public static final byte MODE_LEVEL_ADVANCED = 0x20;

	private int mode;
	public int getMode() { return mode; }

	public void startGame() {
		startGame(this.mode);
	}

	public void startGame(int mode){
		this.mode = mode;
		if ((mode & MODE_LEVEL_ADVANCED) != 0) {
			Config.AI_LEVEL = 4;
			Config.BOX_RANG = 7;
			Config.CHESS_RANG = 4;
		} else {
			Config.AI_LEVEL = 4;
			Config.BOX_RANG = 6;
			Config.CHESS_RANG = 3;
		}
		reset();

		if ((mode & MODE_PLAYER_ONE) != 0) {
			Game.instance().join(UserHuman.instance());
		}

		while (m_users.size() < 2) {
			User ai = null;
			if ( Config.AI_TYPE == Config.AI_ALPHA_BETA) {
				ai = new UserAI_AlphaBeta(m_users.size() + 1);
			} else if ( Config.AI_TYPE == Config.AI_BRADTH_FIRST) {
				ai = new UserAI_BreadthFirst(m_users.size() + 1);
			}

			String aiName = "AI " + UUID.randomUUID();
			ai.setName( aiName.substring(0, 6));
			join(ai);
		}
		status = Config.PLAY_1_TURN;
		this.nofityUsers(this);

	}
	
	public boolean acceptStep(Step step){
		if ( this.status != step.side)
			return false;

		moveStep(step);

		if ( checkWin( step.side))
			this.status = step.side + 10;
		else {
			this.status = step.side == 1 ? 2 : 1;
		}

		this.nofityUsers(this);
		return true;
	}

	public boolean checkWin(int side) {
		return checkWin(this.getBox(),side);
	}

	public boolean checkWin(int[][] box,int side) {
		int len = box.length;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				if (finalBox[i][j] == side && box[i][j] != side)
					return false;
			}
		}
		return true;
	}

	public void moveStep(Step step) {
		box[step.new_i][step.new_j] = box[step.i][step.j];
		box[step.i][step.j] = 0;
		hisSteps.push(step);
	}

	public void undo() {
		if ((mode & Game.MODE_PLAYER_ALLAI) !=0 || getPlayingSide() != 1)
			return;

		if (hisSteps.size() >=2) {
			Step step = hisSteps.pop();
			box[step.i][step.j] = box[step.new_i][step.new_j];
			box[step.new_i][step.new_j] = 0;

			step = hisSteps.pop();
			box[step.i][step.j] = box[step.new_i][step.new_j];
			box[step.new_i][step.new_j] = 0;
		}
	}

	public boolean canMove(Step step){
		ArrayList<WeightStep> steps = new ArrayList<WeightStep>();
		int[][] cloneBox = new int[Config.BOX_RANG][Config.BOX_RANG];
		Util.get_path( step.i,step.j,box, cloneBox,steps);

		if ( steps.isEmpty())
			return false;

		Iterator<WeightStep> it = steps.iterator();
		while( it.hasNext()){
			WeightStep st = it.next();
			if ( step.new_i == st.new_i && step.new_j == st.new_j )
				return true;
		}
		return false;
	}
	
	public int getPlayingSide(){
		return this.status > 10? this.status - 10: this.status;
	}
	
	public boolean isUserTurn(String name){
		int side = getPlayingSide();
		if( ((User)m_users.get(side-1)).getName().equals(name) ){
			return true;
		}
		return false;
	}
}