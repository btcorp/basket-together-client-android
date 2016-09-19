package com.angel.black.baframework.logger;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.angel.black.baframework.BaApplication;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Created by KimJeongHun on 2016-05-22.
 */
public class BaLog {
    public static final String LOG_TAG = BaLog.class.getSimpleName();

    public static final void e(String message) {
        if (BaApplication.debug)
            Log.e(LOG_TAG, buildLogMsg(message));
    }

    public static final void e(String tag, String message) {
        if (BaApplication.debug)
            Log.e(tag, buildLogMsg(message));
    }

    public static final void w(String message) {
        if (BaApplication.debug)
            Log.w(LOG_TAG, buildLogMsg(message));
    }

    public static final void w(String tag, String message) {
        if (BaApplication.debug)
            Log.w(tag, buildLogMsg(message));
    }

    public static void i() {
        if (BaApplication.debug)
            Log.i(LOG_TAG, buildLogMsg(""));
    }

    public static final void i(String message) {
        if (BaApplication.debug)
            Log.i(LOG_TAG, buildLogMsg(message));
    }

    public static final void i(String tag, String message) {
        if (BaApplication.debug)
            Log.i(tag, buildLogMsg(message));
    }

    public static final void d() {
        if (BaApplication.debug)
            Log.d(LOG_TAG, buildLogMsg(""));
    }

    public static final void d(Object... object) {
        if (BaApplication.debug)
            Log.d(LOG_TAG, buildLogMsg(_MESSAGE(object)));
    }

    public static final void d(String message) {
        if (BaApplication.debug)
            Log.d(LOG_TAG, buildLogMsg(message));
    }

    public static final void d(String tag, String message) {
        if (BaApplication.debug)
            Log.d(tag, buildLogMsg(message));
    }

    public static final void v(String message) {
        if (BaApplication.debug)
            Log.v(LOG_TAG, buildLogMsg(message));
    }

    public static final void v(String tag, String message) {
        if (BaApplication.debug)
            Log.v(tag, buildLogMsg(message));
    }

    public static String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("] ");
//        sb.append(String.format(":at (%s:%d)", ste.getFileName(), ste.getLineNumber()));
        sb.append(message);

