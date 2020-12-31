#include "UserInteraction/UI.h"
#include "Utils/Utils.h"
#include "MyConvolve.h"
#include <vector>
#include <iostream>

int exercise1(){
    // Prompt user
    auto numberOfElements = Helper::getIntLargerThan(10);
    // Create vector of random elements
    auto randomVector = Helper::createRandomVector<double>(numberOfElements);
    // Create a vector of 5 elements with value 1/5
    auto defaultVector = std::vector<double>(5,0.2);
    std::cout << "Random vector is: " << Helper::formatVector(*randomVector,',') << endl;
    auto result = myConvolve<double,double>(*randomVector,defaultVector);
    std::cout << "Convolution is: " << Helper::formatVector(*result,',') << endl;
    return 0;
}