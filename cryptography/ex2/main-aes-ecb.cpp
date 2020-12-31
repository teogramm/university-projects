#include <cmath>
#include <vector>
#include <stdexcept>
#include <memory>
#include <iostream>
#include <chrono>
#include <random>
#include <algorithm>
#include <openssl/evp.h>
#include <openssl/bio.h>
#include "crypto_shared/Util.h"
#include "util.h"

using EVP_CIPHER_CTX_ptr = std::unique_ptr<EVP_CIPHER_CTX, decltype(&::EVP_CIPHER_CTX_free)>;
using byteVector = std::vector<uint8_t>;

/**
 * @param enablePadding During the analysis, padding is disabled so that it does not interfere with the results.
 */
std::string encrypt_ecb(const std::string &plaintext, const byteVector &key, bool enablePadding = true) {
    // 2021-04-06 verified working by using openssl command to decrypt stuff encrypted by this.
    // Based on C++ code on https://wiki.openssl.org/index.php/EVP_Symmetric_Encryption_and_Decryption
    // Initialize encryption context
    EVP_CIPHER_CTX_ptr ctx(EVP_CIPHER_CTX_new(), ::EVP_CIPHER_CTX_free);
    int rc = EVP_EncryptInit_ex(ctx.get(), EVP_aes_256_ecb(), nullptr, key.data(), nullptr);
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

std::string encrypt_ecb(const byteVector &plaintext, const byteVector& key, bool enablePadding = true) {
    auto pText = std::string(plaintext.data(), plaintext.data() + plaintext.size());
    return encrypt_ecb(pText, key, enablePadding);
}

void measure_ecb(unsigned trials, unsigned messageBlocks) {
    /* We will generate an n-block message and encrypt it. Then, we flip
     * one bit of the original message at random and encrypt it. Afterwards we will XOR the encrypted messages and
     * measure the '1' bits in the result, indicating how many bits differ between them.
     * */
    // Each block is 16 bytes
    auto messageBytes = messageBlocks * 16u;
    // Vector to store the difference percentage for each trial.
    auto observations = std::vector<double>{};
    observations.reserve(trials);
    // Set up the distributions for choosing bytes and bits to flip. Use built-in random as it's not a security critical
    // function.
    unsigned seed1 = std::chrono::system_clock::now().time_since_epoch().count();
    std::default_random_engine generator(seed1);
    std::uniform_int_distribution<unsigned> randomByte(0, messageBytes-1), randomBit(0, 7);
    auto getRandomBytePosition = [&generator,&randomByte](){ return randomByte(generator);};
    auto getRandomBitPosition = [&generator,&randomBit](){ return randomBit(generator);};
    for(int i=0;i <trials;i++) {
        // Generate a random 256 bit key
        auto key = generateRandomBytes(32);
        // Generate a 32-byte random message, choose a random byte and a random bit to flip
        auto generatedMessage = generateRandomBytes(messageBytes);
        auto bytePos = getRandomBytePosition();
        auto bitPos = getRandomBitPosition();
        auto changedMessage = generatedMessage;
        // Change the bit at bitPos position (with position 0 being the LSB) of the byte at position bytePos;
        // 0^1 = 1, 1^1=0
        changedMessage.at(bytePos) = changedMessage.at(bytePos) ^ (1u << bitPos);
        // Encrypt both messages
        auto encryptedMessage = encrypt_ecb(generatedMessage,key,false);
        auto encryptedChangedMessage = encrypt_ecb(changedMessage,key,false);
        observations.push_back(measure_difference_percentage(encryptedMessage,encryptedChangedMessage));
    }
    auto averageDifference = std::accumulate(observations.begin(), observations.end(), 0.0l)/observations.size();
    std::cout << "Average difference percentage in ECB mode: " << averageDifference << std::endl;
}

int main() {
    EVP_add_cipher(EVP_aes_256_ecb());
    measure_ecb(100000,50);
//    auto key = generateRandomBytes(32);
//    auto message = std::string("aaaaaaaaaaaaaaaa");
//    std::cout << "Key:\n" << getBase64String(key) << "\n" << "Text:\n" << getBase64String(encrypt_ecb(message,key)) << std::endl;
    return 0;
}