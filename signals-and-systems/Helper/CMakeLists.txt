project(Helper)

set(CMAKE_CXX_STANDARD 17)

include_directories()
add_library(${PROJECT_NAME} STATIC
        src/UI.cpp)

target_include_directories(${PROJECT_NAME}
        PUBLIC
            ${PROJECT_SOURCE_DIR}/include)