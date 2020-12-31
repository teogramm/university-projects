#include "Point.h"

#include <cmath>
#include <utility>
#include <sstream>

// Create a new temporary coordinates object and use the move constructor
Point::Point(unsigned recordId, uint64_t osmId, const std::vector<double>& coordinates, const std::string& name)
                :Point(recordId, osmId, std::move(std::vector(coordinates.begin(), coordinates.end())), name) {
}

Point::Point(unsigned recordId, uint64_t osmId, std::vector<double> &&coordinates, std::string name)
    :recordId(recordId),osmId(osmId),coordinates(std::move(coordinates)),name(std::move(name)){}

unsigned Point::getDimensions() {
    return coordinates.size();
}

uint64_t Point::getOsmId() const {
    return osmId;
}

unsigned int Point::getRecordId() const {
    return recordId;
}

const std::string &Point::getName() const {
    return name;
}

const std::vector<double> &Point::getCoordinates() const {
    return coordinates;
}

size_t Point::getSize() const{
    // uint8_t is used to store the string length
    return sizeof(recordId) + sizeof(osmId) + coordinates.size() * sizeof(double) + sizeof(uint8_t) + name.size();
}

void Point::setRecordId(uint32_t rId) {
    this->recordId = rId;
}

std::string Point::describe() const {
    std::stringstream description;
    description << "Point ID: " << osmId << '\n'
                << "Name: " << name << '\n'
                << "Coordinates: " ;
    for(auto coordinate: coordinates) {
        description << coordinate << ",";
    }
    description << '\n';
    return description.str();
}

double Point::getDistance(const std::vector<double> &a, const std::vector<double> &b) {
    double distance = 0.0;
    if (a.size() != b.size()) {
        throw std::invalid_argument("Coordinates must have the same size!");
    }
    for(unsigned i=0; i < a.size(); i++){
        distance += pow(a.at(i) - b.at(i), 2);
    }
    return distance;
}