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
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ianbuttimer.tidder.R;
import com.ianbuttimer.tidder.data.IAdapterHandler;
import com.ianbuttimer.tidder.data.ITester;
import com.ianbuttimer.tidder.reddit.BaseObject;
import com.ianbuttimer.tidder.utils.ArrayTester;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import github.nisrulz.recyclerviewhelper.RVHAdapter;


/**
 * Adapter class for a RecyclerView of movies
 */
@SuppressWarnings("unused")
public abstract class AbstractRecycleViewAdapter<T extends BaseObject, K extends AbstractViewHolder>
                                        extends RecyclerView.Adapter<K>
                                        implements RVHAdapter {

    protected static final int DEFAULT_KEY = 0;

    protected List<T> mList;         // list of objects represented by this adapter
    protected IAdapterHandler mAdapterHandler;

    protected ArrayTester<T> mArrayTester;

    protected SparseIntArray mLayoutIds;

    /**
     * Constructor
     * @param objects           The objects to represent in the list.
     * @param adapterHandler    Handler for the views in this adapter
     */
    public AbstractRecycleViewAdapter(@NonNull List<T> objects, @Nullable IAdapterHandler adapterHandler) {
        this(objects, adapterHandler, 0);
    }

    /**
     * Constructor
     * @param objects           The objects to represent in the list.
     * @param adapterHandler    Handler for the views in this adapter
     * @param layoutId          Id of layout to inflate
     */
    public AbstractRecycleViewAdapter(@NonNull List<T> objects, @Nullable IAdapterHandler adapterHandler, @LayoutRes int layoutId) {
        mList = objects;
        mAdapterHandler = adapterHandler;

        mLayoutIds = new SparseIntArray();
        if (layoutId != 0) {
            mLayoutIds.put(DEFAULT_KEY, layoutId);
        }

        mArrayTester = new ArrayTester<>(mList);
    }

    /**
     * Constructor
     * @param objects       The objects to represent in the list.
     * @param layoutId      Id of layout to inflate
     */
    public AbstractRecycleViewAdapter(@NonNull List<T> objects, @LayoutRes int layoutId) {
        this(objects, null, layoutId);
    }

    /**
     * Add a layout id mapping
     * @param viewType  View type of layout
     * @param layoutId  Layout id
     */
    public void addLayoutId(int viewType, @LayoutRes int layoutId) {
        mLayoutIds.put(viewType, layoutId);
    }

    @Override
    public int getItemViewType(int position) {
        // default is to return a single view type
        int key = 0;
        if (mLayoutIds.size() == 1) {
            key = mLayoutIds.keyAt(0);
        }
        return key;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new AbstractViewHolder that holds the View for each list item
     */
    @Override
    public K onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        @LayoutRes int layoutId = mLayoutIds.get(viewType);
        if (layoutId == 0) {
            throw new IllegalStateException("No layout mapping for view type " + viewType);
        }

        // inflate but don't attach
        View view = inflater.inflate(layoutId, viewGroup, false);

        return getNewViewHolder(view, mAdapterHandler, viewType);
    }

    /**
     * Create a new view holder
     * @param view              View to insert into view holder
     * @param adapterHandler    Handler
     * @param viewType          If your RecyclerView has more than one type of item (which ours doesn't) you
     * @return  View holder
     */
    public abstract K getNewViewHolder(View view, IAdapterHandler adapterHandler, int viewType);

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param viewHolder The ViewHolder which should be updated to represent the
     *                   contents of the item at the given position in the data set.
     * @param position   The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull K viewHolder, int position) {
        T info = getItem(position);
        if (info != null) {
            // set the info object as the view tag for easy retrieval later
            viewHolder.itemView.setTag(R.id.base_obj_tag, info);
        }
        viewHolder.setViewInfo(info, position);   // set the view's elements to the object's info
    }

    @Override
    public void onViewRecycled(@NonNull K holder) {
        holder.onViewRecycled();
    }

    /**
     * Set the item click handler
     * @param adapterHandler  handler for the views in this adapter
     */
    public void setAdapterHandler(IAdapterHandler adapterHandler) {
        this.mAdapterHandler = adapterHandler;
    }

    /**
     * Get the item click handler
     * @return  handler for the views in this adapter
     */
    @Nullable public IAdapterHandler getAdapterHandler() {
        return mAdapterHandler;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     * @param position   Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position. This value may be null.
     */
    public T getItem(int position) {
        T item = null;
        if (mList != null) {
            item = mList.get(position);
        }
        return item;
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester        Tester to use to find required data
     * @param fromIndex	    Index of the first element, <b>inclusive</b>
     * @param toIndex	    Index of the last element, <b>exclusive</b>
     * @return A Pair with the data at the specified position and its index. If a data item isn't found the Pair will contain null & -1;
     * @throws IllegalArgumentException    if fromIndex >= toIndex
     * @throws ArrayIndexOutOfBoundsException    if fromIndex < 0 or toIndex > a.length
     */
    public Pair<T, Integer> findItemAndIndex(ITester<T> tester, int fromIndex, int toIndex) {
        return mArrayTester.findItemAndIndex(tester, fromIndex, toIndex);
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester        Tester to use to find required data
     * @return A Pair with the data at the specified position and its index. If a data item isn't found the Pair will contain null & -1;
     */
    public Pair<T, Integer> findItemAndIndex(ITester<T> tester) {
        return mArrayTester.findItemAndIndex(tester);
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @param toIndex	Index of the last element, exclusive
     * @return The data matching the test criteria, or <code>null</code> if not found
     * @throws IllegalArgumentException    if fromIndex > toIndex
     * @throws ArrayIndexOutOfBoundsException    if fromIndex < 0 or toIndex > a.length
     */
    public T findItem(ITester<T> tester, int fromIndex, int toIndex) {
        return mArrayTester.findItem(tester, fromIndex, toIndex);
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @return The data matching the test criteria, or <code>null</code> if not found
     * @throws ArrayIndexOutOfBoundsException    if fromIndex < 0 or toIndex > a.length
     */
    public T findItem(ITester<T> tester, int fromIndex) {
        return mArrayTester.findItem(tester, fromIndex);
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @return The data matching the test criteria, or <code>null</code> if not found
     */
    public T findItem(ITester<T> tester) {
        return findItem(tester, 0);
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @param toIndex	Index of the last element, exclusive
     * @return The index of the data, or -1 if nothing is found
     * @throws IllegalArgumentException    if fromIndex > toIndex
     * @throws ArrayIndexOutOfBoundsException    if fromIndex < 0 or toIndex > a.length
     */
    public int findItemIndex(ITester<T> tester, int fromIndex, int toIndex) {
        return mArrayTester.findItemIndex(tester, fromIndex, toIndex);
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, inclusive
     * @return The index of the data, or -1 if nothing is found
     * @throws ArrayIndexOutOfBoundsException    if fromIndex < 0 or toIndex > a.length
     */
    public int findItemIndex(ITester<T> tester, int fromIndex) {
        return mArrayTester.findItemIndex(tester, fromIndex);
        
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @return The index of the data, or -1 if nothing is found
     */
    public int findItemIndex(ITester<T> tester) {
        return findItemIndex(tester, 0);
    }

    /**
     * Find the data item in the data set matching the test criteria, by searching in reverse order
     * @param tester        Tester to use to find required data
     * @param fromIndex	    Index of the first element, <b>exclusive</b>
     * @param toIndex	    Index of the last element, <b>inclusive</b>
     * @return A Pair with the data at the specified position and its index. If a data item isn't found the Pair will contain null & -1;
     * @throws IllegalArgumentException    if toIndex >= fromIndex
     * @throws ArrayIndexOutOfBoundsException    if toIndex < 0 or fromIndex > a.length
     */
    public Pair<T, Integer> findItemAndIndexReverse(ITester<T> tester, int fromIndex, int toIndex) {
        return mArrayTester.findItemAndIndexReverse(tester, fromIndex, toIndex);
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, <b>exclusive</b>
     * @param toIndex	Index of the last element, <b>inclusive</b>
     * @return The data matching the test criteria, or <code>null</code> if not found
     * @throws IllegalArgumentException    if toIndex >= fromIndex
     * @throws ArrayIndexOutOfBoundsException    if toIndex < 0 or fromIndex > a.length
     */
    public T findItemReverse(ITester<T> tester, int fromIndex, int toIndex) {
        return mArrayTester.findItemReverse(tester, fromIndex, toIndex);
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, <b>exclusive</b>
     * @return The data matching the test criteria, or <code>null</code> if not found
     * @throws ArrayIndexOutOfBoundsException    if fromIndex > a.length
     */
    public T findItemReverse(ITester<T> tester, int fromIndex) {
        return mArrayTester.findItemReverse(tester, fromIndex);
    }

    /**
     * Find the data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @return The data matching the test criteria, or <code>null</code> if not found
     */
    public T findItemReverse(ITester<T> tester) {
        return mArrayTester.findItemReverse(tester);
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, <b>exclusive</b>
     * @param toIndex	Index of the last element, <b>inclusive</b>
     * @return The index of the data, or -1 if nothing is found
     * @throws IllegalArgumentException    if toIndex >= fromIndex
     * @throws ArrayIndexOutOfBoundsException    if toIndex < 0 or fromIndex > a.length
     */
    public int findItemIndexReverse(ITester<T> tester, int fromIndex, int toIndex) {
        return mArrayTester.findItemIndexReverse(tester, fromIndex, toIndex);
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @param fromIndex	Index of the first element, <b>exclusive</b>
     * @return The index of the data, or -1 if nothing is found
     * @throws ArrayIndexOutOfBoundsException    if fromIndex > a.length
     */
    public int findItemIndexReverse(ITester<T> tester, int fromIndex) {
        return mArrayTester.findItemIndexReverse(tester, fromIndex);
    }

    /**
     * Find the index of a data item in the data set matching the test criteria
     * @param tester    Tester to use to find required data
     * @return The index of the data, or -1 if nothing is found
     */
    public int findItemIndexReverse(ITester<T> tester) {
        return mArrayTester.findItemIndexReverse(tester);
    }

    /**
     * This method simply returns the number of items to display
     * @return The number of items available in the dataset.
     */
    @Override
    public int getItemCount() {
        int length = 0;
        if (mList != null) {
            length = mList.size();
        }
        return length;
    }

    /**
     * Adds the specified object at the end of the list.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     * @param item  The object to add at the end of the list.
     * @return <code>true</code> if the list changed as a result of the call
     */
    public boolean add(T item) {
        return mList.add(item);
    }

    /**
     * Adds the specified items at the end of the list.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     * @param items The items to add at the end of the list.
     * @return <code>true</code> if the list changed as a result of the call
     */
    public boolean addAll(T... items) {
        return addAll(Arrays.asList(items));
    }

    /**
     * Adds the specified items at the end of the list.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     * @param collection The Collection to add at the end of the list.
     * @return <code>true</code> if the list changed as a result of the call
     */
    public boolean addAll(Collection<? extends T> collection) {
        return mList.addAll(collection);
    }

    /**
     * Removes the element at the specified position in this list.
     * @param position   The index of the element to be removed.
     * @return The data at the specified position. This value may be null.
     * @throws UnsupportedOperationException    if the remove operation is not supported by this list
     * @throws IndexOutOfBoundsException    if the index is out of range (index < 0 || index >= size())
     */
    @Nullable public T remove(int position) {
        T item = null;
        if (mList != null) {
            item = mList.remove(position);
        }
        return item;
    }

    /**
     * Get a list iterator
     * @return  Iterator
     */
    public Iterator<T> iterator () {
        Iterator<T> iterator = null;
        if (mList != null) {
            iterator = mList.iterator();
        }
        return iterator;
    }

    /**
     * Remove all elements from the list.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     * @return <code>true</code> if the list changed as a result of the call
     */
    public boolean clear() {
        int count = getItemCount();
        if (mList != null) {
            mList.clear();
        }
        return (count != getItemCount());
    }

    /**
     * Sets the list to the specified items.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     * @param items The items to set the list.
     * @return <code>true</code> if the list changed as a result of the call
     */
    public boolean setList(T... items) {
        return setList(Arrays.asList(items));
    }

    /**
     * Sets the list to the specified items.
     * Note: Remember to call notifyDataSetChanged() when finished adding items to the list
     * @param collection The Collection to set the list.
     * @return <code>true</code> if the list changed as a result of the call
     */
    public boolean setList(Collection<? extends T> collection) {
        boolean cleared = clear();
        boolean changed = addAll(collection);
        return (cleared || changed);
    }

    /**
     * Sort the data set
     * @param comparator    Comparator to sort data set
     */
    public void sortList(Comparator<Object> comparator) {
        // based on https://developer.android.com/reference/java/util/List.html#sort(java.util.Comparator<? super E>)
        Object[] elements = mList.toArray();
        Arrays.sort(elements, comparator);
        ListIterator<T> iterator = mList.listIterator();
        for (Object element : elements) {
            iterator.next();
            iterator.set((T) element);
        }
    }

    // vvvvvvv RVHAdapter implementation vvvvvvv

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        boolean moved = false;
        if (mAdapterHandler != null) {
            moved = mAdapterHandler.onItemMove(fromPosition, toPosition);
        } else {
            Collections.swap(mList, fromPosition, toPosition);

            notifyItemMoved(fromPosition, toPosition);
            moved = true;
        }
        return moved;
    }

    @Override
    public void onItemDismiss(int position, int direction) {
        if (mAdapterHandler != null) {
            mAdapterHandler.onItemDismiss(position, direction);
        } else {
            remove(position);
            notifyItemRemoved(position);
        }
    }

}
