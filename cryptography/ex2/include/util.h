#ifndef CRYPTO_EX2_UTIL_H
#define CRYPTO_EX2_UTIL_H

using byteVector = std::vector<uint8_t>;

/**
 * Counts how many bits are 1 in the given number's binary representation.
 */
unsigned countSetBits(unsigned v);

/**
 * Calculates the percentage of bits that differ between the given messages.
 */
double measure_difference_percentage(const byteVector &first, const byteVector &second);
double measure_difference_percentage(const std::string &first, const std::string &second);

/**
 * Converts given bytes to base64 string.
 */
std::string getBase64String(const byteVector &message);
std::string getBase64String(const std::string &message);
#endif //CRYPTO_EX2_UTIL_H
