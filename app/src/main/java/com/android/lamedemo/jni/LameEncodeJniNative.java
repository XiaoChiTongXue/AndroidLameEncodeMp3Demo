package com.android.lamedemo.jni;

public class LameEncodeJniNative {
    static {
        System.loadLibrary("lame-encode");
    }

    public native void encode(String pcmPath, String mp3Path, int sampleRate, int channels, int bitRate);

    public native void destroy();

}
