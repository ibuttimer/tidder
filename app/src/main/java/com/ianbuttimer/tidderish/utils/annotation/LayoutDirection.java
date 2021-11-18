package com.ianbuttimer.tidderish.utils.annotation;

import android.view.View;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/** @hide */
@Retention(SOURCE)
public @interface LayoutDirection {
    /** Defines the allowed constants for this element */
    int[] value() default {View.LAYOUT_DIRECTION_LTR, View.LAYOUT_DIRECTION_RTL,
                            View.LAYOUT_DIRECTION_INHERIT, View.LAYOUT_DIRECTION_LOCALE};

    /** Defines whether the constants can be used as a flag, or just as an enum (the default) */
    boolean flag() default false;
}
