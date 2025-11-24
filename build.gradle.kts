// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id ("com.google.dagger.hilt.android") version "2.51" apply false
    kotlin ("plugin.serialization") version "1.9.23" apply false
    kotlin("jvm") version "1.9.23" apply false
}
buildscript {
    dependencies {
        classpath ("com.android.tools.build:gradle:8.2.0")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
}