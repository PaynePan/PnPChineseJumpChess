package com.pnp.jpchess.common;

/*
 * This file is part of "PnP Chinese Jump Chese".
 *
 * Author 2016 Payne Pan
 *
 * "PnP Chinese Jump Chese" is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * "PnP Chinese Jump Chese" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with minimax4j. If not, see <http://www.gnu.org/licenses/lgpl.html>
 */

public class WeightStep extends Step  implements Comparable<WeightStep> {
	public int rating;
	int mode;

	public WeightStep(int side,int rating,int i, int j, int new_i, int new_j,int mode){
		super(side, i,j,new_i,new_j);
		this.rating = rating;
		this.mode = mode;
	}

	public int compareTo(WeightStep arg0) {
		int r = ((WeightStep)arg0).rating;
		if ( this.rating < r)
			return -1;
		else if( this.rating > r)
			return 1;
		return 0;
	}
	
}
