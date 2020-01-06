package com.android.lamedemo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.lamedemo.R;
import com.android.lamedemo.jni.LameEncodeJniNative;
import com.android.lamedemo.task.AssertReleaseTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

/**
 * @author york
 * @date 2020-01-06
 */
public class LameEncodeActivity extends Activity {
    private static String TAG = LameEncodeActivity.class.getSimpleName();
    private Button mBtnEncode;

    private LameEncodeJniNative mLameEncoder;

    private static final int REQUEST_PERMISSION = 1;
    private static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    private void requestPermission(String[] permissions) {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "no permission", Toast.LENGTH_SHORT).show();
                    finish();
                    System.exit(1);
                    return;
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission(PERMISSIONS);
        mBtnEncode = (Button) findViewById(R.id.btn_encode);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getPcmSource(){
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            Context appContext = getApplicationContext();
            if (appContext != null) {
                for (String fileName : new String[]{"input.pcm"}) {
                    inputStream = appContext.getAssets().open(fileName);
                    File targetFile = new File(appContext.getExternalFilesDir(null), fileName);
                    fos = new FileOutputStream(targetFile);
                    int length;
                    byte[] buffer = new byte[8 * 1024];
                    while ((length = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, length);
                        fos.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                fos.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    public void onEncodeClick(View view) {
        mBtnEncode.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.v(TAG,"---- start encode!!!"+getExternalFilesDir(null));
                getPcmSource();

                File pcmFile = new File(getExternalFilesDir(null), "input.pcm");
                File mp3File = new File(getExternalFilesDir(null), "output.mp3");
                mLameEncoder = new LameEncodeJniNative();
                mLameEncoder.encode(pcmFile.getAbsolutePath(), mp3File.getAbsolutePath(), 44100, 2, 128);

                Log.v(TAG,"----- after encode!!!");
            }
        }).start();
    }
}
