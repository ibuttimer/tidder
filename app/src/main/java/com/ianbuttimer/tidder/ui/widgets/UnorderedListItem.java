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

package com.ianbuttimer.tidder.ui.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ianbuttimer.tidder.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Widget to display an unordered list item<br>
 */

public class UnorderedListItem extends LinearLayout {

    @BindView(R.id.tv_ul_bullet) TextView tvBullet;
    @BindView(R.id.tv_ul_item) TextView tvItem;

    public UnorderedListItem(Context context) {
        this(context, null);
    }

    public UnorderedListItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnorderedListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        @StringRes int bulletId = R.string.unordered_list_bullet;
        @StringRes int itemId = 0;
        @StyleRes int appearance = android.R.style.TextAppearance_Small;
        @IntegerRes int alignment = R.integer.text_alignment;
        @ColorRes int colour = R.color.colorTextDefault;
        @ColorRes int colourLink = R.color.colorPrimaryDark;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UnorderedListItem, 0, 0);
            bulletId = a.getResourceId(R.styleable.UnorderedListItem_bulletId, bulletId);
            itemId = a.getResourceId(R.styleable.UnorderedListItem_itemId, itemId);
            appearance = a.getResourceId(R.styleable.UnorderedListItem_textAppearance, appearance);
            alignment = a.getResourceId(R.styleable.UnorderedListItem_textAlignment, alignment);
            colour = a.getResourceId(R.styleable.UnorderedListItem_textColor, colour);
            colourLink = a.getResourceId(R.styleable.UnorderedListItem_textColorLink, colourLink);

            a.recycle();
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View view = inflater.inflate(R.layout.unordered_list_item, this, true);

            ButterKnife.bind(this, view);

            Resources resources = context.getResources();
            @ColorInt int textColour = resources.getColor(colour);
            @ColorInt int textLinkColour = resources.getColor(colourLink);
            int textAlignment = resources.getInteger(alignment);
            for (TextView tv : new TextView[]{tvItem, tvBullet}) {
                tv.setTextAppearance(context, appearance);
                tv.setTextAlignment(textAlignment);
                tv.setTextColor(textColour);
                tv.setLinkTextColor(textLinkColour);
            }

            tvBullet.setText(bulletId);
            if (itemId != 0) {
                tvItem.setText(itemId);
            } else {
                tvItem.setText(null);
            }
        }
    }

    /**
     * Sets the movement method (arrow key handler) to be used for this object
     * @param movement  MovementMethod
     */
    public void setMovementMethod(MovementMethod movement) {
        tvItem.setMovementMethod(movement);
    }
}
