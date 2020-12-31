project(crypto_shared)

set(SOURCES src/Util.cpp src/UserInputOutput.cpp)

add_library(${PROJECT_NAME} ${SOURCES})

target_include_directories(${PROJECT_NAME} PUBLIC ${PROJECT_SOURCE_DIR}/include)
target_compile_features(${PROJECT_NAME} PRIVATE cxx_std_17)

install(TARGETS ${PROJECT_NAME} LIBRARY DESTINATION lib)
