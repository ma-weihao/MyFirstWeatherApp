package top.maweihao.weather.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.blankj.utilcode.util.LogUtils;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import github.hellocsl.simpleconfig.Config;
import github.hellocsl.simpleconfig.SimpleConfig;
import top.maweihao.weather.R;
import top.maweihao.weather.android_view.dynamicweather.BaseDrawer;
import top.maweihao.weather.entity.dao.NewHeWeatherNow;

/**
 * Created by ma on 17-3-5.
 * this is a utility class
 */

public class Utility {

    private static final String TAG = Utility.class.getSimpleName();

    /**
     * 获取当前系统语言格式
     *
     * @param mContext
     * @return
     */
    public static String getCurrentLanguage(Context mContext) {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String country = locale.getCountry();
        return language + "_" + country;
    }

    /**
     *
     * @return
     */
    public static int getTimeShift() {
        TimeZone tz = TimeZone.getDefault();
        int timeShift = (tz.getRawOffset() + tz.getDSTSavings()) / 1000;
        return timeShift;
    }

    /**
     * 创建自定义的配置文件读取方法
     *
     * @param context Context
     * @return SimpleConfig
     */
    public static SimpleConfig createSimpleConfig(Context context) {
        return new SimpleConfig.Builder(context).configFactory(new Config.Factory() {
            @Override
            public Config newConfig(Context context, String name, int mode) {
                return new PreferenceConfig(context);
            }
        }).build();
    }

    /**
     * 把 float 四舍五入再返回 String，用来处理温度
     */
    public static String stringRoundDouble(double f) {
        return String.valueOf(Math.round(f));
    }

    /**
     * 把 float 四舍五入再返回 int，用来处理温度
     */
    public static int intRoundDouble(double s) {
        return (int) Math.round(s);
    }

    public static int Cel2Fah(int cel) {
        double fah = cel * 1.8 + 32;
        return (int) Math.round(fah);
    }

    public static int Cel2Fah(float cel) {
        double fah = cel * 1.8 + 32;
        return (int) Math.round(fah);
    }

    /**
     * 通过天气获取背景图
     */
    public static @BaseDrawer.Type
    int chooseBgImage(String skycon) {
        switch (skycon) {
            case "CLEAR_DAY":
                return BaseDrawer.Type.CLEAR_D;
            case "CLEAR_NIGHT":
                return BaseDrawer.Type.CLEAR_N;
            case "PARTLY_CLOUDY_DAY":
                return BaseDrawer.Type.OVERCAST_D;
            case "PARTLY_CLOUDY_NIGHT":
                return BaseDrawer.Type.OVERCAST_N;
            case "CLOUDY":
                return BaseDrawer.Type.CLOUDY_D;
            case "RAIN":
                return BaseDrawer.Type.RAIN_D;
            case "SNOW":
                return BaseDrawer.Type.SNOW_D;
            case "WIND":
                return BaseDrawer.Type.WIND_D;
            case "FOG":
                return BaseDrawer.Type.FOG_D;
            case "HAZE":
                return BaseDrawer.Type.HAZE_D;
            case "SLEET":
                return BaseDrawer.Type.RAIN_N;
            default:
                return BaseDrawer.Type.UNKNOWN_D;
        }
    }

