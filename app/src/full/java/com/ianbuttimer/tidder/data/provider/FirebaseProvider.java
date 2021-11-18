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

package com.ianbuttimer.tidderish.data.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Pair;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ianbuttimer.tidderish.BuildConfig;
import com.ianbuttimer.tidderish.data.db.AbstractFbRow;
import com.ianbuttimer.tidderish.data.db.ConfigFb;
import com.ianbuttimer.tidderish.data.db.FollowFb;
import com.ianbuttimer.tidderish.data.db.IFbCursorable;
import com.ianbuttimer.tidderish.data.db.PinnedFb;
import com.ianbuttimer.tidderish.net.UriUtils;
import com.ianbuttimer.tidderish.reddit.RedditClient;

import java.util.ArrayList;

import timber.log.Timber;

import static com.ianbuttimer.tidderish.data.provider.BaseProvider.ID_EQ_SELECTION;
import static com.ianbuttimer.tidderish.data.provider.BaseProvider.Path.FOLLOW_FRAGMENT_INDEX;
import static com.ianbuttimer.tidderish.data.provider.BaseProvider.Path.PINNED_FRAGMENT_INDEX;
import static com.ianbuttimer.tidderish.data.provider.BaseProvider.TYPE_DIR;
import static com.ianbuttimer.tidderish.data.provider.BaseProvider.buildUri;

public class FirebaseProvider extends ContentProvider {
    public static final String AUTHORITY = BuildConfig.FB_PROVIDER_AUTHORITY;

    /** Base Uri for content provider */
    public static final Uri BASE_CONTENT_URI;

    static {
        Uri.Builder builder = new Uri.Builder().
                scheme(ContentResolver.SCHEME_CONTENT).
                encodedAuthority(AUTHORITY);
        BASE_CONTENT_URI = builder.build();
    }

    interface Path extends BaseProvider.Path {
        String CONFIG = "config";
    }


    public static class Follow extends BaseProvider.FollowBase {

        public static final Uri CONTENT_URI = buildUri(BASE_CONTENT_URI, Path.FOLLOW);

        public static Uri withId(long id) {
            return buildUri(BASE_CONTENT_URI, Path.FOLLOW, String.valueOf(id));
        }
    }

    public static class Pinned extends BaseProvider.PinnedBase {

        public static final Uri CONTENT_URI = buildUri(BASE_CONTENT_URI, Path.PINNED);

        public static Uri withId(long id) {
            return buildUri(BASE_CONTENT_URI, Path.PINNED, String.valueOf(id));
        }
    }

    public static class Config {

        public static final Uri CONTENT_URI = buildUri(BASE_CONTENT_URI, Path.CONFIG);
    }


    // start copy from com.ianbuttimer.tidderish.data.db.gen.TidderProvider.java
    private static final int FOLLOW_CONTENT_URI = 0;

    private static final int FOLLOW_follow_id = 1;

    private static final int PINNED_CONTENT_URI = 2;

