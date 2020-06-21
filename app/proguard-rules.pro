
#firebase
    -keepattributes Signature
    -keepclassmembers class com.gmail.maystruks08.data.remote.pojo.** { *; }

    -keepclassmembers class com.gmail.maystruks08.domain.entities.** { *; }

    # For using GSON @Expose annotation
    -keepattributes *Annotation*

    # Gson specific classes
    -keep class sun.misc.Unsafe { *; }
    -keep class com.google.gson.stream.** { *; }

    # Prevent proguard from stripping interface information from TypeAdapterFactory,
    # JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
    -keep class * implements com.google.gson.TypeAdapterFactory
    -keep class * implements com.google.gson.JsonSerializer
    -keep class * implements com.google.gson.JsonDeserializer
