#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include "MyConvolve.h"
#include "Utils/Utils.h"
#include <vector>
using namespace testing;

TEST(Convolution,Equal_Length){
    auto a = unique_ptr<std::vector<double>>(new std::vector<double>{0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0});
    auto b = unique_ptr<std::vector<double>>(new std::vector<double>{0,0,0,0,0,0,0,0,0,1,2,3,2,1,0,0,0,0,0,0,0});
    auto result = myConvolve<double,double>(*a,*b);
    // Result from MATLAB
    auto expected = new vector<double>{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,3,6,8,9,9,9,9,8,6,3,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    EXPECT_THAT(*result,ContainerEq(*expected));
    delete expected;
}

TEST(Convolution,A_Smaller){
    auto a = new std::vector<double>{82,91,13,92,64,10,28,55,96,97};
    auto b = new std::vector<double>{16,98,96,49,81,15,43,92,80,96,66,4,85,94,68,76,75,40,66,18};
    auto result = myConvolve<double,double>(*a,*b);
    // Result from MATLAB
    auto expected = new vector<double>{1312,9492,16998,15500,22389,24502,18024,26824,32159,39686,50239,40151,37622,
                                       42749,33670,42469,51810,44933,40559,34539,27358,32753,28206,20949,18800,15249,
                                       11206,8130,1746};
    EXPECT_THAT(*result,ContainerEq(*expected));
    delete a;
    delete b;
    delete expected;
}

TEST(Convolution,B_Smaller){
    auto a = new std::vector<double>{43,13,41,13,47,18,10,13,31,24,18,42,30,28,46,15,38,38,20,29};
    auto b = new std::vector<double>{4,3,27,39,47,7,29,24,1,17};
    auto result = myConvolve<double,double>(*a,*b);
    // Result from MATLAB
    auto expected = new vector<double>{172,181,1364,2203,3862,3075,5135,4708,4979,4210,4021,5120,4206,4817,5257,5847,
                                       5585,6192,6467,5591,6750,5692,4321,4314,1988,2005,1362,369,493};
    EXPECT_THAT(*result,ContainerEq(*expected));
    delete a;
    delete b;
    delete expected;
}

TEST(Convolution,Parallel_Same_As_Sequential){
    auto a = Helper::createRandomVector<double>(10);
    auto b = Helper::createRandomVector<double>(15);
    auto sequential = singleConvolve<double>(*a,*b);
    auto parallel = myConvolve<double,double>(*a,*b);
    EXPECT_THAT(*sequential,ContainerEq(*parallel));
}