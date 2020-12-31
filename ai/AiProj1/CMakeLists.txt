cmake_minimum_required(VERSION 3.15)
project(AiProj1)

set(CMAKE_CXX_STANDARD 17)

set(CMAKE_BINARY_DIR ${CMAKE_SOURCE_DIR}/bin)
set(EXECUTABLE_OUTPUT_PATH ${CMAKE_BINARY_DIR})
set(LIBRARY_OUTPUT_PATH ${CMAKE_BINARY_DIR})
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread")

find_package(Boost 1.72.0 REQUIRED filesystem system)

include_directories(include)
include_directories(${Boost_INCLUDE_DIR})
file(GLOB SOURCES "src/*.cpp")

add_executable(AiProj1 main.cpp ${SOURCES})
target_link_libraries(AiProj1 ${Boost_LIBRARIES})