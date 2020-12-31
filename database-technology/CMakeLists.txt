cmake_minimum_required(VERSION 3.10.0)
project(database-project)

add_subdirectory(pugixml)

set(INCLUDE_DIRS include)
set(SOURCES src/Point.cpp src/Block.cpp src/Database.cpp src/xml/OsmImporter.cpp src/Benchmarks.cpp src/index/Index.cpp
        src/index/Leaf.cpp src/index/Rectangle.cpp src/index/NonLeaf.cpp)

add_executable(db-project main.cpp ${SOURCES})
target_include_directories(db-project PRIVATE ${INCLUDE_DIRS})
target_compile_features(db-project PRIVATE cxx_std_17)
target_link_libraries(db-project PRIVATE pugixml)