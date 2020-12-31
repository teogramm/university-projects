#ifndef CRYPTO_UTIL_H
#define CRYPTO_UTIL_H
#include <vector>
#include <string>

/**
 * Converts a vector of bytes to a string representing each byte as two hexadecimal digits
 */
std::string convertBytesToHexString(const std::vector<uint8_t>& bytes);


/**
 * Converts a string of bytes to a vector of bytes. Each byte in the input string is represented
 * by a two-digit hexadecimal number.
 */
std::vector<uint8_t> convertHexStringToBytes(const std::string& hexString);

/**
 * Generates a vector of byteNumber random bytes.
 */
std::vector<uint8_t> generateRandomBytes(size_t byteNumber);

#endif //CRYPTO_UTIL_H
