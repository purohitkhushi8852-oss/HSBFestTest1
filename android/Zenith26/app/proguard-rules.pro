# Proguard rules for Zenith '26
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep class com.zenith26.fest.data.model.** { *; }
-keep class com.zenith26.fest.data.api.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
