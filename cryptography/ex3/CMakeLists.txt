cmake_minimum_required(VERSION 3.17)
project(ex3)
set(CMAKE_CXX_STANDARD 17)

set(Boost_USE_STATIC_LIBS OFF)
set(Boost_USE_MULTITHREADED ON)
set(Boost_USE_STATIC_RUNTIME OFF)
find_package(Boost REQUIRED COMPONENTS regex)

add_executable(ex3 main.cpp)
target_link_libraries(ex3 PRIVATE Boost::regex)
