package com.ys.guardpeopledaily;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/9/29.
 */

public class Utils {

    // String packageBoot = Utils.getValueFromProp("persist.sys.packageboot");
    public static String getValueFromProp(String key) {
        String value = "";
        try {
            Class classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class[]{String.class});
            value = (String)getMethod.invoke(classType, new Object[]{key});
        } catch (Exception var4) {
            ;
        }

        return value;
    }

    //Utils.setValueToProp("persist.sys.touchstatus","1");
    public static void setValueToProp(String key, String val) {
        try {
            Class classType = Class.forName("android.os.SystemProperties");
            Method e = classType.getDeclaredMethod("set", new Class[]{String.class, String.class});
            e.invoke(classType, new Object[]{key, val});
        } catch (ClassNotFoundException var4) {
            var4.printStackTrace();
        } catch (NoSuchMethodException var5) {
            var5.printStackTrace();
        } catch (InvocationTargetException var6) {
            var6.printStackTrace();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }
}
