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

package com.ianbuttimer.tidderish.reddit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

/**
 * Base class for receiving responses
 */

public class ResponseReceiver extends ResultReceiver {

    protected String mResultText;

    protected int mHttpCode;

    public ResponseReceiver() {
        super(new Handler(Looper.getMainLooper()));
    }

    public ResponseReceiver(Handler handler) {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        ClientService.ResponseExtractor extractor = ClientService.getResponseExtractor(resultData);

        mHttpCode = extractor.httpCode();
        mResultText = extractor.resultText();
    }


    public interface IResponseReceiver {

        void onReceiveResult(int resultCode, Bundle resultData);
    }

}
