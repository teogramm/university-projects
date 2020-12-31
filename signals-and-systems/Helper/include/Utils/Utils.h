#ifndef SS1_UTILS_H
#define SS1_UTILS_H
#include <vector>
#include <memory>
#include <random>
#include <algorithm>
#include <execution>

namespace Helper {

    /**
     * Creates a vector of random elements of given type
     * @param size The number of elements in the vector
     * @return A pointer to the vector
     */
    template <typename T>
    std::unique_ptr<std::vector<T>> createRandomVector(unsigned int size){
        // Seed from a real random device
        std::random_device r;
        // Create the random number generator using the real random value
        auto generator = std::default_random_engine(r());
        // Create the distribution
        auto distribution = std::uniform_real_distribution<T>(0,1000);
        // Create a lambda so getting a number is easier
        auto nextRandom = [&distribution,&generator](){ return distribution(generator);};
        // Create vector of given size to store the numbers
        auto numbers = std::make_unique<std::vector<T>>();
        for(int i=0; i<size;i++ ){
            numbers->push_back(nextRandom());
        }
        return numbers;
    }

    /**
     * Normalize given vector in place with values between 0 and 1
     * @tparam T Type of elements in vector
     * @param elVector Vector to normalize
     */
    template <typename T>
    void normalize(std::vector<T>& elVector){
        // Find largest element by absolute value
        auto minMax = minmax_element(std::execution::par, elVector.begin(), elVector.end());
        auto absMax = max(abs(*(minMax.first)),abs(*(minMax.second)));
        // Divide all elements by largest
        transform(std::execution::par, elVector.begin(), elVector.end(), elVector.begin(), [absMax](float n)-> float{
            return n/absMax;
        });
    }
}
#endif //SS1_UTILS_H
