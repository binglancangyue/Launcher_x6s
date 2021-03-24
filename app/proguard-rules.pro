# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the transparent_dividing_line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the transparent_dividing_line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-dontnote **
-verbose
# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-ignorewarnings

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keepattributes EnclosingMethod
-keepclasseswithmembernames class * {
    native <methods>;}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;}
-keepattributes Signature
-keepattributes *Annotation*
# 保留R下面的资源
-keep class **.R$* {*;}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

-keep class com.cywl.launcher.model.MusicData {*;}
-keep class com.cywl.launcher.model.**{*; }
-libraryjars libs/kwmusic-autosdk-v1.9.7.jar


#----------------------第三方----------------------#

#喜马拉雅
-dontwarn okio.**
-keep class okio.** { *;}

-dontwarn okhttp3.**
-keep class okhttp3.** { *;}

-dontwarn com.google.gson.**
-keep class com.google.gson.** { *;}

-dontwarn android.support.**
-keep class android.support.** { *;}

-dontwarn com.ximalaya.ting.android.player.**
-keep class com.ximalaya.ting.android.player.** { *;}

-dontwarn com.ximalaya.ting.android.opensdk.**
-keep interface com.ximalaya.ting.android.opensdk.** {*;}
-keep class com.ximalaya.ting.android.opensdk.** { *; }


#Fastjson
-keepattributes Signature
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder


#Rxjava Rxandroid
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;}
-dontnote rx.internal.util.PlatformDependent

-keep  class com.cywl.launcher.model.ItemData

# EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

#腾讯网卡
-keep class sun.misc.Unsafe { *; }
-keep class com.tencent.deviceearn.wecarsdk.CrowdSourceApi{
    public *;}
-keep class com.tencent.deviceearn.wecarsdk.DeviceInfo{
    public *;}

#region 信鸽，实际上纯打包sdk的时候不会涉及到信鸽的混淆
#如果构建完整的app并开启混淆的话，那么就需要用到下面这几句了
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.** {* ;}
-keep class com.tencent.mid.** {* ;}
-keep class com.qq.taf.jce.** {*;}

# 下面这两句在 sdk 构建时有用
-keep class com.tencent.deviceearn.push.PushApi{
    public *;}
-keep class com.tencent.deviceearn.push.PushApi$OnMessageReceivedListener{
    public *;}
#endregion

#region logger，实际上纯打包sdk的时候不会涉及到logger的混淆
#如果构建完整的app并开启混淆的话，那么就需要用到下面这几句了
-keep class ch.qos.** { *; }
-keep class org.slf4j.** { *; }
-keepattributes *Annotation*
-dontwarn ch.qos.logback.core.net.*
#endregion

-keep class com.google.**{ *; }
-keep class com.tencent.halley.** { *; }
-keep class com.tencent.wecarbase.** { *; }
-keep class com.henrik.** { *; }
-keep class com.marswin89.** { *; }
-keep class device.** { *; }
-keep class com.tencent.beacon.** { *; }

-dontwarn com.tencent.halley.**

# 反射获取对应平台的 id 读取方法
-keep class com.tencent.deviceearn.wecarsdk.deviceid.DeviceIdReaderImpl
-dontwarn com.tencent.**
-dontwarn okhttp3.**
-dontwarn okio.**

#-keep class com.tencent.** { *; }
-keep class com.tencent.libdeviceearnbase.** { *; }
-keep class com.tencent.pluginbase.** { *; }
-keep class com.tencent.deviceearn.** { *; }
-keep class com.tencent.libdeviceearn.** { *; }
-keep class com.tencent.framesdk.** { *; }
-keep class com.tencent.oasis.** { *; }
-keep class com.tencent.libdeviceearn.** { *; }
-keep class com.tencent.common.** { *; }
-keep class com.tencent.trafficstats.** { *; }
-keep class com.tencent.dependency.** { *; }
-keep class com.tencent.plugincrowdsourcing.** { *; }
-keep class com.tencent.sha1utils.** { *; }
-keep class com.henrik.** { *; }
-keep class com.marswin89.** { *; }
-keep class ch.qos.logback.** { *; }

-keep class device.** { *; }

-keep class com.google.a.**{ *; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
