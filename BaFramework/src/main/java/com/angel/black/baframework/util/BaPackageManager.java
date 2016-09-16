package com.angel.black.baframework.util;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.IntentCompat;
import android.widget.Toast;

import com.angel.black.baframework.logger.BaLog;

import java.util.List;


/**
 * Created by KimJeongHun on 2016-05-02.
 */
public class BaPackageManager {
    public static final String TAG = BaPackageManager.class.getSimpleName();

    /**
     * 패키지명을 이용하여 해당 어플리케이션 버전정보 확인
     *
     * @param context
     * @param pkg
     * @return
     */
    public static String getPackageVersion(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        String version = null;

        try {
            pi = pm.getPackageInfo(pkg, PackageManager.GET_META_DATA);
            version = pi.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            BaLog.i(TAG, e.toString());
        }
        return version;
    }

    /**
     * 현재 어플의 버전 정보 값을 반환
     *
     * @param context
     * @return
     */
    public static String getPackageVersion(Context context) {
        return getPackageVersion(context, context.getPackageName());
    }

    /**
     * pkg 명으로 설치된 어플리케이션 검색
     *
     * @param context
     * @param pkg
     * @return
     */
    public boolean isInstalledApp(Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;

        try {
            pi = pm.getPackageInfo(pkg, PackageManager.GET_META_DATA);
            ApplicationInfo appInfo = pi.applicationInfo;

            if (appInfo == null)
                return false;
            else
                return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 패키지 명을 이용하여 다른 어플리케이션 구동
     *
     * @param context
     * @param activity
     * @param pkg
     */
    public void executePackage(Context context, String activity, String pkg) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkg);

        try {
            context.startActivity(intent);
        }
        catch (Exception e) {
            BaLog.i(TAG, e.toString());
        }
    }

    /**
     * 패키지명과 실행 액티비티 이름을 이용하여 다른 어플리케이션 구동
     *
     * @param context
     * @param pkg
     * @param cls
     */
    public void executeApp(Context context, String pkg, String cls) {
        ComponentName compname = new ComponentName(pkg, cls);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(compname);

        try {
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(context, "해당 어플이 없습니다 : " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 브라우저에서 url 의 링크를 연다.
     *
     * @param context
     * @param url
     */
    public static void executeBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 현재 실행중인 액티비티를 검색
     *
     * @param context
     * @return
     */
    public static String getCurrentTopActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> runningtaskinfo = activityManager.getRunningTasks(1);

        return runningtaskinfo.get(0).topActivity.getClassName();
    }

    /**
     * 현재 실행중인 액티비티를 검색
     *
     * @param context
     * @return
     */
    public static ComponentName getCurrentActivityComponentName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> runningtaskinfo = activityManager.getRunningTasks(1);

        return runningtaskinfo.get(0).topActivity;
    }
    /**
     * 현재 실행중인 패키지명을 검색
     *
     * @param context
     * @return
     */
    public static String getCurrentPackage(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> runningtaskinfo = activityManager.getRunningTasks(1);

        return runningtaskinfo.get(0).topActivity.getPackageName();
    }


    /**
     * 현재 최상위에서 실행중인 프리마켓 액티비티 명을 가져온다.
     * 포그라운드 아니고 백그라운드 상태일 경우 null 리턴
     * @param context
     * @return
     */
    public static String getSpecificActivityNameOnTop(Context context, String pkgName) {
        if(isSpecificActivityOnTop(context, pkgName)) {
            return getCurrentTopActivity(context);
        }

        return null;
    }

    public static boolean isSpecificActivityOnTop(Context context, String pkgName) {
        return getCurrentTopActivity(context).contains(pkgName);
    }

    public static boolean isRunningProcess(Context context, String pkgName)	{
        ActivityManager activity_manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = activity_manager.getRunningAppProcesses();

        for(int i=0; i < appList.size(); i++)	{
            if(appList.get(i).processName.equals(pkgName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 앱을 재시작시킨다.
     */
    public static void restartApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        System.exit(0);
    }
}
