# AndroidLameEncodeMp3Demo

 一、什么是Lame
       我们看下Lame官网（https://lame.sourceforge.io/index.php）给的描述

             LAME is a high quality MPEG Audio Layer III (MP3) encoder licensed under the LGPL.

       翻译成中文就是：LAME是一个高质量的MPEG音频层III (MP3)编码器，在LGPL下授权。

       好了，现在我们知道Lame就是个开源的编码器，而且是专门用来编码mp3文件的。



二、为什么要采用Lame
      在Android上开发过录音功能的同学应该知道，在Android上要实现录音功能，有2种方式。一种是采用AudioRecord的方式，AudioRecord录制的直接是原始的pcm数据，另一种方式就是采用MediaRecord，可以录制类似amr、aac格式的文件。

     这里贴下MediaRecord目前支持的录音格式，可以看到目前是不支持mp3的。那这时我们想要编码mp3的文件，就得借助lame来实现了。

    public final class AudioEncoder {
      /* Do not change these values without updating their counterparts
       * in include/media/mediarecorder.h!
       */
        private AudioEncoder() {}
        public static final int DEFAULT = 0;
        /** AMR (Narrowband) audio codec */
        public static final int AMR_NB = 1;
        /** AMR (Wideband) audio codec */
        public static final int AMR_WB = 2;
        /** AAC Low Complexity (AAC-LC) audio codec */
        public static final int AAC = 3;
        /** High Efficiency AAC (HE-AAC) audio codec */
        public static final int HE_AAC = 4;
        /** Enhanced Low Delay AAC (AAC-ELD) audio codec */
        public static final int AAC_ELD = 5;
        /** Ogg Vorbis audio codec */
        public static final int VORBIS = 6;
        /** @hide EVRC audio codec */
        public static final int EVRC = 10;
        /** @hide QCELP audio codec */
        public static final int QCELP = 11;
        /** @hide Linear PCM audio codec */
        public static final int LPCM = 12;
    }



三、Lame在Android应用上如何使用
     
     在android上集成开源的代码，很多时候套路都差不多，就是下载源码，编译出相关的so库，然后就是调用这些库提供的方法了，当然，调用so库里面的方法，应用上需要借助jni来实现。

     这里就不介绍如何下载lame源码和编码so库了。编译lame源码后，我们会得到一个libmp3lame.so 的库文件，调用这个so库里面的方法，还需要我们去实现JNI部分的代码。

     整体的思路就是，应用层通过调用JNI提供的native方法，然后调用到 libmp3lame.so 库里面的方法，实现编码mp3文件。

     这里给出的一个例子是将pcm格式的文件编码mp3格式的文件。

     代码git地址：https://github.com/yorkZJC/AndroidLameEncodeMp3Demo.git
      

     代码逻辑这里简单讲下：

         LameEncodeJniNative.java 是本地封装的native方法，为应用层提供了2个方法，一个是编码，一个是结束。这2个方法对应c++层具体的实现，可以看工程 cpp/目录下文件的实现，cpp文件里面的实现也比较简单。

     应用代码调用的时候，就是传入对应的pcm源文件和需要生成的mp3文件路径，还有pcm源文件的格式信息（采用率、通道数、编码数据位数）。

      File pcmFile = new File(getExternalFilesDir(null), "input.pcm");
      File mp3File = new File(getExternalFilesDir(null), "output.mp3");
      mLameEncoder = new LameEncodeJniNative();
      mLameEncoder.encode(pcmFile.getAbsolutePath(), mp3File.getAbsolutePath(), 44100, 2, 128);

