package com.angel.black.baskettogether;

import com.angel.black.baskettogether.util.CipherUtil;
import com.angel.black.baskettogether.util.MyLog;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by KimJeongHun on 2016-05-25.
 */
public class CipherUnitTest {
    @Test
    public void generateKey() {
        String key = CipherUtil.generateKey();
        MyLog.e("cipher key=" + key);
        assertTrue(key, key.length() <= 30);

    }
}
