package com.rzt.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;

/**
 * Created by Administrator on 2016/1/25.
 */
public class DESUtils {
    private static Key key;
    private static String KEY_STR = "lizezhou828";// 密钥
    private static String CHARSETNAME = "UTF-8";// 编码
    private static String ALGORITHM = "DES";// 加密类型

    static {
        try {
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            SecureRandom random= SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(KEY_STR.getBytes());
            generator.init(random);
            key = generator.generateKey();
            generator = null;
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    /**
     * 对str进行DES加密
     *
     * @param str
     * @return
     */
    public static String getEncryptString(String str) {
        BASE64Encoder base64encoder = new BASE64Encoder();
        try {
            byte[] bytes = str.getBytes(CHARSETNAME);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] doFinal = cipher.doFinal(bytes);
            return base64encoder.encode(doFinal);
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }



    /**
     * 对str进行DES解密
     *
     * @param str
     * @return
     */
    public static String getDecryptString(String str) {
        BASE64Decoder base64decoder = new BASE64Decoder();
        try {
            byte[] bytes = base64decoder.decodeBuffer(str);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] doFinal = cipher.doFinal(bytes);
            return new String(doFinal, CHARSETNAME);
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }

public static void main(String[] args) {

    System.out.println(getEncryptString("select td_org,replace(td_org_name,'公司','') td_org_name,score from index_pm order by to_number(score) desc"));
    System.out.println(getEncryptString("sdtd"));
//	System.out.println(getDecryptString("MyuOSAj6tbjyR9+19rwt9TOGiXrVrvM9ztCxeXkrdxS487+GWznJeQ=="));
//    System.out.println(getEncryptString("wx"));
//    System.out.println(getEncryptString("wxjsepc1!"));
}
}
