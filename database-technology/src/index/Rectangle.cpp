#include "index/Rectangle.h"
#include <algorithm>

Rectangle::Rectangle(const std::vector<double> &min, const std::vector<double> &max){
    this->max = max;
    this->min = min;
};

Rectangle::Rectangle(std::vector<double> &&min, std::vector<double> &&max){
    this->max = max;
    this->min = min;
};

double Rectangle::getArea() const {
    double area = 1.0;
    for(unsigned i=0; i < min.size(); i++){
        area *= max.at(i) - min.at(i);
    }
    return area;
}

Rectangle Rectangle::combineWith(const Rectangle &r2) const {
    Rectangle newRect;
    for(unsigned i=0; i< min.size();i++){
        newRect.max.push_back(std::max(this->max.at(i),r2.max.at(i)));
        newRect.min.push_back(std::min(this->min.at(i),r2.min.at(i)));
    }
    return newRect;
}

Rectangle Rectangle::getOverlapRectangle(const Rectangle &r2) {
    auto overlap = Rectangle();
    for(unsigned i=0; i< max.size();i++){
        overlap.min.push_back(std::max(this->min.at(i),r2.min.at(i)));
        overlap.max.push_back(std::min(this->max.at(i),r2.max.at(i)));
        if(overlap.min.at(i) >= overlap.max.at(i)){
            auto tempVector = std::vector<double>(min.size(),0);
            return Rectangle(tempVector,tempVector);
        }
    }
    return overlap;
}

bool Rectangle::overlaps(const Rectangle &r2) const{
    for(unsigned i=0; i< max.size();i++){
        if(this->min.at(i) > r2.max.at(i) || r2.min.at(i) > this->max.at(i)){
            return false;
        }
    }
    return true;
}

bool Rectangle::containsPoint(const std::vector<double> &coordinates) const{
    for(unsigned i=0; i < coordinates.size();i++){
        if(coordinates.at(i) > max.at(i) || coordinates.at(i) < min.at(i)){
            return false;
        }
    }
    return true;
}

double Rectangle::getPerimeter() {
    double perimeter = 0;
    for(unsigned i=0; i<min.size(); i++){
        perimeter += max.at(i) - min.at(i);
    }
    return perimeter;
}