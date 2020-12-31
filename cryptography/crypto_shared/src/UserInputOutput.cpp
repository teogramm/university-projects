#include <sstream>
#include <stdexcept>
#include "crypto_shared/UserInputOutput.h"

const std::unordered_set<char> UserIO::validCharacters = std::unordered_set{'.','!','?','(',')','-'};

std::string UserIO::getMessageFromUser() {
    bool messageValid{false};
    std::string message;
    while(!messageValid) {
        std::cout << "Enter a message to encrypt: ";
        std::cin >> message;
        messageValid = isMessageValid(message);
        if(!messageValid) {
            std::cout << "Invalid characters in message.\n";
            std::cin.clear();
            std::cin.sync();
        }
    }
    return message;
}

bool UserIO::isMessageValid(const std::string &message) {
    if(message.empty()) {
        return false;
    }
    auto charIsValid = [&validChars = UserIO::validCharacters](char c){
        // Check if character is capital latin letter
        if(c >= 'A' && c <= 'Z'){
            return true;
        }
        // Else check if char is an allowed special character
        // If validChars.find returns an iterator past the end character was not
        // found in validChars so it is not allowed.
        return validChars.find(c) != validChars.end();
    };
    // Search the message for an invalid character
    return std::find_if_not(message.cbegin(), message.cend(),charIsValid) == message.cend();
}

std::vector<uint8_t> convertMessageToNums(const std::string &message) {
    std::vector<uint8_t> characters;
    static const std::unordered_map<char,unsigned int> specialCharValues{
            {'.',26},
            {'!',27},
            {'?',28},
            {'(',29},
            {')',30},
            {'-',31}
    };
    for(auto c: message) {
        // Capital character values A:0 Z:25
        // Simply subtract ASCII value, A is 65 in ASCII
        if(c >= 'A' && c <= 'Z') {
            characters.emplace_back(c - 65);
        }else if(specialCharValues.count(c)) {
            characters.emplace_back(specialCharValues.at(c));
        }else {
            throw std::invalid_argument("Invalid character in string");
        }
    }
    return characters;
}

std::string convertNumsToMessage(const std::vector<uint8_t> &messageNums) {
    auto message = std::stringstream();
    // Maps numbers to their special characters
    static const std::unordered_map<uint8_t ,char> specialCharValues{
            {26,'.'},
            {27,'!'},
            {28,'?'},
            {29,'('},
            {30,')'},
            {31,'-'}
    };
    for(auto num: messageNums) {
        // If number represents a character, add 65 to convert it to its ASCII representation
        if(num >= 0 && num <= 25) {
            unsigned char character = num + 65;
            message << character    ;
        }else if(num <= 31) {
            message << specialCharValues.at(num);
        }else {
            throw std::invalid_argument("Invalid character value!");
        }
    }
    return message.str();
}