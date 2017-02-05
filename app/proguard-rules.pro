-verbose
-dontobfuscate
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Optimization step doesn't update things correctly
-optimizations !code/allocation/variable

# Okio
-dontwarn okio.**
-dontwarn org.apache.http.**
-dontwarn com.squareup.okhttp.internal.huc.**
-dontwarn com.google.appengine.api.urlfetch.**
-dontwarn android.net.http.AndroidHttpClient

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson
-keep public class com.google.gson.** { *; }

# RxJava
-dontwarn sun.misc.Unsafe
-dontwarn sun.misc.**
-keep class rx.internal.util.unsafe.** { *; }
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Picasso
-dontwarn com.squareup.okhttp.**

# Retrolambda
-dontwarn java.lang.invoke.*

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# AutoValue
-dontwarn com.google.auto.**
-dontwarn autovalue.shaded.com.**
-dontwarn sun.misc.Unsafe
-dontwarn javax.lang.model.element.Modifier

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}

# Remove logging, except for errors
-assumenosideeffects class timber.log.Timber {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
}
-assumenosideeffects class io.reark.reark.utils.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
}
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
}
