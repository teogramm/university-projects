cmake_minimum_required(VERSION 2.8)
project(domes-2019)

set(CMAKE_CXX_STANDARD 14)

include_directories(include)
add_executable(domes-2019 main.cpp include/Stack.h include/List.h src/AVLTree.cpp src/HashTable.cpp src/Minh.cpp src/MaxHeap.cpp src/Graph.cpp)