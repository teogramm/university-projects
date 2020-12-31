cmake_minimum_required(VERSION 3.17)
project(rc4)

set(CMAKE_CXX_STANDARD 17)

include_directories(include)
set(sources src/RC4.cpp)

add_executable(rc4 main.cpp ${sources})
target_link_libraries(rc4 crypto_shared)