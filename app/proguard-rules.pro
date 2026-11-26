# Upstream youtubedl-android / ffmpeg keep rules. Harmless before the engine
# is added, required once those dependencies are linked. Re-verify in Phase 6
# against the final dependency set.
-keep class com.yausername.** { *; }
-keep class org.apache.commons.compress.archivers.zip.** { *; }

# yt-dlp JSON is mapped through Jackson reflection.
-keepattributes *Annotation*, Signature
-keep class com.fasterxml.jackson.** { *; }

# Jackson probes optional JVM/desktop APIs that Android does not ship.
-dontwarn java.beans.ConstructorProperties
-dontwarn java.beans.Transient
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
