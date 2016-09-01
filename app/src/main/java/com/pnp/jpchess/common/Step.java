package com.pnp.jpchess.common;

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


public class Step {
	public int side;
	public int i;
	public int j;
	public int new_i;
	public int new_j;
		
	public Step(){}
	public Step(int side, int i, int j, int new_i, int new_j){
		this.side = side;
		this.i = i;
		this.j = j;
		this.new_i = new_i;
		this.new_j = new_j;
	}
	
	public String toString(){
		return "side:"+ side +" old:" + i + " " + j + " new:" + new_i +" " + new_j;
	}
	
	public boolean equals(Object arg0) {
		if ( arg0 instanceof Step) {
			 Step step = (Step)arg0;

			if (  step.new_i == this.i
				  && step.new_j == this.j
				  && step.i == this.new_i
				  && step.j == this.new_j )
				return true;
		}
		return false;
	}
}