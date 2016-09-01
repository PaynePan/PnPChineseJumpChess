package com.pnp.jpchess.mode;

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

public class UserHuman extends User{
    
    private UserHuman() { name = "You"; side=1; }
    
    private static UserHuman instance = new UserHuman();
    public static UserHuman instance() {
        return instance;
    }

    public boolean isPlaying() {
        return this.m_game != null;
    }

    public void statusUpdate(Game game) {}
}

