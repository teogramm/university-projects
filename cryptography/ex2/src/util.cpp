#include <openssl/evp.h>
#include <algorithm>
#include <memory>
#include <stdexcept>
#include <vector>
#include <cmath>
#include "util.h"

unsigned countSetBits(unsigned int v) {
    // Code from http://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetKernighan
    unsigned c;
    for(c=0;v;c++) {
        v &= v -1;
    }
    return c;
}

double measure_difference_percentage(const byteVector &first, const byteVector &second) {
    if(first.size() != second.size()) {
        throw std::invalid_argument("Vectors must be of the same size!");
    }
    unsigned differentBits = 0;
    for(int i=0; i<first.size();i++) {
        auto firstByte = first.at(i);
        auto secondByte = second.at(i);
        // Measure the different bits between those bytes and add it to the total
        differentBits += countSetBits(firstByte ^ secondByte);
    }
    return static_cast<double>(differentBits)/(8*first.size());
}



double measure_difference_percentage(const std::string &first, const std::string &second) {
    auto firstByteVector = byteVector(first.c_str(),first.c_str()+first.size());
    auto secondByteVector = byteVector(second.c_str(), second.c_str()+second.size());
    return measure_difference_percentage(firstByteVector,secondByteVector);
}



std::string getBase64String(const byteVector &message) {
    auto messageBase64 = std::string();
    // Base-64 uses 4 characters for 3 bytes, so if we can't divide exactly by 3 we will need an additional
    // 4 characters. So total number of characters is 4*ceil(bytes/3).
    messageBase64.resize(4*ceil(static_cast<double>(message.size())/3));
    EVP_EncodeBlock((unsigned char *) messageBase64.data(), reinterpret_cast<const unsigned char *>(message.data()), message.size());
    return messageBase64;
}

std::string getBase64String(const std::string &message) {
    return getBase64String(byteVector(message.c_str(),message.c_str()+message.size()));
}