    /**
     * 获得天气 String
     *
     * @param context       context
     * @param skycon        天气标识
     * @param precipitation 雨量
     * @param mode          Mode 代表 precipitation 的精度，分为小时级和分钟级， 以确定雨量
     * @return 描述（string）
     */
    public static final int MINUTELY_MODE = 4;
    public static final int HOURLY_MODE = 5;
    public static String chooseWeatherSkycon(Context context, String skycon, double precipitation, int mode) {
        if (context != null) {
            switch (skycon) {
                case "CLEAR_DAY":
                    return context.getResources().getString(R.string.CLEAR_DAY);
                case "CLEAR_NIGHT":
                    return context.getResources().getString(R.string.CLEAR_NIGHT);
                case "PARTLY_CLOUDY_DAY":
                    return context.getResources().getString(R.string.PARTLY_CLOUDY_DAY);
                case "PARTLY_CLOUDY_NIGHT":
                    return context.getResources().getString(R.string.PARTLY_CLOUDY_NIGHT);
                case "CLOUDY":
                    return context.getResources().getString(R.string.CLOUDY);
                case "RAIN":
                    switch (mode) {
                        case MINUTELY_MODE:
                            if (precipitation <= 0.25)
                                return context.getResources().getString(R.string.LIGHT_RAIN);
                            else if (precipitation <= 0.35)
                                return context.getResources().getString(R.string.MODERATE_RAIN);
                            else
                                return context.getResources().getString(R.string.HEAVY_RAIN);
                        case HOURLY_MODE:
                            if (precipitation <= 0.9)
                                return context.getResources().getString(R.string.LIGHT_RAIN);
                            else if (precipitation <= 2.87)
                                return context.getResources().getString(R.string.MODERATE_RAIN);
                            else
                                return context.getResources().getString(R.string.HEAVY_RAIN);
                    }
                case "SNOW":
                    switch (mode) {
                        case MINUTELY_MODE:
                            if (precipitation <= 0.25)
                                return context.getResources().getString(R.string.LIGHT_SNOW);
                            else if (precipitation <= 0.35)
                                return context.getResources().getString(R.string.MODERATE_SNOW);
                            else
                                return context.getResources().getString(R.string.HEAVY_SNOW);
                        case HOURLY_MODE:
                            if (precipitation <= 0.9)
                                return context.getResources().getString(R.string.LIGHT_SNOW);
                            else if (precipitation <= 2.87)
                                return context.getResources().getString(R.string.MODERATE_SNOW);
                            else
                                return context.getResources().getString(R.string.HEAVY_SNOW);
                    }
                case "WIND":
                    return context.getResources().getString(R.string.WIND);
                case "FOG":
                    return context.getResources().getString(R.string.FOG);
                case "HAZE":
                    return context.getResources().getString(R.string.HAZE);
                case "SLEET":
                    return context.getResources().getString(R.string.SLEET);
                default:
                    LogUtils.e("unknown weather: " + skycon);
                    return skycon;
            }
        } else {
            return null;
        }
    }

    /**
     * 获得天气图标
     *
     * @param skycon        天气标识
     * @param precipitation 雨量
     * @param mode          Mode 代表 precipitation 的精度，分为小时级和分钟级， 以确定雨量
     * @param simpleIcon    是否返回简单黑白风格的 icon
     * @return 天气图片
     */
    public static int chooseWeatherIcon(String skycon, double precipitation, int mode, boolean simpleIcon) {
        switch (skycon) {
            case "CLEAR_DAY":
                return simpleIcon ? R.mipmap.simple_clear_day : R.mipmap.weather_clear;
            case "CLEAR_NIGHT":
                return simpleIcon ? R.mipmap.simple_clear_night : R.mipmap.weather_clear_night;
            case "PARTLY_CLOUDY_DAY":
                return simpleIcon ? R.mipmap.simple_cloudy_day : R.mipmap.weather_few_clouds;
            case "PARTLY_CLOUDY_NIGHT":
                return simpleIcon ? R.mipmap.simple_cloudy_night : R.mipmap.weather_few_clouds_night;
            case "CLOUDY":
                return simpleIcon ? R.mipmap.simple_cloudy_2 : R.mipmap.weather_clouds;
            case "RAIN":
                switch (mode) {
                    case MINUTELY_MODE:
                        if (precipitation <= 0.25)
                            return simpleIcon ? R.mipmap.simple_light_rain : R.mipmap.weather_drizzle_day;
                        else if (precipitation <= 0.35)
                            return simpleIcon ? R.mipmap.simple_medium_rain : R.mipmap.weather_rain_day;
                        else
                            return simpleIcon ? R.mipmap.simple_heavy_rain : R.mipmap.weather_showers_day;
                    case HOURLY_MODE:
                        if (precipitation <= 0.9)
                            return simpleIcon ? R.mipmap.simple_light_rain : R.mipmap.weather_drizzle_day;
                        else if (precipitation <= 2.87)
                            return simpleIcon ? R.mipmap.simple_medium_rain : R.mipmap.weather_rain_day;
                        else
                            return simpleIcon ? R.mipmap.simple_heavy_rain : R.mipmap.weather_showers_day;
                }
            case "SNOW":
                switch (mode) {
                    case MINUTELY_MODE:
                        if (precipitation <= 0.25)
                            return simpleIcon ? R.mipmap.simple_snow : R.mipmap.weather_snow;
                        else if (precipitation <= 0.35)
                            return simpleIcon ? R.mipmap.simple_snow : R.mipmap.weather_snow;
                        else
                            return simpleIcon ? R.mipmap.simple_snow_storm : R.mipmap.weather_big_snow;
                    case HOURLY_MODE:
                        if (precipitation <= 0.9)
                            return simpleIcon ? R.mipmap.simple_snow : R.mipmap.weather_snow;
                        else if (precipitation <= 2.87)
                            return simpleIcon ? R.mipmap.simple_snow : R.mipmap.weather_snow;
                        else
                            return simpleIcon ? R.mipmap.simple_snow_storm : R.mipmap.weather_big_snow;
                }
            case "WIND":
                return simpleIcon ? R.mipmap.simple_wind : R.mipmap.weather_wind;
            case "FOG":
                return simpleIcon ? R.mipmap.simple_wind : R.mipmap.weather_fog;
            case "HAZE":
                return simpleIcon ? R.mipmap.simple_fog_day : R.mipmap.weather_haze;
            case "SLEET":
                return simpleIcon ? R.mipmap.simple_sleet : R.mipmap.icon_sleet;
            default:
                LogUtils.e("failed to choose weather icon " + skycon + " " + precipitation);
                return R.mipmap.weather_none_available;
        }
    }

