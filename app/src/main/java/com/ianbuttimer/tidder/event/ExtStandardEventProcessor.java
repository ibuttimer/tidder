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

package com.ianbuttimer.tidder.event;

import android.net.Uri;

import com.ianbuttimer.tidder.data.QueryCallback;
import com.ianbuttimer.tidder.data.provider.ProviderUri;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;

public class ExtStandardEventProcessor implements StandardEventProcessor.IStandardEventProcessorExt {

    private IStandardEventProcessor mHost;


    @Override
    public void setHost(IStandardEventProcessor host) {
        mHost = host;
    }

    @Override
    public boolean onStandardEvent(StandardEvent event) {
        boolean handled = true;

        if (event.isSettingsRequest()) {
            QueryCallback<StandardEvent> cpStdEventHandler = mHost.getCpStdEventHandler();
            if (Uri.EMPTY.equals(ProviderUri.CONFIG_CONTENT_URI)) {
                // nothing to do
                PostOffice.postEvent(StandardEvent.newSettingsResult(), event.getAddresses());
            } else if (cpStdEventHandler != null) {
                cpStdEventHandler.queryList(mHost.getActivity(),
                        mHost.getLoaderId(event),
                        event,
                        ProviderUri.CONFIG_CONTENT_URI);
            }
        } else {
            handled = false;
        }

        return handled;
    }
}
