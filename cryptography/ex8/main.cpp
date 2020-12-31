#include "aescpp.h"
#include "crypto_shared/Util.h"
#include <string>
#include <algorithm>

std::string text = R"(
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus et augue elit. Morbi convallis elit vitae metus accumsan, et interdum urna suscipit. Maecenas nisl sem, commodo sit amet consectetur vel, tincidunt eget ex. Phasellus erat lacus, porta quis aliquet ac, placerat at ex. Donec tempus nibh a libero ornare, a tempus lectus viverra. Morbi aliquam tortor nunc, sed malesuada ex ornare ullamcorper. Pellentesque suscipit scelerisque aliquam. Nam eget lacus risus. Maecenas sed tortor ipsum. Sed iaculis urna a orci dignissim, et lacinia lorem hendrerit. Nulla quis congue ligula. Vivamus bibendum sodales nulla, eu luctus est maximus convallis. Donec dapibus metus quis urna rhoncus, non cursus sem imperdiet. In cursus commodo egestas. Vestibulum a quam imperdiet, porta diam eu, aliquet ante.
Integer ut nibh elementum, aliquet tortor a, mollis libero. Vestibulum convallis magna eget suscipit accumsan. Sed tristique tortor at sem viverra vestibulum. Nullam et mollis lorem, a venenatis quam. Aliquam mauris urna, hendrerit eu odio eu, volutpat aliquet magna. Interdum et malesuada fames ac ante ipsum primis in faucibus. Nulla volutpat quam risus, eget commodo erat posuere at. Vivamus neque dolor, euismod luctus imperdiet quis, placerat in orci.
Integer in lectus et eros aliquet tristique. Donec tempor in velit volutpat aliquam. Nullam id eleifend risus. Donec consequat est vitae ligula imperdiet, sit amet interdum odio rhoncus. Vestibulum pellentesque leo sed magna elementum ornare. Duis ut quam nec purus sodales viverra et viverra ipsum. Praesent hendrerit fermentum condimentum. Nulla blandit vulputate arcu et rutrum. Vestibulum euismod augue quis mollis faucibus. Aenean lacinia orci aliquet rhoncus scelerisque. Sed eget tellus massa. Vestibulum eros felis, rutrum non dapibus vitae, consectetur sed ipsum. Praesent dapibus lectus ante, at consequat nibh efficitur ut. Maecenas aliquet purus erat, eget suscipit urna faucibus sed. Etiam suscipit maximus elit et ultricies. Phasellus quam metus, pharetra in ex lobortis quam.)";

std::string stringXor(const std::string &a,const std::string &b) {
    auto newString = std::vector<char>();
    newString.reserve(a.size());
    for(unsigned i=0;i<a.size();i++){
        newString.emplace_back(a.at(i) ^ b.at(i));
    }
    return std::string(newString.begin(),newString.end());
}

int main(){
    // We use 128-bit blocks
    auto blockSize = 16;
    text.erase(std::remove(text.begin(), text.end(), '\n'), text.end());
    // Text without newlines is 2048 bits so exactly 16 blocks
    // Set up AES library
    aes_encrypt_ctx ctx;
    auto key = generateRandomBytes(128);
    aes_init();
    aes_encrypt_key128(key.data(),&ctx);
    auto encryptedData = std::vector<uint8_t>();
    auto m0 = generateRandomBytes(128);
    auto c0 = generateRandomBytes(128);
    // Iterators to the plaintext data
    auto currentStart = text.begin();
    auto currentEnd = currentStart + blockSize;
    auto blockIndex = 1;
    while(currentStart != text.end()) {
        // Create a substring with the data to be encrypted
        auto blockData = std::string(currentStart,currentEnd);
        // First XOR m_i with c_i-1
        std::string cprev;
        // If i = 1 use c0, else use the previously encrypted block
        if(blockIndex == 1) {
            cprev = std::string(std::begin(c0),std::end(c0));
        }else{
            cprev = std::string(encryptedData.begin() + 16*(blockIndex-2),encryptedData.begin() + 16*(blockIndex-1));
        }
        auto tempXor = stringXor(blockData,cprev);
        // Encrypt the XORed data
        auto tempEncrypted = std::string();
        tempEncrypted.resize(tempXor.size());
        aes_encrypt(reinterpret_cast<const unsigned char *>(tempXor.data()), (unsigned char *) tempEncrypted.data(), &ctx);
        // Then XOR the encrypted data with m_i-1
        std::string mprev;
        if(blockIndex == 1) {
            mprev = std::string(std::begin(m0),std::end(m0));
        }else{
            mprev = std::string(text.begin() + 16*(blockIndex-2),text.begin() + 16*(blockIndex-1));
        }
        auto finalXor = stringXor(tempEncrypted,mprev);
        // After XORing add the data to the ciphertext
        encryptedData.insert(std::end(encryptedData),std::begin(finalXor),std::end(finalXor));
        // Advance the iterators
        currentStart += blockSize;
        currentEnd += blockSize;
        blockIndex++;
    }
    return 0;
}