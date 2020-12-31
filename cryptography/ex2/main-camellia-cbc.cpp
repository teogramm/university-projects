#include <memory>
#include <vector>
#include <openssl/evp.h>
#include <stdexcept>
#include <random>
#include <chrono>
#include <iostream>
#include <util.h>
#include "crypto_shared/Util.h"

using EVP_CIPHER_CTX_ptr = std::unique_ptr<EVP_CIPHER_CTX, decltype(&::EVP_CIPHER_CTX_free)>;
using byteVector = std::vector<uint8_t>;

std::string encrypt_cbc(const std::string &plaintext, const byteVector &key, const byteVector &iv,
                        bool enablePadding = true) {
    EVP_CIPHER_CTX_ptr ctx(EVP_CIPHER_CTX_new(), ::EVP_CIPHER_CTX_free);
    int rc = EVP_EncryptInit_ex(ctx.get(),EVP_camellia_256_cbc(), nullptr,key.data(),iv.data());
    if (rc != 1) {
        throw std::runtime_error("EVP_EncryptInit_ex failed");
    }
    if(!enablePadding) {
        EVP_CIPHER_CTX_set_padding(ctx.get(), 0);
    }
    auto ciphertext = std::string();
    // Ciphertext might expand to an additional BLOCK_SIZE bytes, so reserve them.
    // 128 bit = 16 byte block size
    ciphertext.resize(plaintext.size() + 16);
    int outputLen1 = static_cast<int>(ciphertext.size());
    // Encrypt
    rc = EVP_EncryptUpdate(ctx.get(), reinterpret_cast<unsigned char *>(ciphertext.data()), &outputLen1,
                           reinterpret_cast<const unsigned char *>(plaintext.c_str()), static_cast<int>(plaintext.size()));
    if (rc != 1) {
        throw  std::runtime_error("EVP_EncryptUpdate failed");
    }
    // Pad leftover bytes that were not put in a block and encrypt them
    int outputLen2 = ciphertext.size() - outputLen1;
    rc = EVP_EncryptFinal_ex(ctx.get(), reinterpret_cast<unsigned char *>(ciphertext.data() + outputLen1), &outputLen2);
    if (rc != 1)
        throw std::runtime_error("EVP_EncryptFinal_ex failed");
    // Set the final ciphertext size
    ciphertext.resize(outputLen1 + outputLen2);
    return ciphertext;
}
std::string encrypt_cbc(const byteVector &plaintext, const byteVector &key, const byteVector &iv,
                        bool enablePadding = true){
    return encrypt_cbc(std::string(plaintext.data(),plaintext.data()+plaintext.size()),key,iv,enablePadding);
}
void measure_cbc(unsigned trials, unsigned messageBlocks) {
    EVP_add_cipher(EVP_camellia_256_cbc());
    // Each block is 16 bytes
    auto messageBytes = messageBlocks * 16u;
    // Vector to store the difference percentage for each trial.
    auto observations = std::vector<long double>{};
    observations.reserve(trials);
    // Set up the distributions for choosing bytes and bits to flip. Use built-in random as it's not a security critical
    // function.
    unsigned seed1 = std::chrono::system_clock::now().time_since_epoch().count();
    std::default_random_engine generator{seed1};
    std::uniform_int_distribution<unsigned> randomByte(0, messageBytes-1), randomBit(0, 7);
    auto getRandomBytePosition = [&generator,&randomByte](){ return randomByte(generator);};
    auto getRandomBitPosition = [&generator,&randomBit](){ return randomBit(generator);};
    for(int i=0;i <trials;i++) {
        // Generate a random 256 bit key
        auto key = generateRandomBytes(32);
        // IV size = block size = 16 bytes
        auto iv1 = generateRandomBytes(16);
        auto iv2 = generateRandomBytes(16);
        // Generate a 32-byte random message, choose a random byte and a random bit to flip
        auto generatedMessage = generateRandomBytes(messageBytes);
        auto bytePos = getRandomBytePosition();
        auto bitPos = getRandomBitPosition();
        auto changedMessage = generatedMessage;
        // Change the bit at bitPos position (with position 0 being the LSB) of the byte at position bytePos;
        // 0^1 = 1, 1^1=0
        changedMessage.at(bytePos) = changedMessage.at(bytePos) ^ (1u << bitPos);
        // Encrypt both messages
        auto encryptedMessage = encrypt_cbc(generatedMessage,key,iv1,false);
        // Use same or different IV depending on trial tpye
        auto encryptedChangedMessage = encrypt_cbc(changedMessage,key,iv2,false);
        observations.push_back(measure_difference_percentage(encryptedMessage,encryptedChangedMessage));
    }
    auto averageDifference = std::accumulate(observations.begin(), observations.end(), 0.0l)/observations.size();
    std::cout << "Average difference percentage in Camellia-CBC mode: " << averageDifference << std::endl;
}

int main() {
    measure_cbc(100000,50);
    return 0;
}