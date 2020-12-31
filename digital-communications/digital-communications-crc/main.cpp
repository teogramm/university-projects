#include <iostream>
#include <bitset>
#include <thread>
#include <future>
#include "CRCManager.h"

#define MESSAGE_NUMBER 5000000000

void example(){
    unsigned long message = CRCManager::createMessage();
    std::cout << "Message: " <<std::bitset<64>(message) << std::endl;
    message = CRCManager::addFCS(message);
    std::cout << "With FCS: " << std::bitset<64>(message) << std::endl;
    CRCManager::addErrors(message);
    std::cout << "With errors: " << std::bitset<64>(message) << std::endl;
    std::cout << (CRCManager::verify(message)?"Correct":"Incorrect");
}

unsigned long long* testMessages(unsigned long long messageCount,short threadIndex){
    // Returns a 3 item array with 0 being normal messages,1 messages with error caught
    // and 2 messages with error not caught
    unsigned long long normal = 0;
    unsigned long long errorCaught = 0;
    unsigned long long errorNotCaught = 0;
    for(unsigned long long i =0;i<messageCount;i++){
        unsigned long message = CRCManager::createMessage();
        message = CRCManager::addFCS(message);
        bool errorAdded = CRCManager::addErrors(message);
        bool messageCorrect = CRCManager::verify(message);
        //Show a status message
        if(i%(messageCount/4) == 0){
            std::cout << "Thread " << threadIndex << ":" << i << " messages tested(" <<i/(double)messageCount*100 << "%)" << std::endl;
        }
        if(!errorAdded && messageCorrect){
            normal++;
        }else if(errorAdded && !messageCorrect){
            errorCaught++;
        } else if(errorAdded && messageCorrect){
            errorNotCaught++;
        }else{
            // No error added but message is wrong???????
            fprintf(stderr,"Houston, we have a problem!");
        }
    }
    auto* results = new unsigned long long[3];
    results[0] = normal;
    results[1] = errorCaught;
    results[2] = errorNotCaught;
    return results;
}

int main() {
    //Get number of threads on system
    unsigned concurentThreadsSupported = std::thread::hardware_concurrency();
    if (concurentThreadsSupported == 0) {
        concurentThreadsSupported = 1;
    }
    auto* threads = new std::future<unsigned long long*>[concurentThreadsSupported];
    auto results = new unsigned long long*[concurentThreadsSupported];
    for (int i = 0; i < concurentThreadsSupported; i++) {
      threads[i] = std::async(std::launch::async,&testMessages,MESSAGE_NUMBER/4,i+1);
    }
    for (int i = 0; i < concurentThreadsSupported; i++) {
        results[i] = threads[i].get();
    }
    unsigned long long normal = 0;
    unsigned long long errorCaught = 0;
    unsigned long long errorNotCaught = 0;
    for(int i=0;i<concurentThreadsSupported;i++){
        normal += results[i][0];
        errorCaught += results[i][1];
        errorNotCaught += results[i][2];
        delete[] results[i];
    }
    delete[] results;
    std::cout << "Messages Tested: " << MESSAGE_NUMBER << std::endl;
    std::cout << "Normal Messages: " << normal << std::endl;
    std::cout << "Messages with errors: " << errorCaught + errorNotCaught << " " << 100.0/(errorCaught+errorNotCaught)
    << "%" << std::endl;
    std::cout << "---" << "Error caught: " << errorCaught << " " << 100.0/errorCaught << "%" << std::endl;
    std::cout << "---" << "Error not caught: " << errorNotCaught << " " << (errorNotCaught?100.0/errorNotCaught:0) << "%" << std::endl;
}


