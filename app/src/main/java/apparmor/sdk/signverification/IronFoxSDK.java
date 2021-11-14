package apparmor.sdk.signverification;

import android.util.Log;

import com.scottyab.aescrypt.AESCrypt;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class IronFoxSDK implements Interceptor {

    private String clietAuthKey;
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";

    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static SecureRandom random = new SecureRandom();


    public IronFoxSDK(String clientKey) {
        clietAuthKey = clientKey;

    }

    static {
        System.loadLibrary("iron-lib");
    }
//    static {
//        System.loadLibrary("libkey1956");
//    }


    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
                .header("ironfoxhash", generateHeader());

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }

    public String getNativeHash() {
        byte[] rawCertNative = bytesFromJNI(clietAuthKey);
        return getNativeSignHash(rawCertNative);
    }


    //todo rewrite all of below methods inside native C or C++
    private String generateHeader() {
        byte[] rawCertNative = bytesFromJNI(clietAuthKey);
        Log.i("ironfox get hash ", getNativeSignHash(rawCertNative));

        // get ret (orginal ironfox header)
        String value = clietAuthKey + getNativeSignHash(rawCertNative) + generateRandomString(28);


//        Log.i("ironfox ret raw", value);
//        String password = getEncKey();
//        Log.i("ironfox read AES key", password);
//
//        String ret = null;
//        try {
//            ret = AESCrypt.encrypt(password, value);
//            Log.i("ironfox ret AES enc", ret);
//
//            String decret = AESCrypt.decrypt(password, ret);
//            Log.i("ironfox ret AES dec", decret);
//
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        }


       // return ret;
        return  value;

    }


    private String getNativeSignHash(byte[] bytes) {
        if (null == bytes) {
            return "null";
        }
        String resp = "";
        MessageDigest md;
        try {

            md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            byte[] byteArray = md.digest();
            md.reset();

            resp = bytesToString(byteArray);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return resp;
    }


    /**
     * @param bytes
     * @return
     */
    private String bytesToString(byte[] bytes) {
        StringBuilder md5StrBuff = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & bytes[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & bytes[i]));
            }

        }
        return md5StrBuff.toString();
    }


    /**
     * @param apikey
     * @return byet array
     */
    private native byte[] bytesFromJNI(String apikey);
    private native String getEncKey();
 //   private native String getEncKey1();


    /**
     * @param length
     * @return
     */
    public static String generateRandomString(int length) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            // debug
            System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);

            sb.append(rndChar);

        }

        return sb.toString();

    }

}
