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
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.TidderApplication;
import com.ianbuttimer.tidder.data.ApiResponseCallback;
import com.ianbuttimer.tidder.data.FullnameITester;
import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.data.ICallback;
import com.ianbuttimer.tidder.data.ITester;
import com.ianbuttimer.tidder.data.QueryCallback;
import com.ianbuttimer.tidder.data.adapter.AdapterSelectController;
import com.ianbuttimer.tidder.data.adapter.CommentAdapter;
import com.ianbuttimer.tidder.data.adapter.CommentMoreViewHolder;
import com.ianbuttimer.tidder.event.AbstractEvent;
import com.ianbuttimer.tidder.event.PostEvent;
import com.ianbuttimer.tidder.event.RedditClientEvent;
import com.ianbuttimer.tidder.event.StandardEvent;
import com.ianbuttimer.tidder.event.StandardEventProcessor;
import com.ianbuttimer.tidder.reddit.Comment;
import com.ianbuttimer.tidder.reddit.CommentCache;
import com.ianbuttimer.tidder.reddit.CommentMore;
import com.ianbuttimer.tidder.reddit.CommentProxy;
import com.ianbuttimer.tidder.reddit.Link;
import com.ianbuttimer.tidder.reddit.ListingTracker;
import com.ianbuttimer.tidder.reddit.RedditCache;
import com.ianbuttimer.tidder.reddit.RedditObject;
import com.ianbuttimer.tidder.reddit.Response;
import com.ianbuttimer.tidder.reddit.Subreddit;
import com.ianbuttimer.tidder.reddit.get.CommentMoreRequest;
import com.ianbuttimer.tidder.reddit.get.CommentMoreResponse;
import com.ianbuttimer.tidder.reddit.get.CommentTreeRequest;
import com.ianbuttimer.tidder.reddit.get.CommentTreeResponse;
import com.ianbuttimer.tidder.reddit.get.SubredditAboutResponse;
import com.ianbuttimer.tidder.reddit.get.ThingAboutResponse;
import com.ianbuttimer.tidder.ui.widgets.AutoMeasureLinearLayoutManager;
import com.ianbuttimer.tidder.ui.widgets.BasicStatsView;
import com.ianbuttimer.tidder.ui.widgets.EndlessRecyclerViewScrollListener;
import com.ianbuttimer.tidder.ui.widgets.NoUrlTextViewListItemClickListener;
import com.ianbuttimer.tidder.ui.widgets.PostOffice;
import com.ianbuttimer.tidder.ui.widgets.TypedGestureDetector;
import com.ianbuttimer.tidder.utils.Utils;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

import java.util.ArrayDeque;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import github.nisrulz.recyclerviewhelper.RVHItemDividerDecoration;
import github.nisrulz.recyclerviewhelper.RVHItemTouchHelperCallback;
import timber.log.Timber;

import static com.ianbuttimer.tidder.utils.PreferenceControl.AUTOEXPAND_OFF;
import static com.ianbuttimer.tidder.utils.PreferenceControl.getAutoExpandLevelPreference;

/**
 * A fragment representing a single Post detail screen.
 * This fragment is either contained in a {@link PostListActivity}
 * in two-pane mode (on tablets) or a {@link CommentThreadActivity}
 * on handsets.
 */
public class CommentThreadProcessor implements IAdapterHandler, PostOffice.IAddressable {

    public static final String TAG = CommentThreadProcessor.class.getSimpleName();

    protected static final String LIST = "list";
    protected static final String LINK = "link";
    protected static final String SUBREDDIT = "subreddit";
    protected static final String TRACKER = "tracker";
    protected static final String PINNED = "pinned";

    public static final String DETAIL_ARGS = "detail_args";
    public static final String LINK_NAME = "link_name";
    public static final String LINK_TITLE = "link_title";
    public static final String PERMALINK = "permalink";
    public static final String TWO_PANE = "2pane";
    public static final String THREAD = "thread";
    public static final String PARENT_ARGS = "parent_args";

    protected String mName = null;
    protected String mTitle = null;
    protected String mPermalink = null;
    protected boolean mPinned = false;
    protected boolean mThread = false;
    protected boolean mTwoPane = false;

