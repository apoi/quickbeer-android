-verbose
-dontobfuscate
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Glide
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl

# AndroidSVG
-dontwarn com.caverock.androidsvg.**

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# OkHttp
-dontwarn okhttp3.**

# Renderscript
-keep class android.support.v8.renderscript.** { *; }

# Remove logging, except for errors
-assumenosideeffects class timber.log.Timber {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
}
-assumenosideeffects class io.reark.reark.utils.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
}
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
}
