#include "RC4.h"
#include <stdexcept>
#include <utility>
#include <sys/random.h>

/*
 * Code based on algorithms found on Wikipedia https://en.wikipedia.org/wiki/RC4
 */
std::vector<uint8_t> generatePermutation(const std::vector<uint8_t>& key) {
    if(key.empty() || key.size() > 256) {
        throw std::invalid_argument("Key size must be between 1 and 256 bits");
    }
    std::vector<uint8_t> permutation(256);
    for(unsigned i=0;i < 256; i++) {
        permutation.at(i) = i;
    }
    unsigned j = 0;
    for(unsigned i=0;i < 256; i++) {
        j = (j + permutation.at(i) + key.at(i % key.size())) % 256;
        std::swap(permutation.at(i),permutation.at(j));
    }
    return permutation;
}

std::vector<uint8_t> encrypt(const std::string& message, std::vector<uint8_t> permutation) {
    // Convert the string to byte vector
    std::vector<uint8_t> messageBytes(message.c_str(),message.c_str()+message.size());
    return encrypt(messageBytes,std::move(permutation));
}

std::vector<uint8_t> encrypt(const std::vector<uint8_t> &message, std::vector<uint8_t> permutation) {
    std::vector<uint8_t> ciphertext;
    ciphertext.reserve(message.size());
    uint8_t i=0, j=0;
    // Get the c string representation for the message
    // Encrypt each character of the message
    for(unsigned char c : message) {
        // Generate the keystream byte
        i = (i+1) % 256;
        j = (j + permutation.at(i)) & 256;
        std::swap(permutation.at(i), permutation.at(j));
        uint8_t K = permutation.at((permutation.at(i) + permutation.at(j)) % 256);
        // XOR the keystream byte with the message byte and store the result.
        ciphertext.push_back( K ^ c);
    }
    return ciphertext;
}
