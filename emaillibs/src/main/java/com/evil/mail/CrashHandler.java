package com.evil.mail;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 需要在Application中注册，为了要在程序启动器就监控整个程序。
 * 可以重新继承该类重写里面的三个方法setRestartActivityClass和closeAllActivity和sendCrashLog2PM
 */
public class CrashHandler
        implements Thread.UncaughtExceptionHandler
{
    //系统默认的UncaughtException处理类
    private        Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler                    instance;
    //程序的Context对象
    private        Context                         mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos    = new HashMap();
    private String              mAppName = "未知";
    private Thread    mThread;
    private Throwable mThrowable;
    //用于格式化日期,作为日志文件名的一部分

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {}

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        mContext = context;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            mAppName = context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        mThread = thread;
        mThrowable = ex;
        handleException();
    }

    public String getAppName() {
        return mAppName;
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     */
    private void handleException() {
        if (mThrowable == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //收集设备参数信息
                collectDeviceInfo(mContext);
                //发送日志文件给程序猿
                sendCatchInfo2Coder(mThrowable);
            }
        }).start();
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    private void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null
                                     ? "null"
                                     : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (Exception e) {
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(),
                          field.get(null)
                               .toString());
            } catch (Exception e) {
            }
        }
    }

    /**
     * 发送错误信息给开发人员
     */
    private void sendCatchInfo2Coder(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key   = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "<br/>");
        }

        Writer      writer      = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        String result1 = sb.toString();
        //发送给开发人员
        sendLog2Coder(result1);
    }

    private void sendLog2Coder(String result) {
        EmailUtil.sendMail2Me(mAppName, result);
        if (mDefaultHandler == null) {
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } else {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(mThread, mThrowable);
        }
    }
}