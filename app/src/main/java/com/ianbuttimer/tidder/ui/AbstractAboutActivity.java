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

import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.ui.widgets.UnorderedListItem;
import com.ianbuttimer.tidder.utils.Utils;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class AbstractAboutActivity extends AppCompatActivity {

    @BindView(R.id.rl_aboutA) RelativeLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);

        // set version string
        TextView tv = findViewById(R.id.tv_version_aboutA);
        tv.setText(Utils.getVersionString(getApplicationContext()));

        // make links work
        @IdRes int[] tvIds = new int[] {
                R.id.tv_licencelink_aboutA, R.id.tv_privacylink_aboutA
        };
        for (int id : tvIds) {
            tv = findViewById(id);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }

        // add acknowledgements
        // list of common acks
        @StringRes int[] ids = new int[] {
                R.string.ack_icons8, R.string.ack_icons_material, R.string.ack_noto_font,
                R.string.ack_okhttp, R.string.ack_timber, R.string.ack_butterknife,
                R.string.ack_glide, R.string.ack_parceler, R.string.ack_eventbus,
                R.string.ack_recyclerviewhelper, R.string.ack_multilinecollapsingtoolbar,
                R.string.ack_guava, R.string.ack_gson, R.string.ack_markdownview,
        };
        // list of variant specific acks
        @StringRes int[] specificIds = getSpecificAcks();

        if ((specificIds != null) && (specificIds.length > 0)) {
            int len = ids.length;
            ids = Arrays.copyOf(ids, (len + specificIds.length));
            System.arraycopy(specificIds, 0, ids, len, specificIds.length);
        }

        @IdRes int aboveId = R.id.tv_acknowledgements_aboutA;

        for (int id : ids) {
            UnorderedListItem item = new UnorderedListItem(this);
            item.setText(id);

            int viewId = View.generateViewId();
            item.setId(viewId);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, aboveId);

            item.setLayoutParams(params);

            mLayout.addView(item);
            item.setMovementMethod(LinkMovementMethod.getInstance());

            aboveId = viewId;
        }
        mLayout.invalidate();
    }

    @StringRes protected abstract int[] getSpecificAcks();
}
