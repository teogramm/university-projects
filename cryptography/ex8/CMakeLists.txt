cmake_minimum_required(VERSION 3.10)
project(ex8)

add_subdirectory(aes)
add_executable(ex8 main.cpp)
target_link_libraries(ex8 PRIVATE aes)
target_link_libraries(ex8 PRIVATE crypto_shared)
