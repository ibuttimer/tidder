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

package com.ianbuttimer.tidderish.data;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_ERROR_CODE;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_ERROR_STRING;
import static com.ianbuttimer.tidderish.data.ICallback.CONTENT_PROVIDER_RESULT_TYPE;


/**
 * Class to asynchronously handle a call to the ContentProvider <code>call</code> interface.<br>
 */

public class ContentProviderCallLoader extends ContentProviderLoader {

    /**
     * Constructor
     * @param context   Current context
     * @param args      Loader argument bundle
     */
    public ContentProviderCallLoader(Context context, Bundle args) {
        super(context, args);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public AbstractResultWrapper loadInBackground() {

        // Extract the call arguments from the args
        Extractor extractor = getExtractor(args);
        Uri uri = extractor.getUri();
        String method = extractor.getMethod();
        String arg = extractor.getArg();
        Bundle extras = extractor.getExtras();

        if ((uri == null) || TextUtils.isEmpty(method)) {
            return null;    // can't do anything
        }

        Context context = getContext();
        Bundle bundle = context.getContentResolver().call(uri, method, arg, extras);

        AbstractResultWrapper result = null;
        if (bundle != null) {
            // get resultText of type appropriate to resultText data
            AbstractResultWrapper.ResultType resultType =
                    AbstractResultWrapper.ResultType.getFromBundle(bundle, CONTENT_PROVIDER_RESULT_TYPE);
            switch (resultType) {
                case STRING:
                    result = new ICallback.UrlProviderResultWrapper(uri, bundle.getString(method));
                    break;
                case BUNDLE:
                    result = new ICallback.UrlProviderResultWrapper(uri, bundle.getBundle(method));
                    break;
                case ERROR:
                    result = new ICallback.UrlProviderResultWrapper(uri,
                            bundle.getInt(CONTENT_PROVIDER_ERROR_CODE),
                            bundle.getString(CONTENT_PROVIDER_ERROR_STRING));
                    break;
            }
        }
        if (result != null) {
            result.setAdditionalInfo(extractor.getAdditionalInfo());    // echo back additional info
        }
        return result;
    }
}