    @BindView(R.id.cl_content_post_or_thread) ConstraintLayout clContent;
    @BindView(R.id.rv_list_listingL) RecyclerView rvList;
    @BindView(R.id.pb_progress_listingL) ProgressBar pbProgress;
    @BindView(R.id.tv_message_listingL) TextView tvMessage;
    @BindView(R.id.tv_title_post_or_thread) TextView tvTitle;
    @BindView(R.id.bsv_post_or_thread) BasicStatsView bsvView;

    @Nullable protected FloatingActionButton mFabPin;

    protected Link mLink = null;
    protected Subreddit mSubreddit = null;
    protected ListingTracker<Comment> mTracker;

    protected CommentAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Comment> mList;
    protected EndlessRecyclerViewScrollListener mScrollListener;

    protected ICallback<Response> mApiResponseHandler;
    protected ICallback<Response> mApiStdEventResponseHandler;
    protected QueryCallback<StandardEvent> mCpStdEventHandler;
    protected StandardEventProcessor mStdEventProcessor;

    protected AdapterSelectController mSelectCtrl;

    protected static final boolean mIsPinnable;

    static {
        Context context = TidderApplication.getWeakApplicationContext().get();
        mIsPinnable = context.getResources().getBoolean(R.bool.pinned_functionality_available);
    }

    protected ICommentThread mHost;
    protected String mPOTag;

    public CommentThreadProcessor(ICommentThread host) {
        mHost = host;
        mPOTag = mHost.getAddress() + ":" + TAG;
    }

    public void onAttach(Context context) {
        FragmentActivity activity = mHost.getActivity();
        mApiResponseHandler =
                new ApiResponseCallback<>(activity, PostEvent.getFactory());
        mApiStdEventResponseHandler =
                new ApiResponseCallback<>(activity, StandardEvent.getFactory());
        mCpStdEventHandler =
                new QueryCallback<>(activity, StandardEvent.getFactory());
        mStdEventProcessor =
                new StandardEventProcessor(activity, mApiStdEventResponseHandler, mCpStdEventHandler);
    }

    public void onCreate(Bundle savedInstanceState) {
        Bundle b = savedInstanceState;
        if (b == null) {
            b = mHost.getArguments();
        }
        processSavedInstanceState(b);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(mHost.getLayoutId(), container, false);

        ButterKnife.bind(this, rootView);
        mHost.bind(rootView);

        setTitle(mTitle);

        Activity activity = mHost.getActivity();
        mLayoutManager = new AutoMeasureLinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL,
                false);
        rvList.setLayoutManager(mLayoutManager);
        rvList.setNestedScrollingEnabled(false);

        mAdapter = new CommentAdapter(mList,this);

        rvList.setAdapter(mAdapter);

        // Setup onItemTouchHandler to enable drag and drop, swipe left or right
        ItemTouchHelper.Callback callback = new RVHItemTouchHelperCallback(
                mAdapter, false, false,
                false);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rvList);

        // Set the divider in the recyclerview
        rvList.addItemDecoration(new RVHItemDividerDecoration(activity, LinearLayoutManager.VERTICAL));

        // Set On Click Listener
        rvList.addOnItemTouchListener(
                new NoUrlTextViewListItemClickListener(
                        activity,
                        new int[] { R.id.tv_text_comment_item },
                        mAdapter.getAdapterHandler(),
                        TypedGestureDetector.Gestures.SINGLE_TAP,
                        TypedGestureDetector.Gestures.DOUBLE_TAP)
        );

        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

