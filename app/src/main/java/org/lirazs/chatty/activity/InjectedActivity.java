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

package org.lirazs.chatty.activity;

import android.support.v7.app.AppCompatActivity;

import org.lirazs.chatty.app.ChattyApplication;

import butterknife.ButterKnife;

/**
 * Created by mac on 2/17/17.
 */

public class InjectedActivity extends AppCompatActivity {

    protected ChattyApplication.ApplicationComponent component() {
        return ((ChattyApplication) getApplication()).component();
    }

    protected void injectViews() {
        ButterKnife.bind(this);
    }
}
