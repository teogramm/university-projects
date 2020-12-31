cmake_minimum_required(VERSION 3.15)

set(CMAKE_BUILD_TYPE Release)

# --------- Google Test Setup ------------
# https://github.com/google/googletest/blob/fb98f7447e5d2e498d6458dcd5c888fdeaae82bc/googletest/README.md
# Download and unpack googletest at configure time
configure_file(CMakeLists.txt.in googletest-download/CMakeLists.txt)
execute_process(COMMAND ${CMAKE_COMMAND} -G "${CMAKE_GENERATOR}" .
        RESULT_VARIABLE result
        WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/googletest-download )
if(result)
    message(FATAL_ERROR "CMake step for googletest failed: ${result}")
endif()
execute_process(COMMAND ${CMAKE_COMMAND} --build .
        RESULT_VARIABLE result
        WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/googletest-download )
if(result)
    message(FATAL_ERROR "Build step for googletest failed: ${result}")
endif()

# Prevent overriding the parent project's compiler/linker
# settings on Windows
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)

# Add googletest directly to our build. This defines
# the gtest and gtest_main targets.
add_subdirectory(${CMAKE_CURRENT_BINARY_DIR}/googletest-src
        ${CMAKE_CURRENT_BINARY_DIR}/googletest-build
        EXCLUDE_FROM_ALL)

# Add GoogleTest CMake module
include(GoogleTest)
# ---------------------------------------------------------

project(SS1)

set(CMAKE_CXX_STANDARD 17)

# -------------------- libsndfile -------------------------


set(BUILD_PROGRAMS OFF CACHE BOOL "" FORCE)
set(BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(BUILD_TESTING OFF CACHE BOOL "" FORCE)
set(ENABLE_EXTERNAL_LIBS OFF CACHE BOOL "" FORCE)

# Add libsndfile directly to our build. This defines
# the gtest and gtest_main targets.
add_subdirectory(libsndfile EXCLUDE_FROM_ALL)

# ---------------------------------------------------------

find_package(Threads REQUIRED)
#find_package(Sndfile SndFile::sndfile REQUIRED)

include_directories(include)
file(GLOB SOURCES "src/*.cpp")

add_subdirectory(Helper)

# Enable flags below in case of static linking
#set(CMAKE_CXX_FLAGS "-Wl,--whole-archive -lpthread -Wl,--no-whole-archive")

add_executable(SS1 main.cpp ${SOURCES})
target_link_libraries(SS1
        Helper Threads::Threads SndFile::sndfile tbb)

add_executable(tests test/Convolution.cpp)
target_link_libraries(tests gtest_main gmock_main Helper tbb)
gtest_discover_tests(tests)
