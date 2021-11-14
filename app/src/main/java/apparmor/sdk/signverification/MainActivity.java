package apparmor.sdk.signverification;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.kozhevin.signverification.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

// Sample demo, call ironfox sdk.

public class MainActivity extends AppCompatActivity {
    public String val;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // call API and get value

        String url = "http://95.80.128.101:8080/v1";


        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new IronFoxSDK("1268abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQR"));

        OkHttpClient client = builder.build();


        Request request = new Request.Builder()
                .url(url)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("ironfox response ", response.body().string());

                }
            }
        });








        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != info && info.signatures.length > 0) {
            byte[] rawCertJava = info.signatures[0].toByteArray();
            String str =getInfoFromBytes(rawCertJava);
            tv.setText(str);
        } else {
            tv.setText("No data");
        }



    }


    private String getInfoFromBytes(byte[] bytes) {
        if (null == bytes) {
            return "null";
        }

        /*
         * Get the X.509 certificate.
         */
        StringBuilder sb = new StringBuilder();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            Log.i("ironfox MainA byte ", String.valueOf(bytes));
            md.update(bytes);

            byte[] byteArray = md.digest();
            Log.i("ironfox MainA md.str ", String.valueOf(md.digest()));


            sb.append("Java   MD5: ").append(bytesToString(byteArray)).append("\n\n\n");
            IronFoxSDK ironFoxSDK = new IronFoxSDK("1268abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQR");
            sb.append("Native MD5: ").append(ironFoxSDK.getNativeHash()).append("\n");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        sb.append("\n");

        return sb.toString();
    }

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


}
