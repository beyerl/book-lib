# Keep kotlinx.serialization generated serializers
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class com.lspace.booklib.data.remote.dto.** {
    *** Companion;
}
-keep class com.lspace.booklib.data.remote.dto.** { *; }
