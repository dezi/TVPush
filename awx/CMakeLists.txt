# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             dummy-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/dummy-lib.cpp )

add_library(libTelinkCrypto SHARED IMPORTED)

set_target_properties(libTelinkCrypto PROPERTIES IMPORTED_LOCATION
${CMAKE_SOURCE_DIR}/src/main/jnilibs/${ANDROID_ABI}/libTelinkCrypto.so)

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( # Specifies the target library.
                       dummy-lib
                       libTelinkCrypto
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
