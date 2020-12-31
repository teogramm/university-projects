#include "index/Leaf.h"

LeafEntry::LeafEntry(uint64_t offset, std::vector<double> &&coordinates) {
    this->offset = offset;
    this->coordinates = coordinates;
}

LeafEntry::LeafEntry(uint64_t offset, const std::vector<double> &coordinates){
    this->offset = offset;
    this->coordinates = coordinates;
};

Rectangle LeafEntry::getRectangle() const {
    return Rectangle(coordinates,coordinates);
}

uint64_t LeafEntry::getOffset() const {
    return offset;
}

std::vector<double> LeafEntry::getCoordinates() const {
    return coordinates;
}
