package com.gathin.tvhome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/** Remote-friendly settings owned by the launcher; system settings stay one row away. */
public final class LauncherSettingsActivity extends Activity {
    private SharedPreferences choices;
    private final TextView[] columnChoices = new TextView[4];
    private final TextView[] rowChoices = new TextView[4];
    private TextView wallpaperChoice;
    private TextView wallpaperIntervalChoice;
    private TextView ambientGlowChoice;
    private TextView dockOpacityChoice;
    private TextView hinduCalendarChoice;
    private TextView hinduCalendarCountChoice;
    private TextView animationsChoice;
    private TextView advancedChoice;
    private TextView followThemeChoice;
    private int columns;
    private int rowsBelowDock;
    private boolean animations;
    private boolean advancedEffects;
    private boolean ambientGlow;
    private int rotationMinutes;
    private int dockOpacity;
    private boolean hinduCalendarEnabled;
    private int hinduCalendarCount;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_launcher_settings);
        choices = getSharedPreferences(Config.SETTINGS_NAME, MODE_PRIVATE);
        Config config = Config.load(this);
        columns = config.columns;
        rowsBelowDock = config.rowsBelowDock;
        animations = config.animations;
        advancedEffects = config.animations && config.advancedEffects;
        ambientGlow = config.ambientGlow;
        rotationMinutes = config.wallpaperRotationMinutes;
        dockOpacity = config.dockOpacity;
        hinduCalendarEnabled = config.hinduCalendarEnabled;
        hinduCalendarCount = config.hinduCalendarCount;

        int[] ids = {R.id.columns4, R.id.columns5, R.id.columns6, R.id.columns7};
        for (int i = 0; i < ids.length; i++) {
            final int value = i + 4;
            columnChoices[i] = findViewById(ids[i]);
            columnChoices[i].setOnClickListener(v -> {
                if (columns == value) return;
                columns = value;
                choices.edit().putInt("columns", value).apply();
                setResult(RESULT_OK);
                refresh();
            });
        }
        int[] rowIds = {R.id.rows0, R.id.rows1, R.id.rows2, R.id.rows3};
        for (int i = 0; i < rowIds.length; i++) {
            final int value = i;
            rowChoices[i] = findViewById(rowIds[i]);
            rowChoices[i].setOnClickListener(v -> {
                if (rowsBelowDock == value) return;
                rowsBelowDock = value;
                choices.edit().putInt(Config.ROWS_BELOW_DOCK_KEY, value).apply();
                setResult(RESULT_OK);
                refresh();
            });
        }
        // Keep the numeric choices increasing in the Hebrew reading direction. Reorder the
        // actual views so D-pad focus follows their positions without changing the saved values.
        if (getResources().getBoolean(R.bool.rtl_app_grid)) {
            reverseRow(columnChoices);
            reverseRow(rowChoices);
        }
        wallpaperChoice = findViewById(R.id.wallpaperChoice);
        wallpaperChoice.setOnClickListener(v -> showWallpaperPicker());
        wallpaperIntervalChoice = findViewById(R.id.wallpaperIntervalChoice);
        wallpaperIntervalChoice.setOnClickListener(v -> showRotationIntervalPicker());
        ambientGlowChoice = findViewById(R.id.ambientGlowChoice);
        ambientGlowChoice.setOnClickListener(v -> {
            ambientGlow = !ambientGlow;
            choices.edit().putBoolean(Config.AMBIENT_GLOW_KEY, ambientGlow).apply();
            setResult(RESULT_OK);
            refresh();
        });
        dockOpacityChoice = findViewById(R.id.dockOpacityChoice);
        dockOpacityChoice.setOnClickListener(v -> showDockOpacityPicker());
        hinduCalendarChoice = findViewById(R.id.hinduCalendarChoice);
        hinduCalendarChoice.setOnClickListener(v -> {
            hinduCalendarEnabled = !hinduCalendarEnabled;
            choices.edit().putBoolean(Config.HINDU_CALENDAR_KEY, hinduCalendarEnabled).apply();
            setResult(RESULT_OK);
            refresh();
        });
        hinduCalendarCountChoice = findViewById(R.id.hinduCalendarCountChoice);
        hinduCalendarCountChoice.setOnClickListener(v -> showHinduCalendarCountPicker());
        animationsChoice = findViewById(R.id.animationsChoice);
        animationsChoice.setOnClickListener(v -> {
            animations = !animations;
            if (!animations) advancedEffects = false;
            choices.edit().putBoolean("animations", animations)
                    .putBoolean("advanced_effects", advancedEffects).apply();
            setResult(RESULT_OK);
            refresh();
        });
        advancedChoice = findViewById(R.id.advancedChoice);
        advancedChoice.setOnClickListener(v -> {
            if (advancedEffects) {
                setAdvanced(false);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.advanced_effects)
                        .setMessage(R.string.advanced_effects_warning)
                        .setPositiveButton(R.string.enable, (dialog, which) -> setAdvanced(true))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
        followThemeChoice = findViewById(R.id.followThemeChoice);
        followThemeChoice.setOnClickListener(v -> {
            if (!hasOnTvChoices()) return;
            choices.edit().remove("columns").remove(Config.ROWS_BELOW_DOCK_KEY).remove("animations")
                    .remove("advanced_effects").remove(Config.WALLPAPER_CHOICE_KEY)
                    .remove(Config.ROTATION_MINUTES_KEY)
                    .remove(Config.AMBIENT_GLOW_KEY)
                    .remove(Config.DOCK_OPACITY_KEY)
                    .remove(Config.HINDU_CALENDAR_KEY)
                    .remove(Config.HINDU_CALENDAR_COUNT_KEY).commit();
            Config current = Config.load(this);
            columns = current.columns;
            rowsBelowDock = current.rowsBelowDock;
            animations = current.animations;
            advancedEffects = current.animations && current.advancedEffects;
            ambientGlow = current.ambientGlow;
            rotationMinutes = current.wallpaperRotationMinutes;
            dockOpacity = current.dockOpacity;
            hinduCalendarEnabled = current.hinduCalendarEnabled;
            hinduCalendarCount = current.hinduCalendarCount;
            setResult(RESULT_OK);
            refresh();
        });
        findViewById(R.id.defaultHomeChoice).setOnClickListener(v -> {
            Toast.makeText(this, R.string.home_settings_help, Toast.LENGTH_LONG).show();
            open(new Intent(Settings.ACTION_HOME_SETTINGS));
        });
        findViewById(R.id.tvSettingsChoice).setOnClickListener(v ->
                open(new Intent(Settings.ACTION_SETTINGS)));
        refresh();
        columnChoices[Math.max(0, Math.min(columns - 4, 3))].requestFocus();
    }

    private void reverseRow(TextView[] views) {
        LinearLayout row = (LinearLayout) views[0].getParent();
        row.removeAllViews();
        for (int i = views.length - 1; i >= 0; i--) row.addView(views[i]);
    }

    private void showWallpaperPicker() {
        List<File> images = WallpaperRotation.allImages(this);
        List<String> labels = new java.util.ArrayList<>();
        labels.add(getString(R.string.wallpaper_random));
        for (File img : images) {
            labels.add(img.getName());
        }
        labels.add(getString(R.string.wallpaper_none));

        String current = choices.getString(Config.WALLPAPER_CHOICE_KEY, null);
        int checked = 0;
        if ("none".equals(current)) {
            checked = labels.size() - 1;
        } else if ("random".equals(current) || current == null) {
            checked = 0;
        } else {
            int idx = indexOf(images, current);
            if (idx >= 0) checked = idx + 1;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.wallpaper_label)
                .setSingleChoiceItems(labels.toArray(new String[0]), checked, (dialog, which) -> {
                    dialog.dismiss();
                    if (which == 0) {
                        choices.edit().putString(Config.WALLPAPER_CHOICE_KEY, "random").apply();
                    } else if (which == labels.size() - 1) {
                        choices.edit().putString(Config.WALLPAPER_CHOICE_KEY, "none").apply();
                    } else {
                        choices.edit().putString(Config.WALLPAPER_CHOICE_KEY,
                                images.get(which - 1).getName()).apply();
                    }
                    setResult(RESULT_OK);
                    Toast.makeText(this, R.string.wallpaper_applied, Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .setNeutralButton(R.string.wallpaper_theme_default, (dialog, which) -> {
                    choices.edit().remove(Config.WALLPAPER_CHOICE_KEY).apply();
                    setResult(RESULT_OK);
                    refresh();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showRotationIntervalPicker() {
        final int[] minutesOptions = {1, 3, 5, 10, 15, 30, 60};
        final String[] labels = new String[minutesOptions.length];
        int checked = 2;
        for (int i = 0; i < minutesOptions.length; i++) {
            if (minutesOptions[i] == 60) {
                labels[i] = getString(R.string.wallpaper_interval_hour, 1);
            } else {
                labels[i] = getString(R.string.wallpaper_interval_minutes, minutesOptions[i]);
            }
            if (minutesOptions[i] == rotationMinutes) {
                checked = i;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.wallpaper_interval_dialog_title)
                .setSingleChoiceItems(labels, checked, (dialog, which) -> {
                    dialog.dismiss();
                    rotationMinutes = minutesOptions[which];
                    choices.edit().putInt(Config.ROTATION_MINUTES_KEY, rotationMinutes).apply();
                    setResult(RESULT_OK);
                    refresh();
                })
                .setNeutralButton(R.string.wallpaper_theme_default, (dialog, which) -> {
                    choices.edit().remove(Config.ROTATION_MINUTES_KEY).apply();
                    Config cfg = Config.load(this);
                    rotationMinutes = cfg.wallpaperRotationMinutes;
                    setResult(RESULT_OK);
                    refresh();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showDockOpacityPicker() {
        final int[] values = {100, 75, 50, 25, 0};
        final String[] labels = {
                getString(R.string.dock_opacity_100),
                getString(R.string.dock_opacity_75),
                getString(R.string.dock_opacity_50),
                getString(R.string.dock_opacity_25),
                getString(R.string.dock_opacity_0)
        };
        int checked = 2; // 50% default
        for (int i = 0; i < values.length; i++) {
            if (values[i] == dockOpacity) {
                checked = i;
                break;
            }
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.dock_opacity_dialog_title)
                .setSingleChoiceItems(labels, checked, (dialog, which) -> {
                    dialog.dismiss();
                    dockOpacity = values[which];
                    choices.edit().putInt(Config.DOCK_OPACITY_KEY, dockOpacity).apply();
                    setResult(RESULT_OK);
                    refresh();
                })
                .setNeutralButton(R.string.wallpaper_theme_default, (dialog, which) -> {
                    choices.edit().remove(Config.DOCK_OPACITY_KEY).apply();
                    Config cfg = Config.load(this);
                    dockOpacity = cfg.dockOpacity;
                    setResult(RESULT_OK);
                    refresh();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showHinduCalendarCountPicker() {
        final int[] values = {1, 2, 3, 4};
        final String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            labels[i] = getString(R.string.hindu_calendar_events_count, values[i]);
        }
        int checked = hinduCalendarCount - 1;
        if (checked < 0 || checked >= values.length) checked = 0;
        new AlertDialog.Builder(this)
                .setTitle(R.string.hindu_calendar_count_dialog_title)
                .setSingleChoiceItems(labels, checked, (dialog, which) -> {
                    dialog.dismiss();
                    hinduCalendarCount = values[which];
                    choices.edit().putInt(Config.HINDU_CALENDAR_COUNT_KEY, hinduCalendarCount).apply();
                    setResult(RESULT_OK);
                    refresh();
                })
                .setNeutralButton(R.string.wallpaper_theme_default, (dialog, which) -> {
                    choices.edit().remove(Config.HINDU_CALENDAR_COUNT_KEY).apply();
                    Config cfg = Config.load(this);
                    hinduCalendarCount = cfg.hinduCalendarCount;
                    setResult(RESULT_OK);
                    refresh();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private static int indexOf(List<File> images, String name) {
        if (name == null) return -1;
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    private void setAdvanced(boolean enabled) {
        advancedEffects = enabled;
        choices.edit().putBoolean("advanced_effects", enabled).apply();
        setResult(RESULT_OK);
        refresh();
    }

    private void refresh() {
        for (int i = 0; i < columnChoices.length; i++) {
            columnChoices[i].setSelected(columns == i + 4);
        }
        for (int i = 0; i < rowChoices.length; i++) {
            rowChoices[i].setSelected(rowsBelowDock == i);
        }
        animationsChoice.setText(getString(R.string.animations_state,
                getString(animations ? R.string.on : R.string.off)));
        advancedChoice.setText(getString(R.string.advanced_effects_state,
                getString(advancedEffects ? R.string.on : R.string.off)));
        advancedChoice.setEnabled(animations);
        advancedChoice.setAlpha(animations ? 1f : 0.5f);
        String wallpaperCurrent = choices.getString(Config.WALLPAPER_CHOICE_KEY, null);
        String currentLabel;
        if ("none".equals(wallpaperCurrent)) {
            currentLabel = getString(R.string.wallpaper_none);
        } else if ("random".equals(wallpaperCurrent) || wallpaperCurrent == null) {
            currentLabel = getString(R.string.wallpaper_random);
        } else {
            currentLabel = wallpaperCurrent;
        }
        wallpaperChoice.setText(getString(R.string.wallpaper_state, currentLabel));
        String intervalText;
        if (rotationMinutes == 60) {
            intervalText = getString(R.string.wallpaper_interval_hour, 1);
        } else {
            intervalText = getString(R.string.wallpaper_interval_minutes, rotationMinutes);
        }
        wallpaperIntervalChoice.setText(getString(R.string.wallpaper_interval_label, intervalText));
        ambientGlowChoice.setText(getString(R.string.ambient_glow_label,
                getString(ambientGlow ? R.string.on : R.string.off)));

        String dockOpacityLabel;
        if (dockOpacity == 100) dockOpacityLabel = getString(R.string.dock_opacity_100);
        else if (dockOpacity == 75) dockOpacityLabel = getString(R.string.dock_opacity_75);
        else if (dockOpacity == 50) dockOpacityLabel = getString(R.string.dock_opacity_50);
        else if (dockOpacity == 25) dockOpacityLabel = getString(R.string.dock_opacity_25);
        else if (dockOpacity == 0) dockOpacityLabel = getString(R.string.dock_opacity_0);
        else dockOpacityLabel = dockOpacity + "%";
        dockOpacityChoice.setText(getString(R.string.dock_opacity_label, dockOpacityLabel));

        hinduCalendarChoice.setText(getString(R.string.hindu_calendar_label,
                getString(hinduCalendarEnabled ? R.string.on : R.string.off)));
        hinduCalendarCountChoice.setText(getString(R.string.hindu_calendar_count_label, hinduCalendarCount));
        hinduCalendarCountChoice.setEnabled(hinduCalendarEnabled);
        hinduCalendarCountChoice.setAlpha(hinduCalendarEnabled ? 1f : 0.5f);

        followThemeChoice.setSelected(!hasOnTvChoices());
    }

    private boolean hasOnTvChoices() {
        return choices.contains("columns") || choices.contains(Config.ROWS_BELOW_DOCK_KEY)
                || choices.contains("animations") || choices.contains("advanced_effects")
                || choices.contains(Config.WALLPAPER_CHOICE_KEY)
                || choices.contains(Config.ROTATION_MINUTES_KEY)
                || choices.contains(Config.AMBIENT_GLOW_KEY)
                || choices.contains(Config.DOCK_OPACITY_KEY)
                || choices.contains(Config.HINDU_CALENDAR_KEY)
                || choices.contains(Config.HINDU_CALENDAR_COUNT_KEY);
    }

    private void open(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException e) {
            Toast.makeText(this, R.string.not_available, Toast.LENGTH_LONG).show();
        }
    }
}