        return sb.toString();
    }

    public static String _MESSAGE(Object... args) {
        if (args == null)
            return "null[]";

        StringBuilder sb = new StringBuilder();
        for (Object object : args) {
            //@formatter:off
            if (object == null)                       sb.append("null");
            else if (object instanceof Class)         sb.append(DUMP((Class<?>) object));
            else if (object instanceof Cursor)        sb.append(DUMP((Cursor) object));
            else if (object instanceof View)          sb.append(DUMP((View) object));
            else if (object instanceof Intent)        sb.append(DUMP((Intent) object));
            else if (object instanceof Bundle)        sb.append(DUMP((Bundle) object));
            else if (object instanceof ContentValues) sb.append(DUMP((ContentValues) object));
            else if (object instanceof Throwable)     sb.append(DUMP((Throwable) object));
            else if (object instanceof Uri)           sb.append(DUMP((Uri) object));
            else if (object instanceof Method)        sb.append(DUMP((Method) object));
            else if (object.getClass().isArray())     sb.append(DUMP_array(object));
            else                                      sb.append(object.toString());
            //@formatter:on
            sb.append(",");
        }
        return sb.toString();
    }

    public static String DUMP(Method method) {
        StringBuilder result = new StringBuilder(Modifier.toString(method.getModifiers()));
        if (result.length() != 0) {
            result.append(' ');
        }
        result.append(method.getReturnType().getSimpleName());
        result.append("                           ");
        result.setLength(20);
        result.append(method.getDeclaringClass().getSimpleName());
        result.append('.');
        result.append(method.getName());
        result.append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameterType : parameterTypes) {
            result.append(parameterType.getSimpleName());
            result.append(',');
        }
        if (parameterTypes.length > 0)
            result.setLength(result.length() - 1);
        result.append(")");

        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (exceptionTypes.length != 0) {
            result.append(" throws ");
            for (Class<?> exceptionType : exceptionTypes) {
                result.append(exceptionType.getSimpleName());
                result.append(',');
            }
            if (exceptionTypes.length > 0)
                result.setLength(result.length() - 1);
        }
        return result.toString();
    }

    private static void appendViewInfo(StringBuilder out, View v) {
        final int id = v.getId();
        if (id != View.NO_ID) {
            // out.append(String.format(" #%08x", id));
            final Resources r = v.getResources();
            if (((id >>> 24) != 0) && r != null) {
                try {
                    String pkgname;
                    switch (id & 0xff000000) {
                        case 0x7f000000 :
                            pkgname = "app";
                            break;
                        case 0x01000000 :
                            pkgname = "android";
                            break;
                        default :
                            pkgname = r.getResourcePackageName(id);
                            break;
                    }
                    String typename = r.getResourceTypeName(id);
                    String entryname = r.getResourceEntryName(id);
                    out.append(" ");
                    out.append(pkgname);
                    out.append(":");
                    out.append(typename);
                    out.append("/");
                    out.append(entryname);
                } catch (Resources.NotFoundException e) {
                }
            }
        }
    }

    public static String DUMP(Throwable th) {
        String message = "Throwable";
        try {
            Throwable cause = th;
            while (cause != null) {
                message = cause.getClass().getSimpleName() + "," + cause.getMessage();
                cause = cause.getCause();
            }
        } catch (Exception e) {
        }
        return message;
    }

    private static String DUMP(View v, int... depths) {

        final int depth = depths.length > 0 ? depths[0] : 0;
        StringBuilder out = new StringBuilder(128);
        out.append("                    ");
        if (v instanceof WebView)
            out.insert(depth, "W:" + ((WebView) v).getTitle());
        else if (v instanceof TextView)
            out.insert(depth, "T:" + ((TextView) v).getText());
        else
            out.insert(depth, "N:" + v.getClass().getSimpleName());
        out.setLength("                    ".length());
        appendViewInfo(out, v);
        return out.toString();
    }

    private static String DUMP(Cursor c) {
        if (c == null)
            return "null_Cursor";

        StringBuilder sb = new StringBuilder();
        int count = c.getCount();
        sb.append("<" + count + ">");

        try {
            String[] columns = c.getColumnNames();
            sb.append(Arrays.toString(columns));
            sb.append("\n");
        } catch (Exception e) {
        }

        int countColumns = c.getColumnCount();
        if (!c.isBeforeFirst()) {
            for (int i = 0; i < countColumns; i++) {
                try {
                    sb.append(c.getString(i) + ",");
                } catch (Exception e) {
                    sb.append("BLOB,");
                }
            }
        } else {
            int org_pos = c.getPosition();
            while (c.moveToNext()) {
                for (int i = 0; i < countColumns; i++) {
                    try {
                        sb.append(c.getString(i) + ",");
                    } catch (Exception e) {
                        sb.append("BLOB,");
                    }
                }
                sb.append("\n");
            }
            c.moveToPosition(org_pos);
        }
        return sb.toString();
    }

    private static String DUMP(ContentValues values) {
        if (values == null)
            return "null_ContentValues";

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> etry : values.valueSet()) {
            String key = etry.getKey();
            String value = etry.getValue().toString();
            String type = etry.getValue().getClass().getSimpleName();
            sb.append(key + "," + type + "," + value).append("\n");
        }

        return sb.toString();
    }

    private static String DUMP(Bundle bundle) {
        if (bundle == null)
            return "null_Bundle";

        StringBuffer sb = new StringBuffer();
        final Set<String> keys = bundle.keySet();

        for (String key : keys) {
            final Object o = bundle.get(key);
            if (o == null) {
                sb.append(key + ",null,null");
            } else if (o.getClass().isArray()) {
                sb.append(key + "," + o.getClass().getSimpleName() + "," + DUMP_array(o));
            } else {
                sb.append(key + "," + o.getClass().getSimpleName() + "," + o.toString());
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private static String DUMP_array(Object o) {

        //@formatter:off
        if (o == null)
            return "null";

        if (!o.getClass().isArray())
            return "";

        Class<?> elemElemClass = o.getClass().getComponentType();
        if (elemElemClass.isPrimitive()) {
            if      (boolean.class.equals(elemElemClass)) return Arrays.toString((boolean[]) o);
            else if (char   .class.equals(elemElemClass)) return Arrays.toString((char   []) o);
            else if (double .class.equals(elemElemClass)) return Arrays.toString((double []) o);
            else if (float  .class.equals(elemElemClass)) return Arrays.toString((float  []) o);
            else if (int    .class.equals(elemElemClass)) return Arrays.toString((int    []) o);
            else if (long   .class.equals(elemElemClass)) return Arrays.toString((long   []) o);
            else if (short  .class.equals(elemElemClass)) return Arrays.toString((short  []) o);
            else if (byte   .class.equals(elemElemClass)) return           DUMP((byte   []) o);
            else throw new AssertionError();
        } else
            return Arrays.toString((Object[]) o);
        //@formatter:on

    }
    private static String DUMP(Class<?> cls) {
        if (cls == null)
            return "null_Class<?>";
        return cls.getSimpleName();
//		return cls.getSimpleName() + ((cls.getSuperclass() != null) ? (">>" + cls.getSuperclass().getSimpleName()) : "");
    }

    private static String DUMP(Uri uri) {
        if (uri == null)
            return "null_Uri";

//		return uri.toString();
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n Uri                       ").append(uri.toString());
        sb.append("\r\n Scheme                    ").append(uri.getScheme() != null ? uri.getScheme().toString() : "null");
        sb.append("\r\n Host                      ").append(uri.getHost() != null ? uri.getHost().toString() : "null");
        sb.append("\r\n Port                      ").append(uri.getPort());
        sb.append("\r\n Path                      ").append(uri.getPath() != null ? uri.getPath().toString() : "null");
        sb.append("\r\n Query                     ").append(uri.getQuery() != null ? uri.getQuery().toString() : "null");
        sb.append("\r\n");
        sb.append("\r\n Fragment                  ").append(uri.getFragment() != null ? uri.getFragment().toString() : "null");
        sb.append("\r\n LastPathSegment           ").append(uri.getLastPathSegment() != null ? uri.getLastPathSegment().toString() : "null");
        sb.append("\r\n SchemeSpecificPart        ").append(uri.getSchemeSpecificPart() != null ? uri.getSchemeSpecificPart().toString() : "null");
        sb.append("\r\n UserInfo                  ").append(uri.getUserInfo() != null ? uri.getUserInfo().toString() : "null");
        sb.append("\r\n PathSegments              ").append(uri.getPathSegments() != null ? uri.getPathSegments().toString() : "null");
        sb.append("\r\n Authority                 ").append(uri.getAuthority() != null ? uri.getAuthority().toString() : "null");
        sb.append("\r\n");
        sb.append("\r\n EncodedAuthority          ").append(uri.getEncodedAuthority() != null ? uri.getEncodedAuthority().toString() : "null");
        sb.append("\r\n EncodedPath               ").append(uri.getEncodedPath() != null ? uri.getEncodedPath().toString() : "null");
        sb.append("\r\n EncodedQuery              ").append(uri.getEncodedQuery() != null ? uri.getEncodedQuery().toString() : "null");
        sb.append("\r\n EncodedFragment           ").append(uri.getEncodedFragment() != null ? uri.getEncodedFragment().toString() : "null");
        sb.append("\r\n EncodedSchemeSpecificPart ").append(uri.getEncodedSchemeSpecificPart() != null ? uri.getEncodedSchemeSpecificPart().toString() : "null");
        sb.append("\r\n EncodedUserInfo           ").append(uri.getEncodedUserInfo() != null ? uri.getEncodedUserInfo().toString() : "null");
        sb.append("\r\n");
        return sb.toString();
    }

    public static String DUMP(Intent intent) {
        if (intent == null)
            return "null_Intent";
        StringBuffer sb = new StringBuffer();
        //@formatter:off
        sb.append(intent.getAction    () != null ? "Action     " + intent.getAction    ().toString() + "\n" : "");
        sb.append(intent.getData      () != null ? "Data       " + intent.getData      ().toString() + "\n" : "");
        sb.append(intent.getCategories() != null ? "Categories " + intent.getCategories().toString() + "\n" : "");
        sb.append(intent.getType      () != null ? "Type       " + intent.getType      ().toString() + "\n" : "");
        sb.append(intent.getScheme    () != null ? "Scheme     " + intent.getScheme    ().toString() + "\n" : "");
        sb.append(intent.getPackage   () != null ? "Package    " + intent.getPackage   ().toString() + "\n" : "");
        sb.append(intent.getComponent () != null ? "Component  " + intent.getComponent ().toString() + "\n" : "");
        sb.append(intent.getFlags()      != 0x00 ? "Flags      " + Integer.toHexString(intent.getFlags()) + "\n" : "");
        //@formatter:on

        if (intent.getExtras() != null)
            sb.append(DUMP(intent.getExtras()));

        return sb.toString();
    }

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private static String DUMP(byte[] bytearray) {
        if (bytearray == null)
            return "null_bytearray";
        try {
            char[] chars = new char[2 * bytearray.length];
            for (int i = 0; i < bytearray.length; ++i) {
                chars[2 * i] = HEX_CHARS[(bytearray[i] & 0xF0) >>> 4];
                chars[2 * i + 1] = HEX_CHARS[bytearray[i] & 0x0F];
            }
            return new String(chars);
        } catch (Exception e) {
            return "!!byte[]";
        }
    }
}
