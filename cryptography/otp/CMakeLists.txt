cmake_minimum_required(VERSION 3.17)
project(otp)

set(CMAKE_CXX_STANDARD 17)

include_directories(include)

add_executable(otp main.cpp src/Processing.cpp)
target_link_libraries(otp crypto_shared)
