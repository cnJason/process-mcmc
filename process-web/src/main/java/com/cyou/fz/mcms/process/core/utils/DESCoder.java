package com.cyou.fz.mcms.process.core.utils;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DESCoder extends Coder {
    public DESCoder() {
    }

    public static String encryptDES(String encryptString, String encryptKey) throws Exception {
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(1, key);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
        return encryptBASE64(encryptedData);
    }

    public static String decryptDES(String decryptString, String decryptKey) throws Exception {
        byte[] byteMi = decryptBASE64(decryptString);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(2, key);
        byte[] decryptedData = cipher.doFinal(byteMi);
        return new String(decryptedData);
    }
}