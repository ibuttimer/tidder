/*
 * Copyright (C) 2018  Ian Buttimer
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ianbuttimer.tidder.ui;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.ui.widgets.UnorderedListItem;
import com.ianbuttimer.tidder.utils.Utils;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // set version string
        TextView tv = findViewById(R.id.tv_version_aboutA);
        tv.setText(Utils.getVersionString(getApplicationContext()));

        // make links work
        @IdRes int[] tvIds = new int[] {
                R.id.tv_licencelink_aboutA
        };
        for (int id : tvIds) {
            tv = findViewById(id);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
        @IdRes int[] ulliIds = new int[] {
                R.id.ulli_icons8_aboutA, R.id.ulli_icons_material_aboutA, R.id.ulli_noto_font_aboutA
        };
        for (int id : ulliIds) {
            UnorderedListItem item = findViewById(id);
            item.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