    /**
     * 系统语言是否为中文
     *
     * @param context context
     * @return isChinese?
     */
    public static boolean isChinese(Context context) {
        String lan = getCurrentLanguage(context);
        return lan.startsWith("zh");
    }

    /**
     * 获得风向
     *
     * @param context   context
     * @param direction double
     * @return 风向
     */
    public static String getWindDirection(Context context, double direction) {
        String dir;
        if (direction <= 22.5 || direction >= 337.5) {
            dir = context.getResources().getString(R.string.north);
        } else if (direction <= 67.5) {
            dir = context.getResources().getString(R.string.northeast);
        } else if (direction <= 112.5) {
            dir = context.getResources().getString(R.string.east);
        } else if (direction <= 157.5) {
            dir = context.getResources().getString(R.string.southeast);
        } else if (direction <= 202.5) {
            dir = context.getResources().getString(R.string.south);
        } else if (direction <= 247.5) {
            dir = context.getResources().getString(R.string.southwest);
        } else if (direction <= 292.5) {
            dir = context.getResources().getString(R.string.west);
        } else {
            dir = context.getResources().getString(R.string.northwest);
        }
        return dir;
    }

    /**
     * 获得风力
     *
     * @param context context
     * @param speed   double
     * @return string
     */
    public static String getWindLevel(Context context, double speed) {
        int level;
        String info;
        if (speed <= 0.72) {
            level = 0;
            info = "无风";
        } else if (speed <= 5.4) {
            level = 1;
            info = "软风";
        } else if (speed <= 11.88) {
            level = 2;
            info = "轻风";
        } else if (speed <= 19.44) {
            level = 3;
            info = "微风";
        } else if (speed <= 28.44) {
            level = 4;
            info = "和风";
        } else if (speed <= 38.52) {
            level = 5;
            info = "劲风";
        } else if (speed <= 49.68) {
            level = 6;
            info = "强风";
        } else if (speed <= 61.56) {
            level = 7;
            info = "疾风";
        } else if (speed <= 74.52) {
            level = 8;
            info = "大风";
        } else if (speed <= 87.84) {
            level = 9;
            info = "烈风";
        } else if (speed <= 102.24) {
            level = 10;
            info = "狂风";
        } else if (speed <= 117.36) {
            level = 11;
            info = "暴风";
        } else {
            level = 12;
            info = "飓风";
        }
        if (isChinese(context)) {
            return level + " 级" + info;
        } else {
            return "LEVEL " + level;
        }
    }

