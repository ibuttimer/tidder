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
package com.ianbuttimer.tidder.data.adapter;

import android.content.Context;
import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.net.NetworkUtils;
import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.utils.ColourUtils;

import github.nisrulz.recyclerviewhelper.RVHViewHolder;
import timber.log.Timber;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static com.ianbuttimer.tidder.net.RedditUriBuilder.BASE_URL;
import static com.ianbuttimer.tidder.net.RedditUriBuilder.SUBREDDIT_RELATIVE_START_URL;


/**
 * A base RecyclerView.ViewHolder for BaseObject objects
 */
@SuppressWarnings("unused")
public abstract class AbstractViewHolder<T extends BaseObject>
                    extends RecyclerView.ViewHolder
                    implements View.OnClickListener, View.OnLongClickListener, RVHViewHolder {

    enum Listeners { NONE, CLICK, LONG_CLICK, CLICK_AND_LONG };

    private final View mView;
    private IAdapterHandler mAdapterHandler;

    private int mActionState;

    /**
     * Constructor
     * @param view          View to hold
     * @param clickHandler  onClick handler for view
     */
    public AbstractViewHolder(View view, IAdapterHandler clickHandler) {
        this(view, clickHandler, Listeners.CLICK_AND_LONG);
    }

    /**
     * Constructor
     * @param view          View to hold
     * @param clickHandler  onClick handler for view
     * @param setListener   Set click listener for view
     */
    public AbstractViewHolder(View view, IAdapterHandler clickHandler, Listeners setListener) {
        super(view);

        mView = view;
        mAdapterHandler = clickHandler;

        mActionState = ACTION_STATE_IDLE;

        switch (setListener) {
            case CLICK_AND_LONG:
            case LONG_CLICK:
                view.setOnLongClickListener(this);
                if (Listeners.LONG_CLICK.equals(setListener)) {
                    break;
                }
                // fall through
            case CLICK:
                view.setOnClickListener(this);
                break;
        }


        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                boolean consumed = false;
                if (mAdapterHandler != null) {
                    consumed = mAdapterHandler.onKey(view, keyCode, keyEvent);
                }
                return consumed;
            }
        });
    }

    /**
     * Get a context reference
     * @return  context reference
     */
    public Context getContext() {
        return mView.getContext();
    }

    /**
     * Set the details to display
     * @param info   Information object to use
     * @param position   The position of the item within the adapter's data set.
     */
    public abstract void setViewInfo(T info, int position);

    public View getView() {
        return mView;
    }

    public IAdapterHandler getAdapterHandler() {
        return mAdapterHandler;
    }

    public int getActionState() {
        return mActionState;
    }

    @Override
    public void onClick(View view) {
        // pass the click onto the click handler
        if (mAdapterHandler != null) {
            mAdapterHandler.onItemClick(view);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        // pass the click onto the click handler
        boolean handled = false;
        if (mAdapterHandler != null) {
            handled = mAdapterHandler.onItemLongClick(view);
        }
        return handled;
    }

    /**
     * Set the adapter handler
     * @param adapterHandler  Handler for view
     */
    public void setAdapterHandler(IAdapterHandler adapterHandler) {
        this.mAdapterHandler = adapterHandler;
    }


    protected void setLinkMovement(TextView view) {

        // Linkify.addLinks clears existing links
        // get around this by merging the links based on
        // https://stackoverflow.com/a/37853641/4054609
        SpannableString existingLinks = new SpannableString(view.getText());

        SpannableString webLinks = new SpannableString(view.getText());
        Linkify.addLinks(webLinks, Linkify.WEB_URLS);

        view.setText(mergeUrlSpans(webLinks, existingLinks));

        URLSpan[] urls = view.getUrls();
        boolean hasUrl = (urls.length > 0);
        MovementMethod method = null;
        if (hasUrl) {
            method = LinkMovementMethod.getInstance();
        }
        view.setMovementMethod(method);
        view.setFocusable(hasUrl);
    }

    private SpannableString mergeUrlSpans(SpannableString span1, SpannableString span2) {
        URLSpan[] spans = span2.getSpans(0, span2.length() , URLSpan.class);
        for (URLSpan span : spans) {
            span1.setSpan(span, span2.getSpanStart(span), span2.getSpanEnd(span), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span1;
    }


    protected void setText(TextView textView, String html) {
        textView.setText(
                Html.fromHtml(html),
                TextView.BufferType.SPANNABLE
        );
        Spannable spannable = (Spannable) textView.getText();
        URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (URLSpan span : spans) {
            String url = span.getURL();
            if (url.startsWith(SUBREDDIT_RELATIVE_START_URL)) {
                // looks like a relative subreddit url, replace with http link
                int start = spannable.getSpanStart(span);
                int end = spannable.getSpanEnd(span);
                int flags = spannable.getSpanFlags(span);
                spannable.removeSpan(span);
                URLSpan myUrlSpan = new URLSpan(NetworkUtils.joinUrlPaths(BASE_URL, url));
                spannable.setSpan(myUrlSpan, start, end, flags);
            }
        }
    }

    /**
     * Set link colour to a contrasting colour
     * @param textView      TextView to set link colour
     * @param background    Colour to contrast against
     */
    protected void setLinkColour(TextView textView, @ColorInt int background) {
        textView.setLinkTextColor(
                ColourUtils.getFurthestColour(background, ColourUtils.APP_COLOURS));

    }


    // vvvvvvvv RVHViewHolder implementation vvvvvvvv

    @Override
    public void onItemSelected(int actionstate) {
        mActionState = actionstate;
        actionStateDbg();
    }

    @Override
    public void onItemClear() {
        mActionState = ACTION_STATE_IDLE;
        actionStateDbg();
    }

    private void actionStateDbg() {
        String state;
        switch (mActionState) {
            case ACTION_STATE_IDLE:
                state = "idle";
                break;
            case ACTION_STATE_SWIPE:
                state = "swipe";
                break;
            case ACTION_STATE_DRAG:
                state = "drag";
                break;
            default:
                state = "unknown";
                break;
        }
        Timber.d("Item id %d at position %d is %s (%d)",
                getItemId(), getAdapterPosition(), state, mActionState);
    }

    /**
     * Called from adapter when this view holder has been recycled.
     * @see RecyclerView.Adapter#onViewRecycled(RecyclerView.ViewHolder)
     */
    public void onViewRecycled() {
        // no op
    }
}
