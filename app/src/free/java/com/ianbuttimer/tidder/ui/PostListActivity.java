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

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.ianbuttimer.tidder.event.StandardEventProcessor.IStandardEventProcessorExt;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;

import java.util.ArrayList;
import java.util.Map;

import timber.log.Timber;

/**
 * An activity representing a list of Posts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PostDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PostListActivity extends AbstractPostListActivity implements PostOffice.IAddressable {

    private static final String TAG = PostListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this, initializationStatus -> {
            for (Map.Entry<String, AdapterStatus> key : initializationStatus.getAdapterStatusMap().entrySet()) {
                Timber.i(key.getKey() + ": " + key.getValue().toString());
            }
        });
    }

    @Override
    public String getAddress() {
        return getActivityAddress();
    }

    public static String getActivityAddress() {
        return TAG;
    }

    @Override
    protected ArrayList<IStandardEventProcessorExt> getExtensions() {
        return null;
    }

}
