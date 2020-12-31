#ifndef CRYPTO_RC4_H
#define CRYPTO_RC4_H
#include <vector>
#include <cstdint>
#include <string>

/*
 * Code based on algorithms found on Wikipedia https://en.wikipedia.org/wiki/RC4
 */
std::vector<uint8_t> generatePermutation(const std::vector<uint8_t>&);
std::vector<uint8_t> encrypt(const std::string& message, std::vector<uint8_t> permutation);
std::vector<uint8_t> encrypt(const std::vector<uint8_t>& message, std::vector<uint8_t> permutation);
#endif //CRYPTO_RC4_H
