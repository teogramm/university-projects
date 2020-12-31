#include <random>
#include <chrono>
#include <bitset>
#include "CRCManager.h"

#define k 10
#define n 15
#define BER 0.001
#define P 0b110101
// Plen is n-k+1
#define Plen 6

CRCManager::CRCManager()= default;


/**
 * Create a message of dataSize bits. The result is stored as a number.
 * @return an unsigned long with value from 1 up to 2^dataSize
 */
unsigned long CRCManager::createMessage() {
    // construct a random generator engine from a time-based seed:
    unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
    std::default_random_engine generator (seed);

    // For 10 bit numbers so upper bound is 2^10-1
    // For k-bit numbers upper bound is 2^k -1
    std::uniform_int_distribution<unsigned long> distribution(0,pow(2,k)-1);

    // Return a random number
    return distribution(generator);
}

unsigned long CRCManager::addFCS(const unsigned long &data){
    // create a bitset from our message with n-k zeroes appended
    std::bitset<n> messageBits(data << (unsigned) (n-k));
    // create a bitset from the P number
    std::bitset<Plen> pBits(P);
    // On bitsets position 0 is the LSB.
    auto tempBitSet = new std::bitset<Plen>();
    // Copy the first Plen bits from the message bitset to the temp bit set
    for(int i=0;i<Plen;i++){
        tempBitSet->set(Plen-1-i,messageBits[n-1-i]);
    }
    // The next bit that will be brought down is initially the 14-6=8th bit
    // or n-1-Plen bit. Because numbering starts from n-1 and we have brought down
    // Plen bits
    int nextBitPos = n-1-Plen;
    // Once we bring down the least significant bit (at position 0) we have calculated the fcs in tempBitSet
    while(true){
        //Align the leading 1 of the divisor with the first 1 of the dividend
        while((*tempBitSet)[Plen-1] != 1){
            // While the MSB is not one shift left by one
            *tempBitSet<<=1;
            (*tempBitSet)[0] = messageBits[nextBitPos];
            nextBitPos--;
            if(nextBitPos==-1){
                break;
            }
        }
        if(nextBitPos==-1){
            // If the dividend has not been zeroed, we need to perform
            // an additional division
            if((*tempBitSet)[Plen-1] == true){
                *tempBitSet^=pBits;
            }
            break;
        }
        // XOR with P
        *tempBitSet^=pBits;
    }
    // Append the n-k FCS bits to the end of the message bitset
    for(int i =0;i<n-k;i++){
        messageBits[i] = (*tempBitSet)[i];
    }
    delete tempBitSet;
    return messageBits.to_ulong();
}

bool CRCManager::addErrors(unsigned long &message) {
    // construct a random generator engine from a time-based seed:
    unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
    std::default_random_engine generator (seed);

    // Create a bernoulli distribution. The probability of an error is 10^-3.
    // If the distribution returns true there is an error at that bit and we flip it
    std::bernoulli_distribution bernoulliDistribution(BER);

    // Create a bitset from the message
    auto bitset = new std::bitset<n>(message);
    bool modified = false;
    //Iterate over the bits
    for(int i=0;i<bitset->size();i++){
        if(bernoulliDistribution(generator)){
            bitset->flip(i);
            modified = true;
        }
    }
    message = bitset->to_ulong();
    delete bitset;
    return modified;
}

bool CRCManager::verify(const unsigned long &message) {
    // Code very similar to addFCS function
    // create a bitset from our message with 5 zeroes appended
    std::bitset<n> messageBits(message);
    // create a bitset from the P number
    std::bitset<Plen> pBits(P);
    // On bitset position 0 is the rightmost bit.
    auto tempBitSet = new std::bitset<Plen>();
    // Copy the first 6 bits from the message bitset to the temp bit set
    for(int i=0;i<Plen;i++){
        tempBitSet->set(Plen-1-i,messageBits[n-1-i]);
    }
    // The next bit that will be brought down is initially the 14-6=8th bit
    int nextBitPos = n-1-Plen;
    // Once we bring down the least significant bit (at position 0) we have calculated the fcs in tempBitSet
    while(true){
        //Align the leading 1 of the divisor with the first 1 of the dividend
        while((*tempBitSet)[Plen-1] != 1){
            // Shift left by one
            *tempBitSet<<=1;
            (*tempBitSet)[0] = messageBits[nextBitPos];
            nextBitPos--;
            // If next position is -1 we have seen all bits on the original message
            if(nextBitPos == -1){
                break;
            }
        }
        if(nextBitPos == -1){
            // If the dividend has not been zeroed, we need to perform
            // an additional division
            if((*tempBitSet)[Plen-1]==true){
                *tempBitSet^=pBits;
            }
            break;
        }
        // XOR with P
        *tempBitSet^=pBits;
    }
    // If check is successful the tempBitSet should contain all zeros
    bool result = tempBitSet->none();
    delete tempBitSet;
    return result;
}
