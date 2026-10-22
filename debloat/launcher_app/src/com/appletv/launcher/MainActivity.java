package com.appletv.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.tv.TvContract;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity {

    private TextView tvGreeting;
    private TextView tvClock;
    private TextView tvDate;
    private TextView btnInputs;
    private TextView btnWifi;
    private TextView btnSettings;
    private TextView tvFocusedTitle;
    private View viewAmbientGlow;
    private ImageView ivWallpaper;
    private ScrollView mainScrollView;
    private LinearLayout upperViewport;
    private LinearLayout dropdownInputs;
    private LinearLayout dropdownInputsList;
    private GridLayout gridAllApps;

    private final FrameLayout[] dockTiles = new FrameLayout[4];
    private final String[] dockPackages = new String[]{
            "org.smarttube.stable",
            "com.netflix.ninja",
            "com.internet.tvbrowser",
            "com.alphainventor.filemanager"
    };
    private final String[] dockLabels = new String[]{
            "SmartTube",
            "Netflix",
            "BrowseHere",
            "File Manager +"
    };
    private final int[] dockGlowColors = new int[]{
            Color.parseColor("#B3FF3D00"),
            Color.parseColor("#B3E50914"),
            Color.parseColor("#B300B0FF"),
            Color.parseColor("#B32979FF")
    };

    private final List<FrameLayout> gridTileFrames = new ArrayList<>();
    private final List<View> dropdownItemViews = new ArrayList<>();

    private View lastFocusedView = null;

    private final Handler timeHandler = new Handler(Looper.getMainLooper());
    private final Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            updateTimeAndGreeting();
            timeHandler.postDelayed(this, 15000);
        }
    };

    private static class InputItem {
        final String label;
        final String inputId;
        final String appPackage;

        InputItem(String label, String inputId, String appPackage) {
            this.label = label;
            this.inputId = inputId;
            this.appPackage = appPackage;
        }
    }

    private final List<InputItem> tvInputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadCustomFont();
        initWallpaper();
        setupTopBarNavigation();
        setupDock();
        setupTvInputs();
        loadAllApps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTimeAndGreeting();
        updateWifiStatus();
        timeHandler.post(timeRunnable);

        if (dropdownInputs.getVisibility() == View.VISIBLE) {
            dropdownInputs.setVisibility(View.GONE);
        }

        if (lastFocusedView != null) {
            lastFocusedView.requestFocus();
        } else if (dockTiles[0] != null) {
            dockTiles[0].requestFocus();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        timeHandler.removeCallbacks(timeRunnable);
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tv_greeting);
        tvClock = findViewById(R.id.tv_clock);
        tvDate = findViewById(R.id.tv_date);
        btnInputs = findViewById(R.id.btn_inputs);
        btnWifi = findViewById(R.id.btn_wifi);
        btnSettings = findViewById(R.id.btn_settings);
        tvFocusedTitle = findViewById(R.id.tv_focused_app_title);
        viewAmbientGlow = findViewById(R.id.view_ambient_glow);
        ivWallpaper = findViewById(R.id.iv_wallpaper);
        mainScrollView = findViewById(R.id.main_scroll_view);
        upperViewport = findViewById(R.id.upper_viewport);
        dropdownInputs = findViewById(R.id.dropdown_inputs);
        dropdownInputsList = findViewById(R.id.dropdown_inputs_list);
        gridAllApps = findViewById(R.id.grid_all_apps);

        dockTiles[0] = findViewById(R.id.tile_dock_0);
        dockTiles[1] = findViewById(R.id.tile_dock_1);
        dockTiles[2] = findViewById(R.id.tile_dock_2);
        dockTiles[3] = findViewById(R.id.tile_dock_3);
    }

    private void loadCustomFont() {
        try {
            Typeface outfit = Typeface.createFromAsset(getAssets(), "fonts/Outfit.ttf");
            if (outfit != null) {
                tvGreeting.setTypeface(outfit);
                tvClock.setTypeface(outfit);
                tvDate.setTypeface(outfit);
                btnInputs.setTypeface(outfit);
                btnWifi.setTypeface(outfit);
                btnSettings.setTypeface(outfit);
                tvFocusedTitle.setTypeface(outfit, Typeface.BOLD);
                TextView tvHeader = findViewById(R.id.tv_all_apps_header);
                if (tvHeader != null) {
                    tvHeader.setTypeface(outfit, Typeface.BOLD);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initWallpaper() {
        File wpJpg = new File("/sdcard/wallpaper.jpg");
        File wpPng = new File("/sdcard/wallpaper.png");
        File chosen = wpJpg.exists() ? wpJpg : (wpPng.exists() ? wpPng : null);
        if (chosen != null) {
            try {
                Bitmap bmp = BitmapFactory.decodeFile(chosen.getAbsolutePath());
                if (bmp != null) {
                    ivWallpaper.setImageBitmap(bmp);
                    ivWallpaper.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTimeAndGreeting() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour >= 4 && hour < 12) {
            greeting = "Good morning";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Good afternoon";
        } else {
            greeting = "Good evening";
        }
        tvGreeting.setText(greeting);

        SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvClock.setText(timeFmt.format(new Date()));

        SimpleDateFormat dateFmt = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        tvDate.setText(dateFmt.format(new Date()));
    }

    private void updateWifiStatus() {
        try {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm != null && wm.isWifiEnabled()) {
                WifiInfo info = wm.getConnectionInfo();
                if (info != null && info.getSSID() != null) {
                    String ssid = info.getSSID().replace("\"", "");
                    if (!ssid.isEmpty() && !ssid.equals("<unknown ssid>")) {
                        btnWifi.setText(ssid);
                        return;
                    }
                }
                List<android.net.wifi.WifiConfiguration> configs = wm.getConfiguredNetworks();
                if (configs != null) {
                    for (android.net.wifi.WifiConfiguration config : configs) {
                        if (config.status == android.net.wifi.WifiConfiguration.Status.CURRENT && config.SSID != null) {
                            String ssid = config.SSID.replace("\"", "");
                            if (!ssid.isEmpty() && !ssid.equals("<unknown ssid>")) {
                                btnWifi.setText(ssid);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (netInfo != null && netInfo.isConnected()) {
                    String extra = netInfo.getExtraInfo();
                    if (extra != null) {
                        String clean = extra.replace("\"", "");
                        if (!clean.isEmpty() && !clean.equals("<unknown ssid>")) {
                            btnWifi.setText(clean);
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnWifi.setText("Wi-Fi");
    }

    private void setupTopBarNavigation() {
        btnInputs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleInputsDropdown();
            }
        });

        btnInputs.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                animateFocus(v, hasFocus, 1.08f);
                if (hasFocus) {
                    lastFocusedView = v;
                    mainScrollView.smoothScrollTo(0, 0);
                }
            }
        });

        btnInputs.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnWifi.requestFocus();
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    dockTiles[0].requestFocus();
                    return true;
                }
                return false;
            }
        });

        btnWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnWifi.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                animateFocus(v, hasFocus, 1.08f);
                if (hasFocus) {
                    lastFocusedView = v;
                    mainScrollView.smoothScrollTo(0, 0);
                }
            }
        });

        btnWifi.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnInputs.requestFocus();
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    btnSettings.requestFocus();
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    dockTiles[1].requestFocus();
                    return true;
                }
                return false;
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnSettings.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                animateFocus(v, hasFocus, 1.08f);
                if (hasFocus) {
                    lastFocusedView = v;
                    mainScrollView.smoothScrollTo(0, 0);
                }
            }
        });

        btnSettings.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    btnWifi.requestFocus();
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    dockTiles[2].requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private void setupTvInputs() {
        tvInputs.clear();
        tvInputs.add(new InputItem("TV (Antena)", "uri:content://android.media.tv/channel/1", null));
        tvInputs.add(new InputItem("HDMI 1", "com.mediatek.tvinput/.hdmi.HDMIInputService/HW4", null));
        tvInputs.add(new InputItem("HDMI 2", "com.mediatek.tvinput/.hdmi.HDMIInputService/HW5", null));
        tvInputs.add(new InputItem("HDMI 3", "com.mediatek.tvinput/.hdmi.HDMIInputService/HW6", null));
        tvInputs.add(new InputItem("AV (Composite)", "com.mediatek.tvinput/.composite.CompositeInputService/HW1", null));
        tvInputs.add(new InputItem("Pindai Saluran TV", "intent:com.mediatek.tvinput/.tuner.TunerInputServiceSetup", null));

        dropdownInputsList.removeAllViews();
        dropdownItemViews.clear();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < tvInputs.size(); i++) {
            final int index = i;
            final InputItem item = tvInputs.get(i);
            TextView itemTv = (TextView) inflater.inflate(R.layout.item_dropdown_input, dropdownInputsList, false);
            itemTv.setText(item.label);

            itemTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchToInput(item);
                    dropdownInputs.setVisibility(View.GONE);
                }
            });

            itemTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    animateFocus(v, hasFocus, 1.04f);
                }
            });

            itemTv.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (index > 0) {
                            dropdownItemViews.get(index - 1).requestFocus();
                        } else {
                            dropdownInputs.setVisibility(View.GONE);
                            btnInputs.requestFocus();
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (index < dropdownItemViews.size() - 1) {
                            dropdownItemViews.get(index + 1).requestFocus();
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        dropdownInputs.setVisibility(View.GONE);
                        btnInputs.requestFocus();
                        return true;
                    }
                    return false;
                }
            });

            dropdownItemViews.add(itemTv);
            dropdownInputsList.addView(itemTv);
        }
    }

    private void toggleInputsDropdown() {
        if (dropdownInputs.getVisibility() == View.VISIBLE) {
            dropdownInputs.setVisibility(View.GONE);
            btnInputs.requestFocus();
        } else {
            dropdownInputs.setVisibility(View.VISIBLE);
            if (!dropdownItemViews.isEmpty()) {
                dropdownItemViews.get(0).requestFocus();
            }
        }
    }

    private void switchToInput(InputItem item) {
        try {
            if (item.inputId != null) {
                if (item.inputId.startsWith("uri:")) {
                    Uri uri = Uri.parse(item.inputId.substring(4));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (item.inputId.startsWith("intent:")) {
                    String comp = item.inputId.substring(7);
                    String[] parts = comp.split("/");
                    String cls = parts[1].startsWith(".") ? parts[0] + parts[1] : parts[1];
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(parts[0], cls));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Uri uri = TvContract.buildChannelUriForPassthroughInput(item.inputId);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else if (item.appPackage != null) {
                launchPackage(item.appPackage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDock() {
        PackageManager pm = getPackageManager();

        for (int i = 0; i < dockTiles.length; i++) {
            final int index = i;
            final FrameLayout tile = dockTiles[i];
            final String pkg = dockPackages[i];

            ImageView iv = new ImageView(this);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setPadding(8, 8, 8, 8);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            tile.addView(iv, lp);

            CharSequence appName = dockLabels[i];
            Drawable iconDrawable = null;

            try {
                Intent launchIntent = pm.getLaunchIntentForPackage(pkg);
                if (launchIntent != null) {
                    List<ResolveInfo> list = pm.queryIntentActivities(launchIntent, 0);
                    if (!list.isEmpty()) {
                        ResolveInfo ri = list.get(0);
                        appName = ri.loadLabel(pm);
                        iconDrawable = ri.activityInfo.loadBanner(pm);
                        if (iconDrawable == null) {
                            iconDrawable = ri.activityInfo.loadLogo(pm);
                        }
                        if (iconDrawable == null) {
                            iconDrawable = ri.activityInfo.loadIcon(pm);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (iconDrawable != null) {
                iv.setImageDrawable(iconDrawable);
            }

            final String displayName = (appName != null && appName.length() > 0) ? appName.toString() : dockLabels[i];
            final int glowColor = dockGlowColors[i];

            tile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchPackage(pkg);
                }
            });

            tile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    animateFocus(v, hasFocus, 1.10f);
                    if (hasFocus) {
                        lastFocusedView = v;
                        mainScrollView.smoothScrollTo(0, 0);
                        tvFocusedTitle.setText(displayName);
                        setAmbientGlow(glowColor);
                    }
                }
            });

            // Deterministic D-Pad navigation for dock
            tile.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() != KeyEvent.ACTION_DOWN) return false;

                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (index > 0) {
                            dockTiles[index - 1].requestFocus();
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (index < dockTiles.length - 1) {
                            dockTiles[index + 1].requestFocus();
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (index == 0) {
                            btnInputs.requestFocus();
                        } else if (index == 1) {
                            btnWifi.requestFocus();
                        } else {
                            btnSettings.requestFocus();
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        focusGridItem(index);
                        return true;
                    }
                    return false;
                }
            });
        }

        dockTiles[0].post(new Runnable() {
            @Override
            public void run() {
                dockTiles[0].requestFocus();
                setAmbientGlow(dockGlowColors[0]);
            }
        });
    }

    private void focusGridItem(int col) {
        if (gridTileFrames.isEmpty()) return;
        int targetIndex = Math.min(col, gridTileFrames.size() - 1);
        FrameLayout targetFrame = gridTileFrames.get(targetIndex);
        if (targetFrame != null) {
            targetFrame.requestFocus();
            mainScrollView.smoothScrollTo(0, upperViewport.getHeight() - 40);
        }
    }

    private void setAmbientGlow(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gd.setGradientRadius(360);
        gd.setColors(new int[]{color, Color.TRANSPARENT});
        viewAmbientGlow.setBackground(gd);
    }

    private void loadAllApps() {
        gridAllApps.removeAllViews();
        gridTileFrames.clear();
        PackageManager pm = getPackageManager();

        Set<String> dockSet = new HashSet<>();
        for (String p : dockPackages) {
            dockSet.add(p);
        }

        Intent leanbackIntent = new Intent(Intent.ACTION_MAIN);
        leanbackIntent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> activities = pm.queryIntentActivities(leanbackIntent, 0);

        Intent standardIntent = new Intent(Intent.ACTION_MAIN);
        standardIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> standardList = pm.queryIntentActivities(standardIntent, 0);

        Set<String> addedPackages = new HashSet<>();
        addedPackages.add(getPackageName());
        addedPackages.add("com.mediatek.tv.service");
        addedPackages.add("com.android.tv.settings");
        addedPackages.add("com.google.android.packageinstaller");
        addedPackages.add("com.google.android.webview");
        addedPackages.add("com.mediatek.wwtv.webview");
        addedPackages.add("com.mitv.tvhome.atv");

        List<ResolveInfo> combined = new ArrayList<>();
        if (activities != null) {
            for (ResolveInfo ri : activities) {
                String pkg = ri.activityInfo.packageName;
                if (!dockSet.contains(pkg) && !addedPackages.contains(pkg)) {
                    combined.add(ri);
                    addedPackages.add(pkg);
                }
            }
        }
        if (standardList != null) {
            for (ResolveInfo ri : standardList) {
                String pkg = ri.activityInfo.packageName;
                if (!dockSet.contains(pkg) && !addedPackages.contains(pkg)) {
                    combined.add(ri);
                    addedPackages.add(pkg);
                }
            }
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        final int totalItems = combined.size();

        for (int i = 0; i < totalItems; i++) {
            final int index = i;
            final ResolveInfo ri = combined.get(i);
            final String pkg = ri.activityInfo.packageName;
            final CharSequence appLabel = ri.loadLabel(pm);

            View itemView = inflater.inflate(R.layout.item_grid_app, gridAllApps, false);
            final FrameLayout frame = itemView.findViewById(R.id.tile_app_frame);
            ImageView iv = itemView.findViewById(R.id.iv_app_banner);
            TextView tv = itemView.findViewById(R.id.tv_app_label);

            tv.setText(appLabel);

            Drawable icon = ri.activityInfo.loadBanner(pm);
            if (icon == null) icon = ri.activityInfo.loadLogo(pm);
            if (icon == null) icon = ri.activityInfo.loadIcon(pm);
            if (icon != null) {
                iv.setImageDrawable(icon);
            }

            frame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchPackage(pkg);
                }
            });

            frame.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    animateFocus(v, hasFocus, 1.08f);
                    if (hasFocus) {
                        lastFocusedView = v;
                        tvFocusedTitle.setText(appLabel);
                        setAmbientGlow(Color.parseColor("#66667788"));
                    }
                }
            });

            // Deterministic D-Pad navigation for 4-column grid
            frame.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() != KeyEvent.ACTION_DOWN) return false;

                    int col = index % 4;
                    int row = index / 4;

                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (col > 0 && index > 0) {
                            gridTileFrames.get(index - 1).requestFocus();
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (col < 3 && index < totalItems - 1) {
                            gridTileFrames.get(index + 1).requestFocus();
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (row == 0) {
                            int targetDock = Math.min(col, dockTiles.length - 1);
                            dockTiles[targetDock].requestFocus();
                            mainScrollView.smoothScrollTo(0, 0);
                        } else {
                            gridTileFrames.get(index - 4).requestFocus();
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (index + 4 < totalItems) {
                            gridTileFrames.get(index + 4).requestFocus();
                        }
                        return true;
                    }
                    return false;
                }
            });

            gridTileFrames.add(frame);
            gridAllApps.addView(itemView);
        }
    }

    private void launchPackage(String pkg) {
        try {
            if ("com.mediatek.wwtv.tvcenter".equals(pkg)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://android.media.tv/channel/1"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }
            Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateFocus(View view, boolean hasFocus, float scale) {
        float targetScale = hasFocus ? scale : 1.0f;
        float elevation = hasFocus ? 12f : 0f;
        view.animate()
                .scaleX(targetScale)
                .scaleY(targetScale)
                .translationZ(elevation)
                .setDuration(160)
                .start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dropdownInputs.getVisibility() == View.VISIBLE) {
                dropdownInputs.setVisibility(View.GONE);
                btnInputs.requestFocus();
                return true;
            }
            if (mainScrollView.getScrollY() > 50) {
                mainScrollView.smoothScrollTo(0, 0);
                dockTiles[0].requestFocus();
                return true;
            }
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_TV_INPUT) {
            toggleInputsDropdown();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_SETTINGS) {
            try {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
