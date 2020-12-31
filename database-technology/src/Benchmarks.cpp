#include "Benchmarks.h"
#include <iostream>
#include <chrono>
#include <index/Index.h>

void testRange(Database& database) {
    auto singlePoint = std::vector{40.59239863852829, 22.94781758054225};
    auto doublePoint = std::vector{40.60541609844405, 22.960263002033575};
    auto results = database.findPoints(singlePoint, doublePoint);
    for(auto& p: results){
        if(!p.getName().empty()) {
            std::cout << p.describe() << '\n';
        }
    }
}

void timeARange(Database &database, const std::vector<double> &low, const std::vector<double> &high){
    auto preSearchWithIndex = std::chrono::high_resolution_clock::now();
    auto resultsWithTIme = database.findPointsIndexTimed(low,high);
    auto postSearchWithIndex = std::chrono::high_resolution_clock::now();
    auto preSearchLinear = std::chrono::high_resolution_clock::now();
    auto linear = database.findPoints(low, high);
    auto postSearchLinear = std::chrono::high_resolution_clock::now();
    std::cout <<"Index search : " << resultsWithTIme.second.count()
              << "\nTotal time with index: "
              << std::chrono::duration_cast<std::chrono::milliseconds>(postSearchWithIndex - preSearchWithIndex).count()
              << "\nTime without index: "
              << std::chrono::duration_cast<std::chrono::milliseconds >(postSearchLinear - preSearchLinear).count()
              << '\n';
}

void timeRange(Database &database){
    auto singlePoint = std::vector{40.59239863852829, 22.94781758054225};
    auto doublePoint = std::vector{40.60541609844405, 22.960263002033575};
    std::cout << "Small area\n";
    timeARange(database, singlePoint, doublePoint);
    singlePoint = {40.574228153966956, 22.942882128652663};
    doublePoint = {40.61424523108736, 22.9832225506235};
    std::cout << "\nMedium area\n";
    timeARange(database, singlePoint, doublePoint);
    std::cout << "\nAll of thessaloniki\n";
    singlePoint = {40.55531925320463, 22.92554433027371};
    doublePoint = {40.667390465795975, 22.982879228995365};
    timeARange(database,singlePoint,doublePoint);
}

void timeANearestNeighbors(Database &database, const std::vector<double> &centerPoint, unsigned pointNumber){
    auto preLinear = std::chrono::high_resolution_clock::now();
    auto results = database.nearestPoints(centerPoint, pointNumber);
    auto postLinear = std::chrono::high_resolution_clock::now();
    auto linearTime = std::chrono::duration_cast<std::chrono::milliseconds>(postLinear - preLinear);
    std::cout << pointNumber << " nearest points linear: " << linearTime.count() << '\n';
    auto preIndex = std::chrono::high_resolution_clock::now();
    auto indexResults = database.nearestPointsIndex(centerPoint, pointNumber);
    auto postIndex = std::chrono::high_resolution_clock::now();
    auto indexTime = std::chrono::duration_cast<std::chrono::milliseconds>(postIndex - preIndex);
    std::cout << pointNumber << " nearest points indexed: " << indexTime.count() << '\n';
}

void timeNearestNeighbors(Database& database){
    auto centerPoint = {40.60827008311881, 22.966061693306482};
    for(unsigned i=100; i <= 100000; i *= 10){
        timeANearestNeighbors(database, centerPoint, i);
    }
}

void testNearestNeighbors(Database& database){
    auto centerPoint = {40.59878565127596, 22.952142087157984};
    auto results = database.nearestPointsIndex(centerPoint, 100);
    for(auto& p: results){
        if(!p.getName().empty()){
            std::cout << p.describe() << '\n';
        }
    }
}

void testIndex(){
    auto index = Index("test.in4", 2, 4,true);
    auto p1 = Point(18, 18818, {1.0,1.0}, "aa");
    auto p2 = Point(19, 18818, {2.0,2.0}, "aa");
    auto p3 = Point(20, 18818, {0.0,1.0}, "aa");
    auto p4 = Point(21, 18818, {1.0,4.0}, "aa");
    auto p5 = Point(22, 18818, {5.0,5.0}, "aa");
    auto p6 = Point(23, 18818, {4.0,4.0}, "aa");
    auto p7 = Point(24, 18818, {6.0,6.0}, "aa");
    auto p8 = Point(25, 18818, {0.0,0.0}, "aa");
    auto p9 = Point(26, 18818, {1.5,1.5}, "aa");
    auto p10 = Point(26, 18818, {7,7}, "aa");
    auto p11 = Point(26, 18818, {1.25,1.25}, "aa");
    auto p12 = Point(26, 18818, {1.75,1.75}, "aa");
    index.insert(p1, 0);
    index.insert(p2, 0);
    index.insert(p3, 0);
    index.insert(p4, 0);
    // First split with root split is here
    index.insert(p5, 0);
    index.insert(p6, 1);
    index.insert(p7, 1);
    index.insert(p8, 1);
    // Second split is here
    index.insert(p9, 1);
    // Third split is here
    index.insert(p10, 2);
    index.insert(p11, 2);
    // Second root split is here
    index.insert(p12, 2);
    auto results = index.findPoints({0.0,0.0},{4.0,4.0});
    std::cout << std::endl;
}
