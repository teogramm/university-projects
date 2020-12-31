#include "crypto_shared/Util.h"
#include <sstream>
#include <iomanip>
#include <sys/random.h>

std::string convertBytesToHexString(const std::vector<uint8_t> &bytes) {
    std::stringstream hexString;
    // Set stream output format to hex
    hexString << std::hex;
    for(auto byte: bytes) {
        // Set width of all outputs to 2 character and pad each characters with 0.
        // These options only apply for the next output, so they must be inside the loop.
        hexString << std::setw(2) << std::setfill('0') << static_cast<int>(byte);
    }
    return hexString.str();
}

std::vector<uint8_t> convertHexStringToBytes(const std::string &hexString) {
    std::vector<uint8_t> bytes;
    bytes.reserve(hexString.size());
    if(hexString.size() % 2 != 0) {
        throw std::invalid_argument("Input text is invalid.");
    }
    std::istringstream hexStringStream(hexString);
    while(!hexStringStream.eof()) {
        uint8_t byte;
        char buffer[3] = {'a','b','\0'};
        hexStringStream.read(buffer,2);
        byte = strtol(buffer, nullptr,16);
        bytes.emplace_back(byte);
    }
    // The loop always reads one byte more at the end. For the moment just remove it.
    bytes.pop_back();
    return bytes;
}

std::vector<uint8_t> generateRandomBytes(size_t byteNumber) {
    std::vector<uint8_t> key(byteNumber);
    // Use the underlying array of the vector to store the random bits.
    getrandom(key.data(), key.size(),0);
    return key;
}