package com.pnp.jpchess.mode;

import java.util.ArrayList;

import java.util.List;
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


public abstract class User implements GameStatusListener {
	protected String name;
	protected Game m_game;
	protected int side;
	
	public String getName(){
		return name;
	}
	
	public void setName(String m){
		this.name = m;
	}

	public int getSide() {
	    return side;
	}
	
	public boolean equals(Object object) {
		if(this == object) {
		    return true;
		}
		if(object instanceof User) {
			User user = (User)object;
		    if(name.equals(user.name)) {
		     return true;
		    }
		}
		return false;
	}

	public void setGame(Game game){
		m_game = game;
	}

	public Game getGame(){
		return m_game;
	}
	
	abstract public void statusUpdate(Game game);

   public boolean isMyTurn() {
        return m_game.status ==  side;
   }

}
