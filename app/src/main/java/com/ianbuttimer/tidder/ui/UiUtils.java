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

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ianbuttimer.tidder.TidderApplication;
import com.ianbuttimer.tidder.utils.ScreenUtils;

/**
 * Ui-related utility functions
 */

public class UiUtils {

    public enum AppTypeface {
        NO_TYPEFACE, NOTO_SANS_REGULAR, NOTO_SANS_ITALIC
    }

    private static final Typeface mNotoSansRegular;
    private static final Typeface mNotoSansItalic;
    static {
        AssetManager assetManager = TidderApplication.getWeakApplicationContext().get().getAssets();
        mNotoSansRegular = Typeface.createFromAsset(assetManager, "NotoSans-Regular.ttf");
        mNotoSansItalic = Typeface.createFromAsset(assetManager, "NotoSans-Italic.ttf");
    }

    /**
     * Display the view's content description in a toast
     * @param view     View
     */
    public static void processContentDescription(View view) {
        DisplayMetrics metrics = new DisplayMetrics();

		/* buttons from a fragment come in here with a ContextThemeWrapper */

        WindowManager mgr;
        Context appCtx;
        Object obj = view.getContext();
        if (obj instanceof Activity) {

            Activity a = (Activity) view.getContext();
            appCtx = a.getApplicationContext();
            mgr = a.getWindowManager();
            a.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        } else {
            ContextThemeWrapper wrapper = (ContextThemeWrapper)view.getContext();
            appCtx = wrapper.getApplicationContext();
            mgr = (WindowManager)wrapper.getSystemService(Context.WINDOW_SERVICE);
        }
        if (mgr != null) {
            mgr.getDefaultDisplay().getMetrics(metrics);

            Toast toast = Toast.makeText(appCtx, view.getContentDescription(), Toast.LENGTH_LONG);

            int[] location = new int[2];
            view.getLocationOnScreen(location);

            int top = location[1] - view.getHeight();
            top = ScreenUtils.convertPixelsToDp(appCtx, top);
            if (top < 0) {
                top = 0;
            }

            toast.setGravity(Gravity.TOP, 0, top);
            toast.show();
        }
    }

    /**
     * Get NotoSans regular
     * @return Typeface
     */
    public static Typeface getNotoSansRegular() {
        return mNotoSansRegular;
    }

    /**
     * Get NotoSans italic
     * @return Typeface
     */
    public static Typeface getNotoSansItalic() {
        return mNotoSansItalic;
    }

    /**
     * Set typeface
     */
    public static void setTypeface(TextView textView, AppTypeface typeface) {
        if (textView != null) {
            Typeface type = null;   // default NO_TYPEFACE
            boolean set = true;
            switch (typeface) {
                case NOTO_SANS_REGULAR:
                    type = getNotoSansRegular();
                    break;
                case NOTO_SANS_ITALIC:
                    type = getNotoSansItalic();
                    break;
                default:
                    set = false;    // do nothing
                    break;
            }
            if (set) {
                textView.setTypeface(type);
            }
        }
    }
}
