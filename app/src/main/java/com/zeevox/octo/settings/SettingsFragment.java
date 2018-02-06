/*
 * Copyright (C) 2017 Timothy "ZeevoX" Langer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.zeevox.octo.settings;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.zeevox.octo.R;
import com.zeevox.octo.wallpaper.OcquariumWallpaperService;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_ocquarium);

        findPreference("set_live_wallpaper").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(
                        WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(getActivity(), OcquariumWallpaperService.class));
                startActivity(intent);
                return false;
            }
        });

        findPreference("restart_live_wallpaper").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity(), "Restarting live wallpaper...", Toast.LENGTH_SHORT).show();
                restartLiveWallpaper();
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        restartLiveWallpaper();
    }

    @Override
    public void onResume() {
        super.onResume();
        Preference setLiveWallpaper = findPreference("set_live_wallpaper");
        Preference restartLiveWallpaper = findPreference("restart_live_wallpaper");
        if (isLiveWallpaperSet()) {
            setLiveWallpaper.setEnabled(false);
            setLiveWallpaper.setSummary("Ocquarium live wallpaper already set.");
            restartLiveWallpaper.setEnabled(true);
        }
    }

    private boolean isLiveWallpaperSet() {
        WallpaperInfo info = WallpaperManager.getInstance(getActivity()).getWallpaperInfo();
        return info != null && info.getPackageName().equals(getActivity().getPackageName());
    }

    private void restartLiveWallpaper() {
        if (isLiveWallpaperSet()) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            editor.putBoolean("restart_live_wallpaper", true);
            editor.apply();
        }
    }
}
