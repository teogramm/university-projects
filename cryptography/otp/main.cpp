#include <iostream>
#include "Processing.h"
#include "crypto_shared/UserInputOutput.h"

int main() {
    auto str = UserIO::getMessageFromUser();
    std::cout << "Message: " << str << "\n";
    auto numbers = convertMessageToNums(str);
    auto key = generateRandomKey(numbers.size());
    std::cout << "Key: " << convertNumsToMessage(key) << '\n';
    auto encrypted = encrypt(numbers, key);
    std::cout << "Encrypted message: " << convertNumsToMessage(encrypted) << '\n';
    // To decrypt we encrypt using the ciphertext as message and the same key
    auto decrypted = encrypt(encrypted,key);
    std::cout << "Decrypted message: " << convertNumsToMessage(decrypted) << '\n';
    return 0;
}
