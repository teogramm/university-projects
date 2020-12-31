#ifndef UI_TEST_H
#define UI_TEST_H
#include <string>
#include <sstream>
#include <vector>


namespace Helper {
    /**
    * Prompts the user for an integer larger than the given value.
    */
    int getIntLargerThan(int limit);

    /**
     * Formats the elements of the given vector into a string.
     * @tparam T The type of the containers in the element
     * @param v The vector containing the elements
     * @param delimiter The character used to separate the elements
     * @return String with the elements of the vector
     */
    template<typename T>
    std::string formatVector(const std::vector<T> &v, char delimiter){
        auto tempStr = std::stringstream();
        tempStr << '[';
        // Special handling for the last element
        // Typecast to int is required because size() returns unsigned
        for(int i=0;i<(int)v.size()-1;i++){
            tempStr << v.at(i) << delimiter << " ";
        }
        // Output the last element
        if((int)v.size()-1 >= 0){
            tempStr << v.at(v.size() - 1);
        }
        tempStr << ']';
        return tempStr.str();
    }
}

#endif
