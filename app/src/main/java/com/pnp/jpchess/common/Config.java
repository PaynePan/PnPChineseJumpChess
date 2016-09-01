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


public class Config {
	public static final int PLAY_INIT = 0;
	public static final int PLAY_1_TURN = 1;
	public static final int PLAY_1_WIN = 11;
	public static final int PLAY_2_TURN = 2;
	public static final int PLAY_2_WIN = 12;

	public static int BOX_RANG; // the size of the board
	public static int CHESS_RANG;  // row count of chessmen
	public static int AI_LEVEL; // AI search level

	public static final int AI_BRADTH_FIRST = 1; // AI search will apply breadth-first max-min method
	public static final int AI_ALPHA_BETA = 2;   // AI search will apply Alpha-beta method
	public static int AI_TYPE = AI_ALPHA_BETA; // AI chosen strategy
}
