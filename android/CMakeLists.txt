cmake_minimum_required(VERSION 3.10.2)

project(than_pkg_android)

# C++ ဖိုင်ကို Shared Library (.so) အဖြစ် သတ်မှတ်ခြင်း
add_library(than_pkg_android SHARED
    src/main/cpp/than_pkg_android_plugin.cpp)

# Android ရဲ့ Graphic System (ANativeWindow) ကို သုံးနိုင်အောင် linkage လုပ်ခြင်း
find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(than_pkg_android
    ${log-lib}
    ${android-lib})
