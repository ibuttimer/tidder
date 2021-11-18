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

package com.ianbuttimer.tidderish.ui.util;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public abstract class AbstractSectionPagerAdapter extends FragmentPagerAdapter
                                                    implements ISectionsPagerAdapter {

    private final FragmentManager mFragmentManager;
    private ViewGroup mContainer;

    public AbstractSectionPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragmentManager = fm;
    }

    @Override
    public @NonNull Object instantiateItem(@NonNull ViewGroup container, int position) {
        mContainer = container;
        return super.instantiateItem(container, position);
    }

    @Override
    @Nullable
    public Fragment getFragment(int position) {
        final long itemId = getItemId(position);

        // Do we have this fragment?
        String name = makeFragmentName(mContainer.getId(), itemId);
        return mFragmentManager.findFragmentByTag(name);
    }

    /**
     * Get fragment name tag. <b>Note:</b> copied from super class
     */
    private String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
