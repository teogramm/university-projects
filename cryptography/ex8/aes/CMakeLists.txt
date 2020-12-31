cmake_minimum_required(VERSION 3.10)
project(aes)
file(GLOB INCLUDES *.h)
file(GLOB SOURCES ${PROJECT_SOURCE_DIR}/*.c)
 
add_library(aes aescrypt.c aeskey.c aestab.c)
target_include_directories(aes PUBLIC ${PROJECT_SOURCE_DIR})
