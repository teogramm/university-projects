cmake_minimum_required(VERSION 3.17)
project(ex2)

set(SOURCES src/util.cpp)

find_package(OpenSSL REQUIRED)

set(LIBS OpenSSL::Crypto crypto_shared)

add_executable(ex2-aes-ecb main-aes-ecb.cpp ${SOURCES})
target_link_libraries(ex2-aes-ecb PRIVATE ${LIBS})
target_compile_features(ex2-aes-ecb PUBLIC cxx_std_17)
target_include_directories(ex2-aes-ecb PRIVATE include)

add_executable(ex2-aes-cbc main-aes-cbc.cpp ${SOURCES})
target_link_libraries(ex2-aes-cbc PRIVATE ${LIBS})
target_compile_features(ex2-aes-cbc PUBLIC cxx_std_17)
target_include_directories(ex2-aes-cbc PRIVATE include)

add_executable(ex2-camellia-ecb main-camellia-ecb.cpp ${SOURCES})
target_link_libraries(ex2-camellia-ecb PRIVATE ${LIBS})
target_compile_features(ex2-camellia-ecb PUBLIC cxx_std_17)
target_include_directories(ex2-camellia-ecb PRIVATE include)

add_executable(ex2-camellia-cbc main-camellia-cbc.cpp ${SOURCES})
target_link_libraries(ex2-camellia-cbc PRIVATE ${LIBS})
target_compile_features(ex2-camellia-cbc PUBLIC cxx_std_17)
target_include_directories(ex2-camellia-cbc PRIVATE include)
