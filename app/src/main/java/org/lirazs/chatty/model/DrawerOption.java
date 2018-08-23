/**
 * Copyright Liraz Shilkrot
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package org.lirazs.chatty.model;

public class DrawerOption extends DrawerItem  {

    private int nameRes;
    private int unselectedIconRes;
    private int selectedIconRes;

    public DrawerOption() {
        super(Type.OPTION);
    }
    public DrawerOption(Type type) {
        super(type);
    }

    public int getNameRes() {
        return nameRes;
    }

    public DrawerOption setNameRes(int nameRes) {
        this.nameRes = nameRes;
        return this;
    }

    public int getUnselectedIconRes() {
        return unselectedIconRes;
    }

    public DrawerOption setUnselectedIconRes(int unselectedIconRes) {
        this.unselectedIconRes = unselectedIconRes;
        return this;
    }

    public int getSelectedIconRes() {
        return selectedIconRes;
    }

    public DrawerOption setSelectedIconRes(int selectedIconRes) {
        this.selectedIconRes = selectedIconRes;
        return this;
    }
}