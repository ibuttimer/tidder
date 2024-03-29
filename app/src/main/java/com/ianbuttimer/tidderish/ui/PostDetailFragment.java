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

package com.ianbuttimer.tidderish.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ianbuttimer.tidderish.R;
import com.ianbuttimer.tidderish.data.DatabaseIntentService;
import com.ianbuttimer.tidderish.data.PinnedQueryResponse;
import com.ianbuttimer.tidderish.data.provider.PinnedBuilder;
import com.ianbuttimer.tidderish.databinding.ContentPostBinding;
import com.ianbuttimer.tidderish.event.PostEvent;
import com.ianbuttimer.tidderish.event.StandardEvent;
import com.ianbuttimer.tidderish.net.GlideApp;
import com.ianbuttimer.tidderish.net.NetworkUtils;
import com.ianbuttimer.tidderish.net.UriUtils;
import com.ianbuttimer.tidderish.reddit.Comment;
import com.ianbuttimer.tidderish.reddit.ImageSource;
import com.ianbuttimer.tidderish.reddit.Link;
import com.ianbuttimer.tidderish.reddit.MediaEmbed;
import com.ianbuttimer.tidderish.reddit.OEmbed;
import com.ianbuttimer.tidderish.reddit.Preview;
import com.ianbuttimer.tidderish.reddit.PreviewImages;
import com.ianbuttimer.tidderish.reddit.RedditClient;
import com.ianbuttimer.tidderish.reddit.SecureMedia;
import com.ianbuttimer.tidderish.reddit.util.RedditMisc;
import com.ianbuttimer.tidderish.ui.widgets.BasicStatsView;
import com.ianbuttimer.tidderish.ui.widgets.PostOffice;
import com.ianbuttimer.tidderish.ui.widgets.ToastReceiver;
import com.ianbuttimer.tidderish.utils.ScreenUtils;
import com.ianbuttimer.tidderish.utils.Utils;

import java.text.MessageFormat;
import java.util.Objects;

import static com.ianbuttimer.tidderish.data.provider.BaseProvider.PinnedBase.NAME_EQ_SELECTION;
import static com.ianbuttimer.tidderish.ui.CommentThreadProcessor.DETAIL_ARGS;
import static com.ianbuttimer.tidderish.ui.CommentThreadProcessor.PARENT_ARGS;

/**
 * A fragment representing a single Post detail screen.
 * This fragment is either contained in a {@link PostListActivity}
 * in two-pane mode (on tablets) or a {@link PostDetailActivity}
 * on handsets.
 */
public class PostDetailFragment extends CommentThreadFragment implements PostOffice.IAddressable {

    public static final String TAG = PostDetailFragment.class.getSimpleName();

    private static final int TOAST_TITLE_LEN = 10;  // max length of title int pin/unpin toast

    private ContentPostBinding binding;

    private WebView wvSelfText;
    private ImageView imgThumbnail;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PostDetailFragment() {
        super();
    }

    @Override
    public ViewBinding getViewBinding() {
        binding = ContentPostBinding.inflate(getLayoutInflater());

        wvSelfText = binding.wvSelftextPostA;
        imgThumbnail = binding.imgThumbnailPostA;
        imgThumbnail.setOnClickListener(onImageClick);

        return binding;
    }

    @Override
    public ConstraintLayout getContents() {
        return binding.clContentPostOrThread;
    }

    @Override
    public RecyclerView getRecyclerView() {
        return binding.incListingLayout.rvListListingL;
    }

    @Override
    public ProgressBar getProgressBar() {
        return binding.incListingLayout.pbProgressListingL;
    }

    @Override
    public TextView getMessageTv() {
        return binding.incListingLayout.tvMessageListingL;
    }

    @Override
    public TextView getTitleTv() {
        return binding.tvTitlePostOrThread;
    }

    @Override
    public BasicStatsView getBasicStatsView() {
        return binding.bsvPostOrThread;
    }

    @Override
    public void onActivityCreated() {
        FloatingActionButton fabPin = mProcessor.getFabPin();
        if (fabPin != null) {
            fabPin.setOnClickListener(this::onPinClick);
        }
    }

