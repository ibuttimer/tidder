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

package com.ianbuttimer.tidderish.ui.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import android.text.Spanned;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.databinding.UnorderedListItemBinding;


/**
 * Widget to display an unordered list item<br>
 */

public class UnorderedListItem extends LinearLayout {

    private TextView tvBullet;
    private TextView tvItem;

    @StyleRes private int mAppearance = android.R.style.TextAppearance_Small;
    @IntegerRes private int mAlignment = R.integer.text_alignment;
    @ColorRes private int mColour = R.color.colorTextDefault;
    @ColorRes private int mColourLink = R.color.colorPrimaryDark;

    public UnorderedListItem(Context context) {
        this(context, null);
    }

    public UnorderedListItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UnorderedListItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        @StringRes int bulletId = R.string.unordered_list_bullet;
        @StringRes int textId = 0;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UnorderedListItem, 0, 0);
            bulletId = a.getResourceId(R.styleable.UnorderedListItem_bulletId, bulletId);
            textId = a.getResourceId(R.styleable.UnorderedListItem_textId, textId);
            mAppearance = a.getResourceId(R.styleable.UnorderedListItem_textAppearance, mAppearance);
            mAlignment = a.getResourceId(R.styleable.UnorderedListItem_textAlignment, mAlignment);
            mColour = a.getResourceId(R.styleable.UnorderedListItem_textColor, mColour);
            mColourLink = a.getResourceId(R.styleable.UnorderedListItem_textColorLink, mColourLink);

            a.recycle();
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            UnorderedListItemBinding binding = UnorderedListItemBinding.inflate(inflater, this, true);

            tvBullet = binding.tvUlBullet;
            tvItem = binding.tvUlItem;

            setTexts(bulletId, textId);
            setStyle();
        }
    }

    private void setTexts(@StringRes int bulletId, @StringRes int itemId) {
        setBullet(bulletId);
        setText(itemId);
    }

    private void setStyle() {
        Context context = tvBullet.getContext();
        Resources resources = context.getResources();
        @ColorInt int textColour = resources.getColor(mColour);
        @ColorInt int textLinkColour = resources.getColor(mColourLink);
        int textAlignment = resources.getInteger(mAlignment);
        for (TextView tv : new TextView[]{tvItem, tvBullet}) {
            tv.setTextAppearance(context, mAppearance);
            tv.setTextAlignment(textAlignment);
            tv.setTextColor(textColour);
            tv.setLinkTextColor(textLinkColour);
        }
    }


    public void setBullet(@StringRes int bulletId) {
        tvBullet.setText(bulletId);
    }

    public void setText(@StringRes int textId) {
        if (textId != 0) {
            tvItem.setText(textId);
        } else {
            tvItem.setText(null);
        }
    }

    public void setText(String text) {
        tvItem.setText(text);
    }

    public void setText(Spanned text) {
        tvItem.setText(text);
    }

    public void setAppearance(@StyleRes int appearance) {
        mAppearance = appearance;
        setStyle();
    }

    public void setAlignment(@IntegerRes int alignment) {
        mAlignment = alignment;
        setStyle();
    }

    public void setColour(@ColorRes int colour) {
        mColour = colour;
        setStyle();
    }

    public void setColourLink(@ColorRes int colourLink) {
        mColourLink = colourLink;
        setStyle();
    }


    /**
     * Sets the movement method (arrow key handler) to be used for this object
     * @param movement  MovementMethod
     */
    public void setMovementMethod(MovementMethod movement) {
        tvItem.setMovementMethod(movement);
    }
}
