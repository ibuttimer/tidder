package com.ianbuttimer.tidder.ui.util;

import android.app.Activity;
import android.text.TextUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.ianbuttimer.tidder.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_G;

public class AdConfig {

    private final WeakReference<Activity> mActivity;
    private final boolean initialised;
    private final List<AdView> views;

    public AdConfig(Activity activity) {
        this.mActivity = new WeakReference<>(activity);
        this.initialised = false;
        this.views = new ArrayList<>();
    }

    public void initialise() {
        if (!initialised) {
            Activity activity = mActivity.get();
            String testDeviceId = activity.getString(R.string.admob_test_device_id);

            RequestConfiguration.Builder confBuilder = new RequestConfiguration.Builder()
                    .setMaxAdContentRating(MAX_AD_CONTENT_RATING_G);

            if (!TextUtils.isEmpty(testDeviceId) &&
                    !testDeviceId.equals(activity.getString(R.string.admob_test_device_id_todo))) {
                confBuilder.setTestDeviceIds(List.of(testDeviceId));
            }

            MobileAds.setRequestConfiguration(confBuilder.build());
            MobileAds.initialize(activity);
        }
    }

    public boolean addAdView(AdView adView) {
        return views.add(adView);
    }

    public boolean removeAdView(AdView adView) {
        return views.remove(adView);
    }

    public void loadAd(AdView adView) {
        addAdView(adView);
        loadAd();
    }

    public void loadAd() {
        if (!initialised) {
            initialise();
        }
        AdRequest request = new AdRequest.Builder().build();
        for (AdView adView : views) {
            adView.loadAd(request);
        }
    }

    public void onResume() {
        for (AdView adView : views) {
            adView.resume();
        }
    }

    public void onPause() {
        for (AdView adView : views) {
            adView.pause();
        }
    }

    public void onDestroy() {
        for (AdView adView : views) {
            adView.destroy();
        }
    }
}
