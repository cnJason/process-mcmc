package com.cyou.fz.mcms.process.core.utils;//


import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class SecurityUtil {
    private static final String KEY_SHA = "SHA";
    private static final String KEY_MD5 = "MD5";

    public SecurityUtil() {
    }

    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    public static String encryptBASE64(byte[] bts) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(bts);
    }

    public static String encryptMD5(String data) throws Exception {
        if(StringUtils.isBlank(data)) {
            return null;
        } else {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data.getBytes());
            byte[] s = md5.digest();
            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < s.length; ++i) {
                sb.append(Integer.toHexString(255 & s[i] | -256).substring(6));
            }

            return sb.toString();
        }
    }

    public static String encryptSHA(String data) throws Exception {
        if(StringUtils.isBlank(data)) {
            return null;
        } else {
            MessageDigest sha = MessageDigest.getInstance("SHA");
            sha.update(data.getBytes());
            return new String(sha.digest());
        }
    }

    public static String initMacKey(String type) throws Exception {
        if(StringUtils.isBlank(type)) {
            type = SecurityUtil.KEY_MAC.MD5.getValue();
        }

        KeyGenerator keyGenerator = KeyGenerator.getInstance(type);
        SecretKey secretKey = keyGenerator.generateKey();
        return encryptBASE64(secretKey.getEncoded());
    }

    public static String encryptHMAC(String data, String key, String type) throws Exception {
        if(StringUtils.isBlank(type)) {
            type = SecurityUtil.KEY_MAC.MD5.getValue();
        }

        SecretKeySpec secretKey = new SecretKeySpec(decryptBASE64(key), type);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return new String(mac.doFinal(data.getBytes()));
    }

    public static String desEncode(String encryptString, String encryptKey) throws Exception {
        int count = encryptString.getBytes().length % 8;
        if(count > 0) {
            for(int se = 0; se < 8 - count; ++se) {
                encryptString = encryptString.concat(" ");
            }
        }

        String var4 = DESCoder.encryptDES(encryptString, encryptKey);
        return var4;
    }

    public static String desDecode(String decryptString, String decryptKey) throws Exception {
        return DESCoder.decryptDES(decryptString, decryptKey) == null?null: DESCoder.decryptDES(decryptString, decryptKey).trim();
    }

    public static String rsaEncode(String str, String publicKey) throws Exception {
        byte[] data = str.getBytes();
        byte[] encodedData = RSACoder.encryptByPublicKey(data, publicKey);
        return RSACoder.encryptBASE64(encodedData);
    }

    public static String rsaDecode(String str, String privateKey) throws Exception {
        byte[] decodedData = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(str), privateKey);
        return new String(decodedData);
    }

    public static enum KEY_MAC {
        MD5("HmacMD5"),
        SHA1("HmacSHA1"),
        SHA256("HmacSHA256"),
        SHA384("HmacSHA384"),
        SHA512("HmacSHA512");

        private final String key;

        private KEY_MAC(String key) {
            this.key = key;
        }

        public String getValue() {
            return this.key;
        }
    }
}
