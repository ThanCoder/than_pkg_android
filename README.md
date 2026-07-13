# than_pkg_android

## Replace -> `than_pkg` Package



## Android Setup ⚠️ (Required for Camera Feature)

To use the camera feature, you **MUST** configure a `FileProvider` in your Android project to avoid crashes on Android 7.0+.

### Step 1: Update `AndroidManifest.xml`
Add this inside the `<application>` tag of your `android/app/src/main/AndroidManifest.xml`:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.file_provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>