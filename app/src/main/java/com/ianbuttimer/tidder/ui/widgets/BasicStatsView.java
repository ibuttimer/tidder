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
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.utils.Utils;

import java.text.MessageFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.ianbuttimer.tidder.utils.Utils.getCountIndication;

/**
 * Widget to display basic stats for a object implementing the IBasicStats interface<br>
 * Usage:<br>
 * <ol>
 *     <li>Create a layout with a ConstraintLayout root</li>
 *     <li>To include author information add a TextView with id <code>tv_author_basic_stats</code></li>
 *     <li>To include score information add a TextView with id <code>tv_score_basic_stats</code></li>
 *     <li>To include comment information add a TextView with id <code>tv_comments_basic_stats</code></li>
 *     <li>To include age information add a TextView with id <code>tv_age_basic_stats</code></li>
 *     <li>Add a BasicStatsView element to your layout and set the <code>app:layoutId</code> attribute to the id of your custom layout</li>
 *     <li>After your layout has been inflated, call the setViewInfo(IBasicStats) to display the information</li>
 * </ol>
 */

public class BasicStatsView extends ConstraintLayout {

    @Nullable @BindView(R.id.tv_author_basic_stats) TextView tvAuthor;
    @Nullable @BindView(R.id.tv_score_basic_stats) TextView tvScore;
    @Nullable @BindView(R.id.tv_comments_basic_stats) TextView tvComments;
    @Nullable @BindView(R.id.tv_age_basic_stats) TextView tvAge;

    public BasicStatsView(Context context) {
        this(context, null);
    }

    public BasicStatsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasicStatsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        @LayoutRes int layoutId = 0;
        if ( attrs != null ) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BasicStatsView, 0, 0);
            layoutId = a.getResourceId(R.styleable.BasicStatsView_layoutId, 0);

            a.recycle();
        }

        if (layoutId != 0) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                View view = inflater.inflate(layoutId, this, true);

                ButterKnife.bind(this, view);
            }
        } else {
            Timber.e("No layout specified for %s", getClass());
        }
    }

    public void setViewInfo(IBasicStats source) {
        Context context = getContext();

        if (tvScore != null) {
            Utils.setCountIndication(tvScore, source.getScore(), 0, 0);
        }
        if (tvComments != null) {
            Utils.setCountIndication(tvComments, source.getNumComments(), R.string.item_comment, R.string.item_comments);
        }
        if (tvAge != null) {
            Date created = source.getCreated();
            if (created != null) {
                long current = System.currentTimeMillis();
                String age = Utils.getRelativeTimeSpanString(created.getTime(), current);
                tvAge.setText(age);
            }
        }
        if (tvAuthor != null) {
            String html = MessageFormat.format(
                    context.getString(R.string.link_author_format), source.getAuthor());
            tvAuthor.setText(Html.fromHtml(html)
            );
        }
    }


    public interface IBasicStats {

        @Nullable String getAuthor();
        int getScore();
        int getNumComments();
        @Nullable Date getCreated();
    }

}