    /**
     * 判断时间和现在的时间相差
     *
     * @param mills 时间
     */
    public static String getTime(Context context, long mills) {
        Calendar now = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mills);
        if (now.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH) &&
                now.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
            Date date = new Date();
            date.setTime(mills);
            return simpleDateFormat.format(date);
        } else {
            now.set(Calendar.HOUR, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            int intervalDay = Long.valueOf((now.getTimeInMillis() - cal.getTimeInMillis())
                    / (24 * 60 * 60 * 1000)).intValue();
            if (intervalDay == 1) {
                return context.getResources().getString(R.string.yesterday);
            } else {
                return context.getResources().getString(R.string.days_before, intervalDay);
//                return (-1 * intervalDay) + " " + context.getResources().getString(R.string.days_before);
            }
        }
    }

    /**
     * 直接返回 'now'
     */
    public static String getTime(Context context) {
        return context.getResources().getString(R.string.just_now);
    }

    /**
     * getIP 获取网络IP地址(优先获取wifi地址)
     *
     * @param ctx context
     * @return ip地址字符串
     */
    public static String getIP(Context ctx) {
        WifiManager wifiManager = (WifiManager)
                ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return (wifiManager != null && wifiManager.isWifiEnabled()) ? getWifiIP(wifiManager) : getGPRSIP();
    }

    private static String getWifiIP(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return intToIp(wifiInfo.getIpAddress());
    }

    private static String getGPRSIP() {
        String ip = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                for (Enumeration<InetAddress> enumIpAddr = en.nextElement().getInetAddresses();
                     enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip = null;
        }
        return ip;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

    public static int getStatusBarHeight(Resources r) {
        int resourceId = r.getIdentifier("status_bar_height", "dimen", "android");
        return r.getDimensionPixelSize(resourceId);
    }

    /**
     * 关闭软键盘
     *
     * @param context context
     */
    public static void closeSoftInput(@Nullable Context context) {
        assert context != null;
        InputMethodManager inputMethodManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && ((Activity) context).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    ((Activity) context).getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 获得时间，用于桌面插件
     * @param context context
     * @return time string
     */
    public static String parseTime(Context context) {
        Date date = new Date();
        boolean time12 = isTimeFormat12(context);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(time12 ? "hh:mm" : "HH:mm", Locale.CHINA);
        return simpleDateFormat.format(date);
    }

    // used in BigWeatherWidget
    public static String parseTime(int minute) {
        Date date = new Date();
        date.setTime(System.currentTimeMillis() + minute * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm", Locale.CHINA);
        return simpleDateFormat.format(date);
    }

    public static String am_pm(String time) {
        int hour = Integer.parseInt(time);
        return (hour < 12) ? (hour + "am") : ((hour - 12) + "pm");
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager alm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return alm != null && alm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 检查网络连接是否可用
     *
     * @param context context
     * @return boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
//                if (info.getState() == NetworkInfo.State.CONNECTED) {
                // 当前所连接的网络可用
//                    return true;
//                }
                return true;
            }
        }
        return false;
    }

    // 系统时间是否为12小时制
    private static boolean isTimeFormat12(Context context) {
        return !android.text.format.DateFormat.is24HourFormat(context);
    }

    /**
     * 现在是星期几？
     *
     * @return int
     */
    public static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static long getHeWeatherUpdateTime(NewHeWeatherNow weather) {
        NewHeWeatherNow.HeWeather5Bean.BasicBean basicBean = weather.getHeWeather5().get(0).getBasic();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        try {
            Date date = simpleDateFormat.parse(basicBean.getUpdate().getLoc());
            return date.getTime();
        } catch (ParseException e) {
            Log.e(TAG, "getHeWeatherUpdateTime: parse failed" + basicBean.getUpdate().getLoc());
            return 0;
        }
    }

    public static void closeIO(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.e(TAG, "close error");
                e.printStackTrace();
            }
        }
    }

    public static boolean isToday(long timeInMills) {
        Calendar now = new GregorianCalendar();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        now.setTimeInMillis(timeInMills);
        return now.get(Calendar.DAY_OF_MONTH) == day
                && now.get(Calendar.MONTH) == month
                && now.get(Calendar.YEAR) == year;
    }

}