    private static final int PINNED_pinned_id = 3;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, "follow", FOLLOW_CONTENT_URI);
        MATCHER.addURI(AUTHORITY, "follow/#", FOLLOW_follow_id);
        MATCHER.addURI(AUTHORITY, "pinned", PINNED_CONTENT_URI);
        MATCHER.addURI(AUTHORITY, "pinned/#", PINNED_pinned_id);
    }
    // end copy from com.ianbuttimer.tidderish.data.db.gen.TidderProvider.java

    private static final int CONFIG_CONTENT_URI = 4;

    static {
        MATCHER.addURI(AUTHORITY, Path.CONFIG, CONFIG_CONTENT_URI);
    }


    private FirebaseDatabase mDatabase;

    @Override
    public boolean onCreate() {
        mDatabase = FirebaseDatabase.getInstance();
        return true;
    }

    /**
     * Get a database reference
     * @param nodes Array of nodes to required reference
     * @return
     */
    private DatabaseReference getReference(String... nodes) {
        DatabaseReference ref = mDatabase.getReference(RedditClient.getClient().getUserId());
        for (String node : nodes) {
            ref = ref.child(node);
        }
        
        Timber.i("Fb reference %s", ref);

        return ref;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        throw new UnsupportedOperationException("Bulk insert not currently supported on " + getClass().getSimpleName());
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> ops) throws
            OperationApplicationException {
        throw new UnsupportedOperationException("Batch operations not currently supported on " + getClass().getSimpleName());
    }

    // start copy from com.ianbuttimer.tidderish.data.db.gen.TidderProvider.java
    @Override
    public String getType(Uri uri) {
        switch(MATCHER.match(uri)) {
            case FOLLOW_CONTENT_URI: {
                return "vnd.android.cursor.dir/follow";
            }
            case FOLLOW_follow_id: {
                return "vnd.android.cursor.item/follow";
            }
            case PINNED_CONTENT_URI: {
                return "vnd.android.cursor.dir/pinned";
            }
            case PINNED_pinned_id: {
                return "vnd.android.cursor.item/pinned";
            }

// >>> start insert
            case CONFIG_CONTENT_URI: {
                return TYPE_DIR + Path.CONFIG;
            }
// >>> end insert

            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }
    }
    // end copy from com.ianbuttimer.tidderish.data.db.gen.TidderProvider.java

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;
        ArrayList<Pair<String, String[]>> whereList = whereList(uri, selection, selectionArgs);

        switch(MATCHER.match(uri)) {
            case FOLLOW_follow_id:
                // add id condition
                whereList.add(new Pair<>(
                        ID_EQ_SELECTION, new String[] { uri.getPathSegments().get(FOLLOW_FRAGMENT_INDEX) }));
                // fall through
            case FOLLOW_CONTENT_URI:
                whereList.add(new Pair<>(selection, selectionArgs));

                cursor = fbQueryList(Path.FOLLOW, FollowFb.getFactory(), uri,
                                    projection, whereList, sortOrder);
                break;

            case PINNED_pinned_id:
                // add id condition
                whereList.add(new Pair<>(
                        ID_EQ_SELECTION, new String[] { uri.getPathSegments().get(PINNED_FRAGMENT_INDEX) }));
                // fall through
            case PINNED_CONTENT_URI:
                cursor = fbQueryList(Path.PINNED, PinnedFb.getFactory(), uri,
                                    projection, whereList, sortOrder);
                break;

            case CONFIG_CONTENT_URI:
                cursor = fbQueryObject(Path.CONFIG, ConfigFb.getFactory(), uri,
                                    projection, whereList);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return cursor;
    }

    /**
     * Query the database
     * @param node          Database node
     * @param fbCursorable
     * @param uri           Uri to query
     * @param projection    Projection
     * @param whereList     List of where conditions
     * @param sortOrder     Sort order
     * @return
     */
    private Cursor fbQueryList(String node, IFbCursorable fbCursorable, Uri uri,
                               String[] projection, ArrayList<Pair<String, String[]>> whereList,
                               String sortOrder) {
//        FbQuery query = makeFbQuery(node, whereList, sortOrder);
//
//        QueryValueEventListener listener = new QueryValueEventListener(fbCursorable);
//
//        listener.addToQueryAsSingleValueEvent(query);
//
//        Cursor cursor = listener.getResult();
//
//        listener.removeFromQuery();
//
//        setNotificationUri(cursor, uri);
//
//        return cursor;

        return fbQuery(node, new QueryValueEventListener(fbCursorable), uri,
                projection, whereList, sortOrder);
    }

    /**
     * Query the database
     * @param node          Database node
     * @param fbCursorable
     * @param uri           Uri to query
     * @param projection    Projection
     * @param whereList     List of where conditions
     * @return
     */
    private Cursor fbQueryObject(String node, IFbCursorable fbCursorable, Uri uri,
                               String[] projection, ArrayList<Pair<String, String[]>> whereList) {
        return fbQuery(node, new QueryObjectValueEventListener(fbCursorable), uri,
                            projection, whereList, null);
    }

    /**
     * Query the database
     * @param node          Database node
     * @param listener      Data event listener to use
     * @param uri           Uri to query
     * @param projection    Projection
     * @param whereList     List of where conditions
     * @param sortOrder     Sort order
     * @return
     */
    private Cursor fbQuery(String node, AbstractValueEventListener<?> listener, Uri uri,
                               String[] projection, ArrayList<Pair<String, String[]>> whereList,
                               String sortOrder) {
        FbQuery query = makeFbQuery(node, whereList, sortOrder);

        listener.addToQueryAsSingleValueEvent(query);

        Cursor cursor = null;
        Object result = listener.getResult();
        if (result instanceof Cursor) {
            cursor = (Cursor) result;
        }

        listener.removeFromQuery();

        setNotificationUri(cursor, uri);

        return cursor;
    }

    /**
     * Generate a Firebase Query
     * @param node      Database node
     * @param whereList List of where conditions
     * @param sortOrder Sort order
     * @return  Firebase query
     */
    private FbQuery makeFbQuery(String node, ArrayList<Pair<String, String[]>> whereList,
                           String sortOrder) {
        DatabaseReference ref = getReference(node);
        FbQuery fbQuery = new FbQuery(ref);
        for (Pair<String, String[]> where : whereList) {
            fbQuery.where(where.first, where.second);
        }
        fbQuery.makeQuery();
        return fbQuery;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri result;
        switch(MATCHER.match(uri)) {
            case FOLLOW_CONTENT_URI: {
                final String id = insertFollowValue(values);
                notifyUris(BaseProvider.onInsert(uri, values));
                result = UriUtils.getWithIdUri(uri, id);
                break;
            }
            case FOLLOW_follow_id: {
                final String id = insertFollowValue(values);
                notifyUri(uri);
                result = UriUtils.getWithIdUri(uri, id);
                break;
            }
            case PINNED_CONTENT_URI: {
                final String id = insertPinnedValue(values);
                notifyUris(BaseProvider.onInsert(uri, values));
                result = UriUtils.getWithIdUri(uri, id);
                break;
            }
            case PINNED_pinned_id: {
                final String id = insertPinnedValue(values);
                notifyUri(uri);
                result = UriUtils.getWithIdUri(uri, id);
                break;
            }
            case CONFIG_CONTENT_URI:  {
                final String id = insertConfigValue(values);
                notifyUri(uri);
                result = UriUtils.getWithIdUri(uri, id);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        int count;
        ArrayList<Pair<String, String[]>> whereList = whereList(uri, where, whereArgs);

        switch(MATCHER.match(uri)) {
            case FOLLOW_follow_id:
            case FOLLOW_CONTENT_URI:
                count = fbUpdate(Path.FOLLOW, whereList, values, FollowFb.class);
                break;

            case PINNED_pinned_id:
            case PINNED_CONTENT_URI:
                count = fbUpdate(Path.PINNED, whereList, values, PinnedFb.class);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (count > 0) {
            notifyUri(uri);
        }
        return count;
    }

    /**
     * Update database content
     * @param node
     * @param whereList
     * @param value
     * @param objClass
     * @return
     */
    private int fbUpdate(String node, ArrayList<Pair<String, String[]>> whereList,
                         ContentValues value, Class<? extends AbstractFbRow> objClass) {

        FbQuery query = makeFbQuery(node, whereList, null);

        UpdateValueEventListener listener = new UpdateValueEventListener(objClass, value);

        listener.addToQueryAsSingleValueEvent(query);

        int count = listener.getResult();

        listener.removeFromQuery();

        return count;
    }


    /**
     * Generate a slection list
     * @param uri
     * @param where
     * @param whereArgs
     * @return
     */
    public ArrayList<Pair<String, String[]>> whereList(Uri uri, String where, String[] whereArgs) {
        ArrayList<Pair<String, String[]>>  whereList = new ArrayList<>();
        int match = MATCHER.match(uri);

        switch(match) {
            case FOLLOW_follow_id:
                // add id condition
                whereList.add(new Pair<>(
                        ID_EQ_SELECTION, new String[] { uri.getPathSegments().get(FOLLOW_FRAGMENT_INDEX) }));
                break;
            case PINNED_pinned_id:
                // add id condition
                whereList.add(new Pair<>(
                        ID_EQ_SELECTION, new String[] { uri.getPathSegments().get(PINNED_FRAGMENT_INDEX) }));
                // fall through
        }
        switch(match) {
            case FOLLOW_follow_id:
            case FOLLOW_CONTENT_URI:
            case PINNED_pinned_id:
            case PINNED_CONTENT_URI:
            case CONFIG_CONTENT_URI:
                whereList.add(new Pair<>(where, whereArgs));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return whereList;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int count;
        ArrayList<Pair<String, String[]>> whereList = whereList(uri, where, whereArgs);

        switch(MATCHER.match(uri)) {
            case FOLLOW_follow_id:
            case FOLLOW_CONTENT_URI:
                count = fbDelete(Path.FOLLOW, whereList);
                break;

            case PINNED_pinned_id:
            case PINNED_CONTENT_URI:
                count = fbDelete(Path.PINNED, whereList);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (count > 0) {
            notifyUri(uri);
        }
        return count;
    }

    /**
     * Remove data from the database
     * @param node
     * @param whereList
     * @return
     */
    private int fbDelete(String node, ArrayList<Pair<String, String[]>> whereList) {

        FbQuery query = makeFbQuery(node, whereList, null);

        DeleteValueEventListener listener = new DeleteValueEventListener();

        listener.addToQueryAsSingleValueEvent(query);

        int count = listener.getResult();

        listener.removeFromQuery();

        return count;
    }

    /**
     * Insert a new follow row into the database
     * @param value Data in insert
     * @return  Id for new row
     */
    private String insertFollowValue(ContentValues value) {
        FollowFb obj = FollowFb.generate(value);

        // get list reference
        DatabaseReference ref = getReference(Path.FOLLOW).push();
        ref.setValue(obj);
        return ref.getKey();
    }

    /**
     * Insert multiple new follow rows into the database
     * @param values Data in insert
     * @return  Ids for new row
     */
    private String[] insertFollowValues(ContentValues[] values) {
        String[] ids = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            ids[i] = insertFollowValue(values[i]);
        }
        return ids;
    }

    /**
     * Insert a new pinned row into the database
     * @param value Data in insert
     * @return  Id for new row
     */
    private String insertPinnedValue(ContentValues value) {
        PinnedFb obj = PinnedFb.generate(value);

        // get list reference
        DatabaseReference ref = getReference(Path.PINNED).push();
        ref.setValue(obj);
        return ref.getKey();
    }

    /**
     * Insert multiple new pinned rows into the database
     * @param values Data in insert
     * @return  Ids for new row
     */
    private String[] insertPinnedValues(ContentValues[] values) {
        String[] ids = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            ids[i] = insertPinnedValue(values[i]);
        }
        return ids;
    }

    /**
     * Insert new config data into the database
     * @param value Data in insert
     * @return  Id for object
     */
    private String insertConfigValue(ContentValues value) {
        ConfigFb obj = ConfigFb.generate(value);

        // get object reference
        DatabaseReference ref = getReference(Path.CONFIG);
        ref.setValue(obj);
        return ref.getKey();
    }

    /**
     * Notify registered observers that multiple rows have changed
     * @param notifyUris    Uris of the content that has changed
     */
    private void notifyUris(Uri[] notifyUris) {
        Context context = getContext();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            for (Uri notifyUri : notifyUris) {
                contentResolver.notifyChange(notifyUri, null);
            }
        }
    }

    /**
     * Notify registered observers that a row has changed
     * @param notifyUri    Uri of the content that has changed
     */
    private void notifyUri(Uri notifyUri) {
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(notifyUri, null);
        }
    }

    private void setNotificationUri(Cursor cursor, Uri uri) {
        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
    }
}