    @Override
    public void onStart(boolean emptyList) {
        if (emptyList) {
            Activity activity = getActivity();
            if (activity != null) {
                mProcessor.postEvent(StandardEvent.newPinnedListRequest(mProcessor.getName())
                                        .addAddress(activity.getClass().getSimpleName()));
            }

            imgThumbnail.setVisibility(View.INVISIBLE);
        } else {
            setPostThumbnail();
            mProcessor.setPinned(mProcessor.isPinned());
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // cancel any pending loads
        GlideApp.with(this).clear(imgThumbnail);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public boolean onPostEvent(PostEvent event) {
        boolean handled = true;

        if (event.isViewThreadRequest()) {
            Bundle args = mProcessor.getDetailArgs(event);
            if (mProcessor.isTwoPane()) {
                FragmentActivity activity = getActivity();
                if (activity != null) {

                    CommentThreadDialog dialog = new CommentThreadDialog();
                    dialog.setArguments(args);

                    dialog.show(activity.getSupportFragmentManager(), dialog.getClass().getSimpleName());
                }
            } else {
                Intent intent = new Intent(getActivity(), CommentThreadActivity.class);
                intent.putExtra(DETAIL_ARGS, args);
                intent.putExtra(PARENT_ARGS, mProcessor.getParentArgs());
                Utils.startActivity(getActivity(), intent);
            }
        } else {
            handled = false;
        }
        return handled;
    }

    @Override
    public void processGetCommentTreeResult(Link link) {
        String selfText = link.getSelfTextHtml();
        if (TextUtils.isEmpty(selfText)) {
            wvSelfText.setVisibility(View.GONE);

            // adjust constraints to constrain top of stats view to bottom of image
            mProcessor.addTopToBottomConstraint(R.id.bsv_post_or_thread, R.id.img_thumbnail_postA);
        } else {
            String summary = "<html><body>" + selfText + "</body></html>";
            wvSelfText.loadData(summary, "text/html", null);
        }

        SecureMedia secureMedia = link.getSecureMedia();
        if (secureMedia != null) {
            OEmbed oembed = secureMedia.getOembed();
            if (oembed != null) {
                //                            oembed.ge
            }
        }

        MediaEmbed video = link.getSecureMediaEmbed();
        if ((video != null) && !MediaEmbed.EMPTY.equals(video)) {
            String summary = "<html><body>"+video.getContent()+"</body></html>";
            wvSelfText.loadData(summary, "text/html", null);
        }

        if (link.isSelfThumbnail()) {
            // COMMENT TREE FLOW 8. post subreddit info request
            mProcessor.postEvent(StandardEvent.newSubredditInfoRequest(link.getSubreddit()));
        } else {
            setPostThumbnail();
        }
    }

    @Override
    public boolean onStandardEvent(StandardEvent event) {
        boolean handled = true;
        if (event.isPinnedListResult()) {
            PinnedQueryResponse response = event.getPinnedQueryResponse();
            if (response != null) {
                mProcessor.setPinned(response.getCount() > 0);
            }
        } else if (event.isSubredditInfoResult()) {
            // mostly handled in super class
            setPostThumbnail();
        } else {
            handled = false;
        }
        return handled;
    }

    private void setPostThumbnail() {
        Uri uri = null;
        Link link = mProcessor.getLink();
        if (link!= null) {
            Preview preview = link.getPreview();
            if (preview != null) {
                if (preview.isEnabled()) {
                    int scrnWidth = ScreenUtils.getScreenWidth(getActivity());
                    PreviewImages[] previewImages = preview.getPreview();
                    for (int i = 0; i < Objects.requireNonNull(previewImages).length && (uri == null); i++) {
                        if (!Utils.arrayItemIsNull(previewImages, i)) {
                            ImageSource[] resolutions = previewImages[i].getResolutions();
                            int selectedWidth = 0;
                            int bestIdx = -1;
                            for (int j = 0; (j < resolutions.length) && (selectedWidth < scrnWidth); j++) {
                                if (!Utils.arrayItemIsNull(resolutions, j)) {
                                    int thisWidth = resolutions[j].getWidth();
                                    if ((selectedWidth < thisWidth) && (thisWidth < scrnWidth)) {
                                        bestIdx = j;
                                        selectedWidth = thisWidth;
                                    }
                                }
                            }
                            if (bestIdx >= 0) {
                                uri = resolutions[bestIdx].getUrl();
                            }
                        }
                    }
                }
            }
            if (uri == null) {
                if (link.isLoadableThumbnail()) {
                    uri = RedditMisc.convertDefaultThumbnailUri(getResources(), link.getThumbnail());
                }
            }
        }
        if (uri != null) {
            uri = NetworkUtils.unescapeUri(uri);

            GlideApp.with(this)
                    .load(uri)
                    .placeholder(R.drawable.ic_picture)
//                    .fitCenter()
                    .into(imgThumbnail);
            imgThumbnail.setVisibility(View.VISIBLE);
        } else {
            imgThumbnail.setVisibility(View.GONE);

            @IdRes int idRes;
            if (wvSelfText.getVisibility() == View.GONE) {
                // no self text or image so constrain stats
                idRes = R.id.bsv_post_or_thread;
            } else {
                // no image so constrain selftext
                idRes = R.id.wv_selftext_postA;
            }

            // adjust constraints to constrain top of stats/selftext to bottom of title
            mProcessor.addTopToBottomConstraint(idRes, R.id.tv_title_post_or_thread);
        }
    }

    private final View.OnClickListener onImageClick = view -> {
        Uri url = mProcessor.getLink().getUrl();
        if (UriUtils.actionable(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, url);
            Utils.startActivity(getActivity(), intent);
        }
    };

    @Override
    public void onItemClick(View view, Comment comment) {
        // no op
    }

    @Override
    public boolean onItemLongClick(View view, Comment comment) {
        return false;
    }

    @Override
    public void onItemDoubleClick(View view, Comment comment) {
        mProcessor.postEvent(
            PostEvent.newViewThreadRequest(mProcessor.getName(), mProcessor.getTitle(),
                                                comment.getPermalink()));
    }

    /**
     * Handle the pin button click
     * @param view - view on which event happened
     */
    public void onPinClick(View view) {
        Link link = mProcessor.getLink();
        if (link == null) {
            return;
        }
        String name = link.getName();
        if (TextUtils.isEmpty(name)) {
            return;
        }
        String title = link.getTitle();
        if (TextUtils.isEmpty(title)) {
            title = "";
        } else if (title.length() > TOAST_TITLE_LEN) {
            title = title.substring(0, TOAST_TITLE_LEN) + "...";
        }
        Context context = getContext();
        DatabaseIntentService.Builder builder;
        @StringRes int toastResult;

        boolean pinned = !mProcessor.isPinned();
        mProcessor.setPinned(pinned);  // toggle pinned flag
        if (context != null) {
            if (pinned) {
                ContentValues cv = PinnedBuilder.builder()
                        .uuid(RedditClient.getClient().getUserId())
                        .permalink(name)
                        .build();
                builder = DatabaseIntentService.Builder.builder(
                        context,
                        DatabaseIntentService.Actions.INSERT_OR_UPDATE_PINNED)
                        .cv(cv);

                toastResult = R.string.pinning_toast;
            } else {
                builder = DatabaseIntentService.Builder.builder(
                        context,
                        DatabaseIntentService.Actions.DELETE_PINNED);

                toastResult = R.string.unpinning_toast;

                // TODO should include uuid in delete sql
            }
            context.startService(builder
                    .selection(NAME_EQ_SELECTION)
                    .selectionArgs(name)
                    .resultReceiver(new ToastReceiver(context, MessageFormat.format(
                            context.getResources().getString(toastResult),
                            title)))
                    .build());
        }
    }

    @Override
    public String getAddress() {
        return getTabAddress();
    }

    public static String getTabAddress() {
        return TAG;
    }

}