//                postEvent(FollowEvent.newAllSubredditRequest(
//                        null, null, mTracker.getAfter(), mTracker.getCount()));
            }
        };
        // Adds the scroll listener to RecyclerView
        rvList.addOnScrollListener(mScrollListener);

        mSelectCtrl = new AdapterSelectController(rvList);

        return rootView;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mFabPin = (FloatingActionButton) findActivityView(R.id.fab_pin_postDetailA);
        if (!mIsPinnable && (mFabPin != null)) {
            mFabPin.setVisibility(View.GONE);
        }
        mHost.onActivityCreated();
    }

    public void onStart() {
        PostOffice.register(this, mPOTag);

        if (!TextUtils.isEmpty(mPermalink)) {
            boolean emptyList = mList.isEmpty();
            if (emptyList) {
                // COMMENT TREE FLOW 1. post request for comment tree
                postEvent(PostEvent.newGetCommentTreeRequest(mPermalink, 20));

                showInProgress();
                bsvView.setVisibility(View.INVISIBLE);
            } else if (mLink != null) {
                // for rotated case
                bsvView.setViewInfo(mLink);
                bsvView.setVisibility(View.VISIBLE);
                mHost.processGetCommentTreeResult(mLink);
            }
            mHost.onStart(emptyList);
        } else {
            hideInProgressMessage();
        }
    }

    public void onStop() {
        PostOffice.unregister(this, mPOTag);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LINK_NAME, mName);
        outState.putString(LINK_TITLE, mTitle);
        outState.putString(PERMALINK, mPermalink);
        outState.putBoolean(PINNED, mPinned);
        outState.putBoolean(THREAD, mThread);
        outState.putBoolean(TWO_PANE, mTwoPane);
        if ((mList != null) && (mList.size() > 0)) {
            /* a large list or large comments can cause a TransactionTooLargeException in the
                Binder transaction buffer (fixed size, currently 1Mb), store a proxy list */
            ArrayList<CommentProxy> proxies = CommentProxy.addToCache(mList);
            outState.putParcelable(LIST, Parcels.wrap(proxies));
        }
        if (mLink != null) {
            outState.putParcelable(LINK, Parcels.wrap(mLink));
        }
        if (mSubreddit != null) {
            outState.putParcelable(SUBREDDIT, Parcels.wrap(mSubreddit));
        }
        if (mTracker != null) {
            outState.putParcelable(TRACKER, Parcels.wrap(mTracker));
        }
    }

    protected void processSavedInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(LINK_NAME)) {
                mName = savedInstanceState.getString(LINK_NAME);
            }
            if (savedInstanceState.containsKey(LINK_TITLE)) {
                mTitle = savedInstanceState.getString(LINK_TITLE);
            }
            if (savedInstanceState.containsKey(PERMALINK)) {
                mPermalink = savedInstanceState.getString(PERMALINK);
            }
            if (savedInstanceState.containsKey(PINNED)) {
                mPinned = savedInstanceState.getBoolean(PINNED);
            }
            if (savedInstanceState.containsKey(THREAD)) {
                mThread = savedInstanceState.getBoolean(THREAD);
            }
            if (savedInstanceState.containsKey(TWO_PANE)) {
                mTwoPane = savedInstanceState.getBoolean(TWO_PANE);
            }
            if (savedInstanceState.containsKey(LIST)) {
                // convert proxy list to object list
                ArrayList<CommentProxy> proxies = Parcels.unwrap(savedInstanceState.getParcelable(LIST));
                mList = CommentProxy.getFromCache(proxies);
            }
            if (savedInstanceState.containsKey(LINK)) {
                mLink = Parcels.unwrap(savedInstanceState.getParcelable(LINK));
            }
            if (savedInstanceState.containsKey(SUBREDDIT)) {
                mSubreddit = Parcels.unwrap(savedInstanceState.getParcelable(SUBREDDIT));
            }
            if (savedInstanceState.containsKey(TRACKER)) {
                mTracker = Parcels.unwrap(savedInstanceState.getParcelable(TRACKER));
            }
        }
        if (mList == null) {
            mList = new ArrayList<>();
        }
        if (mTracker == null) {
            mTracker = new ListingTracker<>();
        }

        mHost.processSavedInstanceState(savedInstanceState);
    }

    @UiThread
    @Subscribe
    public void processMessageEvent(AbstractEvent event) {
        if (PostOffice.deliverEvent(event, getAddress())) {
            if (event instanceof PostEvent) {
                onPostEvent((PostEvent)event);
            } else if (event instanceof StandardEvent) {
                onStandardEvent((StandardEvent)event);
            }
        }
    }

    protected Bundle getDetailArgs(PostEvent event) {
        String link = event.getPermalink();
        if (TextUtils.isEmpty(link)) {
            // no link so use currently selected
            Comment comment = mAdapter.getSelectedItem();
            if (comment != null) {
                link = comment.getPermalink();
            }
        }
        return getArgs(link);
    }

    protected Bundle getParentArgs() {
        return getArgs(mPermalink);
    }

    private Bundle getArgs(String link) {
        Bundle arguments = new Bundle();
        arguments.putString(LINK_NAME, mName);
        arguments.putString(LINK_TITLE, mTitle);
        arguments.putString(PERMALINK, link);
        arguments.putBoolean(TWO_PANE, mTwoPane);
        return arguments;
    }


    public boolean onPostEvent(PostEvent event) {
        boolean handled = true;

        if (event.isGetCommentTreeRequest()) {
            // COMMENT TREE FLOW 2. send request for comment tree
            processGetCommentTreeRequest(event);

        } else if (event.isGetCommentMoreRequest()) {
            // COMMENT TREE FLOW ?. send request for additional comments
            processGetCommentMoreRequest(event);

        } else if (event.isGetCommentTreeResult()) {
            hideInProgressMessage();
            // COMMENT TREE FLOW 4. process comment tree result
            processGetCommentTreeResult(event);

        } else if (event.isGetCommentMoreResult()) {
            // COMMENT TREE FLOW ?. process comment more result
            processGetCommentMoreResult(event);
        } else {
            handled = mHost.onPostEvent(event);
        }

        PostOffice.logHandled(event, getAddress(), handled);

        return handled;
    }

    protected void processGetCommentTreeRequest(PostEvent event) {
        // additional info bundle for request
        Bundle additionalInfo = PostEvent.getFactory().additionalInfoTag(event);
        mApiResponseHandler.requestGetService(
                CommentTreeRequest.builder()
                        .permalink(event.getPermalink())
                        .limit(event.getLimit())
                        .build()    // build request
                        .setAdditionalInfo(additionalInfo)  // add additional info
        );
    }

    protected void processGetCommentMoreRequest(PostEvent event) {
        // additional info bundle for request
        Bundle additionalInfo = PostEvent.getFactory().additionalInfoAll(event);
        mApiResponseHandler.requestGetService(
                CommentMoreRequest.builder()
                        .id(event.getId())
                        .linkId(event.getLinkId())
                        .children(event.getChildren())
                        .build()    // build request
                        .setAdditionalInfo(additionalInfo)  // add additional info
        );
        Timber.i("Requesting children %s", TextUtils.join(",", event.getChildren()));
    }

    protected void processGetCommentTreeResult(PostEvent event) {
        CommentTreeResponse response = event.getCommentTreeResponse();
        if (response != null) {
            mLink = response.getLink();
            if (mLink != null) {
                setTitle(mLink.getTitle());

                mHost.processGetCommentTreeResult(mLink);

                bsvView.setViewInfo(mLink);
                bsvView.setVisibility(View.VISIBLE);
            }

            int autoExpand = getAutoExpandLevelPreference(mHost.getActivity());

            ArrayList<Comment> list = response.getList();
            ArrayList<Comment> toAdd = new ArrayList<>();
            for (Comment comment : list) {
                addToCache(comment);

                toAdd.add(comment);
                if (autoExpand > AUTOEXPAND_OFF) {
                    addChildrenToList(toAdd, comment, autoExpand);
                }
            }
            for (Comment comment : toAdd) {
                comment.setDisplayed();
            }

            if (mList.addAll(toAdd)) {

                mTracker.updateForward(response);

                mAdapter.notifyDataSetChanged();
            }
        }
    }

    protected int addChildrenToList(ArrayList<Comment> list, Comment comment, int toDepth) {
        int added = 0;
        Comment[] children = comment.getReplies();
        if (children != null) {
            for (Comment child : children) {
                if (child.getDepth() <= toDepth) {
                    addToCache(child);

                    if (list.add(child)) {
                        ++added;
                    }
                    addChildrenToList(list, child, toDepth);
                }
            }
            if (added > 0) {
                comment.setRepliesExpanded();
            }
        }
        return added;
    }

    protected void processGetCommentMoreResult(PostEvent event) {
        CommentMoreResponse response = event.getCommentMoreResponse();
        if (response != null) {
            int startPos = event.getPosition(); // position of more comment in displayed list

            // remove the more comment straight away, whatever was returned replaces it even
            // if some children may not have been return (could have been deleted etc.)
            Comment reply = mAdapter.getItem(startPos);
            Comment.disconnectFromParent(reply);
            mAdapter.remove(startPos);
            mAdapter.notifyItemRemoved(startPos);

            insertComments(response.getList(), startPos, reply.getDepth());
        }
    }

    public boolean onStandardEvent(StandardEvent event) {
        boolean handled = mStdEventProcessor.onStandardEvent(event);
        if (!handled) {
            handled = true;
            if (event.isSubredditInfoResult()) {
                // NEW POST FLOW 10. handle subreddit info result
                SubredditAboutResponse response = event.getSubredditAboutResponse();
                if (response != null) {
                    mSubreddit = response.getSubreddit();
                    if ((mSubreddit != null) && (mLink != null)) {
                        mLink.setThumbnail(mSubreddit.getIcon());
                    }
                }
            } else if (event.isThingAboutResult()) {
                ThingAboutResponse response = event.getThingAboutResponse();
                if (response != null) {
                    FullnameITester<Comment> tester = new FullnameITester<>(null);
                    for (RedditObject obj : response.getList()) {
                        if (obj instanceof Comment) {
                            Comment comment = (Comment) obj;
                            tester.setFullname(comment.getName());

                            Pair<Comment, Integer> result = mAdapter.findItemAndIndex(tester);
                            if (result.first != null) {
                                result.first.copy(comment);
                                result.first.clearRequestInProgress();

                                mAdapter.notifyItemChanged(result.second);
                            }

                        }
                    }
                }
            } else {
                handled = mHost.onStandardEvent(event);
            }

            PostOffice.logHandled(event, getAddress(), handled);
        }
        return handled;
    }

    private void setTitle(String title) {
        tvTitle.setText(title);

        CollapsingToolbarLayout appBarLayout =
                (CollapsingToolbarLayout) findActivityView(R.id.toolbar_layout_postDetailA);
        if (appBarLayout != null) {
            appBarLayout.setTitle(title);
        }
    }

    /**
     * Add a top to bottom constraint
     * @param startId   Resource id of view whose top is to be constrained
     * @param endId     Resource id of view to whose bottom the view to be constrained
     */
    public void addTopToBottomConstraint(int startId, int endId) {
        ConstraintSet constraint = new ConstraintSet();
        constraint.clone(clContent);
        constraint.connect(startId, ConstraintSet.TOP, endId, ConstraintSet.BOTTOM);
        constraint.applyTo(clContent);
    }

    protected void showInProgress() {
        tvMessage.setVisibility(View.INVISIBLE);
        pbProgress.setVisibility(View.VISIBLE);
    }

    protected void showMessage(String message) {
        pbProgress.setVisibility(View.INVISIBLE);
        tvMessage.setText(message);
        tvMessage.setVisibility(View.VISIBLE);
    }

    protected void showMessage(@StringRes int message) {
        showMessage(mHost.getActivity().getString(message));
    }

    protected void hideInProgressMessage() {
        tvMessage.setVisibility(View.INVISIBLE);
        pbProgress.setVisibility(View.INVISIBLE);
    }

    @UiThread
    @Subscribe
    public void processClientEvent(RedditClientEvent event) {
        if (event.isCommsErrorEvent()) {
            String message = event.getMessage();
            @StringRes int messageRes = event.getMessageRes();
            if (!TextUtils.isEmpty(message)) {
                showMessage(message);
            } else {
                if (messageRes == 0) {
                    messageRes = R.string.comms_error;
                }
                showMessage(messageRes);
            }
        }
    }

    @Override
    public void onItemClick(View view) {
        Comment comment = getClickedObject(view);
        int position = mAdapter.setSelectedPos(mLayoutManager, view);

        if (comment instanceof CommentMore) {
            // more comments placeholder
            CommentMore more = (CommentMore) comment;
            String linkId = "";
            if (mLink != null) {
                linkId = mLink.getName();
            }
            postEvent(
                    PostEvent.newGetCommentMoreRequest(
                            more.getId(), linkId, more.getChildren(), position));

            CommentMoreViewHolder vh = (CommentMoreViewHolder) rvList.getChildViewHolder(view);
            vh.showProgress();

        } else {
            int itemCount = comment.getNumComments();
            if (itemCount > 0) {
                Comment[] replies = comment.getReplies();
                int startPos = position + 1;

                if (!comment.isRepliesExpanded()) {
                    // add replies to list
                    insertComments(replies, startPos, comment.getDepth() + 1);

                    comment.setRepliesExpanded();
                    mAdapter.notifyItemChanged(position);   // change comments icon
                } else {
                    // remove replies from list
                    for (Comment reply : replies) {
                        if (reply.isDisplayed()) {
                            reply.setMarked(true);
                            markRepliesForRemoval(reply);
                        }
                    }

                    ArrayDeque<Pair<Integer, Integer>> removeList = new ArrayDeque<>();

                    startPos = mAdapter.findItemIndex(mMarkedTester, startPos);
                    while (startPos >= 0) {
                        for (int endPos = startPos; endPos < mList.size(); ++endPos) {
                            Comment reply = mList.get(endPos);
                            if (!reply.isMarked()) {
                                removeList.push(new Pair<>(startPos, endPos));
                                startPos = mAdapter.findItemIndex(mMarkedTester, endPos);
                                break;
                            }
                        }
                    }

                    int removedCount = 0;
                    int numRanges = removeList.size();
                    startPos = 0;
                    while (!removeList.isEmpty()) {
                        Pair<Integer, Integer> range = removeList.pop();
                        startPos = range.first;
                        mList.subList(range.first, range.second).clear();
                        removedCount += (range.second - range.first);

                        Timber.i("Removed comments %d-%d", range.first, range.second-1);
                    }

                    Timber.i("Removed %d comments", removedCount);

                    if (numRanges == 1) {
                        mAdapter.notifyItemRangeRemoved(startPos, removedCount);
                    } else if (numRanges > 1) {
                        mAdapter.notifyDataSetChanged();
                    }
                    comment.clearRepliesExpanded();
                    mAdapter.notifyItemChanged(position);   // change comments icon
                }
            }
        }

        mHost.onItemClick(view, comment);
    }

    @Override
    public boolean onItemLongClick(View view) {
        mSelectCtrl.onItemLongClick(view);
        return mHost.onItemLongClick(view, getClickedObject(view));
    }

    @Override
    public void onItemDoubleClick(View view) {
        mSelectCtrl.onItemDoubleClick(view);
        mHost.onItemDoubleClick(view, getClickedObject(view));
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        return mSelectCtrl.onKey(view, keyCode, keyEvent);
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        mSelectCtrl.onItemDismiss(position, direction);
        mHost.onItemDismiss(position, direction);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        mSelectCtrl.onItemMove(fromPosition, toPosition);
        return mHost.onItemMove(fromPosition, toPosition);
    }

    protected Comment getClickedObject(View view) {
        return (Comment) view.getTag(R.id.base_obj_tag);
    }


    private int insertComments(ArrayList<Comment> replies, int startPos, int depth) {
        return insertComments(replies.toArray(new Comment[replies.size()]), startPos, depth);
    }

    private int insertComments(Comment[] replies, int startPos, int depth) {
        // add replies to list
        int added = insertComments(mList, replies, startPos, depth);
        if (added > 0) {
            mAdapter.notifyItemRangeInserted(startPos, added);
        }
        return added;
    }

    /**
     * Insert comments into a list
     * @param list      List to insert into
     * @param replies   Comments to insert
     * @param startPos  Start position for inserting
     * @param depth     Comment expand depth
     * @return
     */
    private int insertComments(ArrayList<Comment> list, Comment[] replies, int startPos, int depth) {

        Timber.i("Insert comments: %d replies at index %d with depth %d",
                                            replies.length, startPos, depth);

        // add replies to list
        int added = 0;
        if (replies.length > 0) {

            int autoExpand = getAutoExpandLevelPreference(mHost.getActivity());

            for (Comment reply : replies) {
                addToCache(reply);

                int childDepth = reply.getDepth();
                boolean addChild = (childDepth <= depth);
                String parentId = reply.getParentId();
                Comment parent = getfromCache(parentId);
                if (parent != null) {
                    // add reply to parent
                    if (parent.indexOfReply(reply) == -1) {
                        parent.addReply(reply);
                    }
                    if (addChild) {
                        parent.setRepliesExpanded();
                    }
                } else {
                    if (childDepth > 0) {
                        Timber.e("Orphan child %s, depth %d, can't find %s",
                                reply.getFullname(), childDepth, parentId);
                    }   // else top level comment
                }
                if (addChild) {
                    int insertPos = startPos + added;
                    list.add(insertPos, reply);
                    ++added;
                    reply.setDisplayed();

                    Timber.i("Insert index %d: %s", insertPos, reply);

                    if (autoExpand > AUTOEXPAND_OFF) {
                        ArrayList<Comment> toAdd = new ArrayList<>();
                        int children = addChildrenToList(toAdd, reply, autoExpand);
                        if (children > 0) {
                            insertPos = startPos + added;
                            list.addAll(insertPos, toAdd);
                            added += toAdd.size();
                            for (Comment child : toAdd) {
                                child.setDisplayed();
                            }

                            Timber.i("Insert index %d: %d children %s",
                                                insertPos, toAdd.size(), toAdd);
                        }
                    }
                }
            }
        }
        return added;
    }


    private void addToCache(Comment comment) {
        comment.tagIfNotTagged();

        String key = comment.getCacheKey();
        if (!TextUtils.isEmpty(key)) {
            CommentCache.getInstance().put(key, comment);
        }
    }

    private Comment getfromCache(String key) {
        return CommentCache.getInstance().get(key, mCacheListener);
    }


    private RedditCache.ICacheListener<Comment> mCacheListener = new RedditCache.ICacheListener<Comment>() {
        @Override
        public void onCreate(String key, Comment object) {
            object.setRequestInProgress();
            postEvent(StandardEvent.newThingAboutRequest(object.getName())
                                    .addAddress(mStdEventProcessor.getAddress()));
        }
    };

    /** Tester to find marked comments */
    private ITester<Comment> mMarkedTester = new ITester<Comment>() {
        @Override
        public boolean test(Comment obj) {
            boolean isMarked;
            Comment.Tag status = obj.getTag();
            if (status != null) {
                isMarked = status.isMarked();
            } else {
                throw new IllegalStateException("Untagged comment");
            }
            return isMarked;
        }
    };

    private void markRepliesForRemoval(Comment comment) {
        if (comment.isRepliesExpanded()) {
            Comment[] replies = comment.getReplies();
            for (Comment reply : replies) {
                if (reply.isDisplayed()) {
                    if (reply.isRepliesExpanded()) {
                        markRepliesForRemoval(reply);
                    }
                    reply.setMarked(true);
                }
            }
            comment.clearRepliesExpanded();
        }
    }

    public <T extends AbstractEvent> void postEvent(T event) {
        PostOffice.postEvent(event.addAddress(getAddress()));
    }

    @Override
    public String getAddress() {
        return mHost.getAddress();
    }

    @Nullable private View findActivityView(@IdRes int idRes) {
        View view = null;
        Activity activity = mHost.getActivity();
        if (activity != null) {
            view = activity.findViewById(idRes);
        }
        return view;
    }


    @Nullable public FloatingActionButton getFabPin() {
        FloatingActionButton fab = null;
        if (mIsPinnable) {
            fab = mFabPin;
        }
        return fab;
    }

    public String getName() {
        return mName;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPermalink() {
        return mPermalink;
    }

    public boolean isPinned() {
        return mPinned;
    }

    public boolean isThread() {
        return mThread;
    }

    public boolean isTwoPane() {
        return mTwoPane;
    }

    public Link getLink() {
        return mLink;
    }

    public Subreddit getSubreddit() {
        return mSubreddit;
    }

    protected void setPinned(boolean pinned) {
        mPinned = pinned;
        if ((mFabPin != null) && mIsPinnable) {
            @DrawableRes int pinRes;
            if (mPinned) {
                pinRes = R.drawable.ic_unpin;
            } else {
                pinRes = R.drawable.ic_pin;
            }
            mFabPin.setImageResource(pinRes);
            mFabPin.setVisibility(View.VISIBLE);
        }
    }

    public interface ICommentThread extends PostOffice.IAddressable {

        FragmentActivity getActivity();

        Bundle getArguments();

        void processSavedInstanceState(@Nullable Bundle savedInstanceState);

        @LayoutRes int getLayoutId();

        void bind(View view);

        void onActivityCreated();

        void onStart(boolean emptyList);

        boolean onPostEvent(PostEvent event);

        boolean onStandardEvent(StandardEvent event);

        void processGetCommentTreeResult(Link link);

        void onItemClick(View view, Comment comment);

        boolean onItemLongClick(View view, Comment comment);

        void onItemDoubleClick(View view, Comment comment);

        void onItemDismiss(int position, int direction);

        boolean onItemMove(int fromPosition, int toPosition);
    }

}
