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

package com.ianbuttimer.tidderish.reddit;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Unit tests for Api class
 */
@RunWith(AndroidJUnit4.class)
public class ApiTest {

    @Test
    public void apiTest() {
        testScopeString();
    }

    private void testScopeString() {
        testEquality(Api.Scopes.ACCOUNT, "account");
        testEquality(Api.Scopes.CREDDITS, "creddits");
        testEquality(Api.Scopes.EDIT, "edit");
        testEquality(Api.Scopes.FLAIR, "flair");
        testEquality(Api.Scopes.HISTORY, "history");
        testEquality(Api.Scopes.IDENTITY, "identity");
        testEquality(Api.Scopes.LIVEMANAGE, "livemanage");
        testEquality(Api.Scopes.MODCONFIG, "modconfig");
        testEquality(Api.Scopes.MODCONTRIBUTORS, "modcontributors");
        testEquality(Api.Scopes.MODFLAIR, "modflair");
        testEquality(Api.Scopes.MODLOG, "modlog");
        testEquality(Api.Scopes.MODMAIL, "modmail");
        testEquality(Api.Scopes.MODOTHERS, "modothers");
        testEquality(Api.Scopes.MODPOSTS, "modposts");
        testEquality(Api.Scopes.MODSELF, "modself");
        testEquality(Api.Scopes.MODWIKI, "modwiki");
        testEquality(Api.Scopes.MYSUBREDDITS, "mysubreddits");
        testEquality(Api.Scopes.PRIVATEMESSAGES, "privatemessages");
        testEquality(Api.Scopes.READ, "read");
        testEquality(Api.Scopes.REPORT, "report");
        testEquality(Api.Scopes.SAVE, "save");
        testEquality(Api.Scopes.SUBMIT, "submit");
        testEquality(Api.Scopes.SUBSCRIBE, "subscribe");
        testEquality(Api.Scopes.VOTE, "vote");
        testEquality(Api.Scopes.WIKIEDIT, "wikiedit");
        testEquality(Api.Scopes.WIKIREAD, "wikiread");

        testEquality(new Api.Scopes[] {
                        Api.Scopes.ACCOUNT,
                        Api.Scopes.CREDDITS
            }
            , "account creddits");
    }

    private void testEquality(Api.Scopes scope, String expected) {
        testEquality(new Api.Scopes[] { scope }, expected);
    }

    private void testEquality(Api.Scopes[] scopes, String expected) {
        String[] strings = new String[scopes.length];
        int idx = 0;
        for (Api.Scopes scope : scopes) {
            strings[idx++] = scope.name();
        }
        assertEquals(TextUtils.join(" ", strings) + " != " + expected, Api.getScopeString(scopes), expected);
    }

}