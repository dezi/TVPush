# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

add_executable(meme src/main/cpp/meme.c src/main/cpp/json.h src/main/cpp/json.c)
add_executable(plib src/main/cpp/plib.c)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp )

add_library(libzztopp2z SHARED IMPORTED)

set_target_properties(libzztopp2z PROPERTIES IMPORTED_LOCATION
${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libzztopp2z.so)

add_library(libzztopaac SHARED IMPORTED)

set_target_properties(libzztopaac PROPERTIES IMPORTED_LOCATION
${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libfaad.so)

add_library(libh265decoder SHARED IMPORTED)

set_target_properties(libh265decoder PROPERTIES IMPORTED_LOCATION
${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libh265decoder.so)

add_library(libijkffmpeg SHARED IMPORTED)

set_target_properties(libijkffmpeg PROPERTIES IMPORTED_LOCATION
${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libijkffmpeg.so)

add_library(libmi_decoder SHARED IMPORTED)

set_target_properties(libmi_decoder PROPERTIES IMPORTED_LOCATION
${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libmi_decoder.so)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib
                       libmi_decoder
                       libijkffmpeg
                       libh265decoder
                       libzztopp2z
                       libzztopaac
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )