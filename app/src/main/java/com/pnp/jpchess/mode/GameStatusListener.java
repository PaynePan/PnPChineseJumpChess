package com.pnp.jpchess.mode;

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

public interface GameStatusListener {
	public void statusUpdate(Game game);
}
