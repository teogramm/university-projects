cmake_minimum_required(VERSION 3.15)
project(AiProj1)

set(CMAKE_CXX_STANDARD 17)

set(CMAKE_BINARY_DIR ${CMAKE_SOURCE_DIR}/bin)
set(EXECUTABLE_OUTPUT_PATH ${CMAKE_BINARY_DIR})
set(LIBRARY_OUTPUT_PATH ${CMAKE_BINARY_DIR})

include_directories(include)
file(GLOB SOURCES "src/*.cpp")

add_executable(digital-comm main.cpp ${SOURCES})

find_package (Threads REQUIRED)

target_link_libraries(digital-comm
        ${CMAKE_THREAD_LIBS_INIT}
        )