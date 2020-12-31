#ifndef OTP_PROCESSING_H
#define OTP_PROCESSING_H
#include <vector>
#include <string>
#include <unordered_map>
#include <stdexcept>

std::vector<uint8_t> generateRandomKey(size_t buteNo);
std::vector<uint8_t> encrypt(const std::vector<uint8_t>& message, const std::vector<uint8_t>& key);

#endif //OTP_PROCESSING_H
