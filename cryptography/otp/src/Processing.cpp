#include "Processing.h"
#include <sys/random.h>

std::vector<uint8_t> generateRandomKey(size_t byteNo) {
    auto buffer = new uint8_t[byteNo];
    getrandom(buffer,byteNo,0);
    auto key = std::vector<uint8_t>();
    for(unsigned i = 0;i<byteNo; i++){
        // Do mod 32 to the generated numbers to put them in the 0-31 range
        key.emplace_back(buffer[i]%32);
    }
    return key;
}

std::vector<uint8_t> encrypt(const std::vector<uint8_t> &message, const std::vector<uint8_t> &key) {
    auto encrypted =  std::vector<uint8_t>();
    if(message.size() != key.size() ) {
        throw std::invalid_argument("Message and key size must match!");
    }
    for(auto i = 0; i<message.size(); i++){
        auto messageByte = message.at(i);
        auto keyByte = key.at(i);
        auto encryptedByte = messageByte^keyByte;
        encrypted.emplace_back(encryptedByte);
    }
    return encrypted;
}

