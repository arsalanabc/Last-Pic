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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Add this global rule
    -keepattributes Signature
    -keepresources string/default_web_client_id
    -keepresources string/firebase_database_url
    -keepresources string/gcm_defaultSenderId
    -keepresources string/google_api_key
    -keepresources string/google_app_id
    -keepresources string/google_crash_reporting_api_key
    -keepresources string/google_storage_bucket

# This rule will properly ProGuard all the model classes in
    # the package com.yourcompany.models. Modify to fit the structure
    # of your app.
    -keepclassmembers class com.skeedo.lastpic.Model.** {
      *;
    }
