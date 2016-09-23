package com.angel.black.baframework.util;

import com.angel.black.baframework.logger.BaLog;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by KimJeongHun on 2016-09-23.
 */
public class JSONUtil {

    public static void debug(JSONObject jsonObject) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> keyIter = jsonObject.keys();

        while(keyIter.hasNext()) {
            String key = keyIter.next();

            sb.append("key=" + key + ", value=" + jsonObject.optString(key));
            sb.append("\n");
        }

        BaLog.d("JSONObject debug >> \n" + sb.toString());
    }
}
