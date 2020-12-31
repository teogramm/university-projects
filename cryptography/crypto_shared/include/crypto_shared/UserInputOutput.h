#ifndef OTP_USERINPUTOUTPUT_H
#define OTP_USERINPUTOUTPUT_H
#include <string>
#include <iostream>
#include <algorithm>
#include <unordered_set>
#include <vector>
#include <unordered_map>
#include <stdexcept>

class UserIO {
public:
    /**
     * Gets a message from the user, checking that all characters are valid,
     * as specified in the validCharacters set.
     * @return String of user input
     */
    static std::string getMessageFromUser();
private:
    static const std::unordered_set<char> validCharacters;
    static bool isMessageValid(const std::string& message);
};

/**
 * Converts a string to a vector of bytes, according to the encoding specified in Table 1.
 * @param message String to convert
 * @return vector of bytes
 */
std::vector<uint8_t> convertMessageToNums(const std::string& message);

/**
 * Converts a vector of bytes to a string, according to the encoding specified in Table 1.
 * @param messageNums Vector of bytes, each representing a character.
 * @return Message as string
 */
std::string convertNumsToMessage(const std::vector<uint8_t>& messageNums);



#endif //OTP_USERINPUTOUTPUT_H
