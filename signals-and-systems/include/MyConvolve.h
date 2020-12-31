#ifndef SS1_MYCONVOLVE_H
#define SS1_MYCONVOLVE_H

#include <vector>
#include <memory>
#include <thread>
#include <future>
#include <iostream>
#include <limits>
#include <algorithm>
#include <execution>
#include <cstdlib>

using namespace std;

/**
 * Calculate the convolution of given vectors for points in range [start,end]
 * Size of vector h must be smaller or equal than size of vector x
 * @tparam Ret Type of return vector
 * @tparam T Type of given vectors
 * @param h Vector of type T
 * @param x Vector of type T
 * @param start First point to be calculated
 * @param end Last point to be calculated
 * @return Vector of type Ret
 */
template <typename Ret, typename T>
unique_ptr<vector<Ret>> convolveAtRange(const vector<T>&h, const vector<T>&x,std::size_t start,std::size_t end){
    auto tVector = make_unique<vector<Ret>>();
    tVector->reserve(end-start);
    long N = x.size();
    long M = h.size();
    for(int n=start;n <= end;n++){
        Ret sum = 0;
        // Sum starts either from 0 or -N+1+n (whichever one is bigger)
        // Sum ends at M-1 or n (whichever one is smaller)
        // Reminder that M<=N
        long convStart = 0;
        if(n-N+1 > 0) {
            convStart = -N+1+n;
        }
        long convEnd = n;
        if(M-1 < n){
            convEnd = M-1;
        }
        for (std::size_t i = convStart; i <= convEnd; i++) {
            sum += h[i]*x[n-i];
        }
        tVector->push_back(sum);
    }
    return tVector;
}

/**
 * Calculates the convolution between given vectors (multi-threaded)
 * @tparam Ret Type of return vector
 * @tparam T Type of given vectors
 * @return unique_ptr to convolution
 */
template <typename Ret, typename T>
unique_ptr<vector<Ret>> myConvolve(const vector<T> &a, const vector<T> &b){
    // Find computer cores
    auto processor_count = std::thread::hardware_concurrency();
    if(processor_count == 0){
        processor_count = 2;
    }
    // First we find the convolution with the fewest points and we assign it
    // to variable h.
    vector<T> h,x;
    auto convolution = make_unique<vector<Ret>>();
    if( a.size() < b.size() ){
        h = a;
        x = b;
    }else{
        h = b;
        x = a;
    }
    std::size_t N = x.size();
    std::size_t M = h.size();
    // Convolution is zero for all n<0 and n> N+M-2
    auto range = (N+M-2)/processor_count;

    // Vector to store the threads
    std::vector<future<unique_ptr<vector<Ret>>>> threads;
    threads.reserve(processor_count);

    // Assign work to other threads
    for(unsigned i=1; i < processor_count; i++) {
        auto start = i*range;
        unsigned end;
        if( i == processor_count-1){
            end = N+M-2;
        }else{
            end = start + range-1;
        }
        threads.push_back(async(std::launch::async,&convolveAtRange<Ret,T>,h,x,start,end));
    }
    // Assign work to this thread
    unique_ptr<vector<Ret>> firstRange;
    // If there is a single core we run the whole range at once
    // otherwise just calculate for the first range-1 elements
    if(processor_count == 1) {
        firstRange = convolveAtRange<Ret,T>(h, x, 0, range);
    }else{
        firstRange = convolveAtRange<Ret,T>(h, x, 0, range - 1);
    }
    // Combine all the results
    convolution->insert(convolution->end(),firstRange->cbegin(),firstRange->cend());
    for(auto& u:threads){
        auto result = u.get();
        convolution->insert(convolution->end(),result->begin(),result->end());
    }
    return convolution;
}

/**
 * Calculates the convolution between given vectors (single-thread)
 * @tparam T Type of elements in vectors
 * @return unique ptr to vector of type
 */
template <typename T>
unique_ptr<vector<T>> singleConvolve(const vector<T> &a, const vector<T> &b) {
    // First we find the convolution with the fewest points and we assign it
    // to variable h.
    vector<T> h,x;
    auto convolution = make_unique<vector<T>>();
    if( a.size() < b.size() ){
        h = a;
        x = b;
    }else{
        h = b;
        x = a;
    }
    int N = x.size();
    int M = h.size();
    // Convolution is zero for all n<0 and n> N+M-2
    for(int n=0; n <= N+M-2; n++) {
        double sum = 0;
        // Sum starts either from 0 or -N+1+n (whichever one is bigger)
        int start = -N+1+n>0 ? -N+1+n : 0;
        // Sum ends at M-1 or n (whichever one is smaller)
        // Reminder that M<=N
        int end = M-1 < n ? M-1 : n;
        for (int i = start; i <= end; i++) {
            sum += h[i]*x[n-i];
        }
        convolution->push_back(sum);
    }
    return convolution;
}

#endif //SS1_MYCONVOLVE_H
