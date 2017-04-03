package com.http.watcher;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.proxy.dns.AdvancedHostResolver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by diyuanwang on 2017/4/3.
 */

public class WatcherApplication extends MultiDexApplication {

    private static BrowserMobProxy mobProxy;

    @Override
    public void onCreate() {
        super.onCreate();

        mobProxy = new BrowserMobProxyServer();
        mobProxy.setTrustAllServers(true);

        mobProxy.enableHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.REQUEST_COOKIES,
                CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_HEADERS, CaptureType.REQUEST_COOKIES,
                CaptureType.RESPONSE_CONTENT);

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(new Date(System.currentTimeMillis()));
        mobProxy.newHar(time);

        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // 设置hosts
        if (shp.getString("system_host", "").length() > 0) {
            AdvancedHostResolver advancedHostResolver = mobProxy.getHostNameResolver();
            for (String temp : shp.getString("system_host", "").split("\\n")) {
                if (temp.split(" ").length == 2) {
                    advancedHostResolver.remapHost(temp.split(" ")[1], temp.split(" ")[0]);
                    Log.e("~~~~remapHost ", temp.split(" ")[1] + " " + temp.split(" ")[0]);
                }
            }
            mobProxy.setHostNameResolver(advancedHostResolver);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                mobProxy.start(8888);
            }
        }).start();
    }

    public static BrowserMobProxy getMobProxy(){
        return mobProxy;
    }

}
