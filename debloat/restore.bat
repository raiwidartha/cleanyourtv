@echo off
REM Restore script for Mi TV (MiTV-MOOQ1) debloat
REM Re-enables all disabled packages and sets Google TV Launcher back as default home

set TARGET=[fe80::51d2:1046:3e07:366d%%20]:5555
if not "%~1"=="" set TARGET=%~1

set ADB="D:\Dev\Android\platform-tools\adb.exe" -s %TARGET%

echo Connecting to %TARGET%...
"D:\Dev\Android\platform-tools\adb.exe" connect %TARGET%

echo Re-enabling disabled packages...
%ADB% shell pm enable com.google.android.apps.mediashell
%ADB% shell pm enable com.google.android.katniss
%ADB% shell pm enable com.duokan.factorytest
%ADB% shell pm enable com.mitv.tvlock
%ADB% shell pm enable com.xiaomi.floatingframe
%ADB% shell pm enable com.xm.webcontent
%ADB% shell pm enable com.mitv.tvhome.atv
%ADB% shell pm enable mediatek.factorymenu.ui
%ADB% shell pm enable com.amazon.amazonvideo.livingroom
%ADB% shell pm enable com.xiaomi.mitv.updateservice
%ADB% shell pm enable com.mitv.videoplayer
%ADB% shell pm enable com.mstar.android.tv.disclaimercustomization
%ADB% shell pm enable com.google.android.tvrecommendations
%ADB% shell pm enable fusion.android.tv.demo
%ADB% shell pm enable com.xiaomi.mitv.smartshare
%ADB% shell pm enable com.xiaomi.mimusic2
%ADB% shell pm enable com.google.android.tvlauncher
%ADB% shell pm enable com.google.android.youtube.tvmusic
%ADB% shell pm enable com.google.android.videos
%ADB% shell pm enable com.xiaomo.tv.milegal
%ADB% shell pm enable com.google.android.feedback
%ADB% shell pm enable com.google.android.tv.bugreportsender
%ADB% shell pm enable com.mitv.gallery
%ADB% shell pm enable com.miui.tv.analytics
%ADB% shell pm enable com.xiaomi.wfdsinkhelperservice
%ADB% shell pm enable com.xiaomi.statistic
%ADB% shell pm enable com.google.android.youtube.tv
%ADB% shell pm enable com.mitv.milinkservice
%ADB% shell pm enable com.mitv.dream
%ADB% shell pm enable com.mitv.tvhome.michannel
%ADB% shell pm enable com.google.android.play.games
%ADB% shell pm enable com.xiaomi.mitv.tvmanager

echo Restoring default stock home launcher...
%ADB% shell cmd package set-home-activity com.google.android.tvlauncher/.MainActivity

echo Restore complete!
pause
