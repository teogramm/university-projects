#include <algorithm>
#include <iostream>
#include <cmath>
#include "Block.h"
#include "helper_queue.h"

Block::Block(const std::vector<uint8_t> &blockData, unsigned pointDimensions): sizeInBytes(blockData.size()),
            bytesUsed(0), pointDimensions(pointDimensions){
    readPoints(blockData);
}

void Block::readPoints(const std::vector<uint8_t> &blockData) {
    // Use a pointer for more flexibility
    auto dataPointer = blockData.data();
    auto dataEnd = blockData.data() + blockData.size();
    // Read all the bytes of the block
    // Add uint32 to make sure the next recordId we are going to read will be within the block.
    while(dataPointer + sizeof(uint32_t) < dataEnd) {
        // Store the start position so we can calculate the size of this record
        // TODO: Maybe this can be moved outside the loop
        auto blockStart = dataPointer;
        // Read each field and then advance the pointer to the next field.
        // For each field cast the data pointer to the relevant type and then dereference it.
        auto recordId = *(reinterpret_cast<const uint32_t*>(dataPointer));
        // We assume no more records are in the block after we encounter a block with recordId 0.
        if(recordId == 0){
            break;
        }
        dataPointer += sizeof(recordId);
        auto osmId = *(reinterpret_cast<const uint64_t*>(dataPointer));
        dataPointer += sizeof(osmId);
        std::vector<double> coordinates;
        // Dimensions are hard-coded in the app
        for(unsigned i =0; i<pointDimensions; i++) {
            auto coordinate = *(reinterpret_cast<const double*>(dataPointer));
            coordinates.emplace_back(coordinate);
            dataPointer += sizeof(coordinate);
        }
        // Read the length of the name string
        auto nameLength = *(reinterpret_cast<const uint8_t*>(dataPointer));
        dataPointer += sizeof(nameLength);
        // Read the name and advance the pointer
        auto name = std::string();
        if(nameLength > 0) {
            name = std::string(dataPointer, dataPointer + nameLength);
            dataPointer += nameLength;
        }
        bytesUsed += dataPointer - blockStart;
        points.emplace_back(Point(recordId, osmId, std::move(coordinates), name));
    }
}

void Block::writePoint(std::ostream &file, const Point &point, unsigned int offset) {
    file.seekp(offset);
    // record id 32 bits
    auto recordId = point.getRecordId();
    file.write(reinterpret_cast<const char *>(&recordId), 4);
    // osm id 64 bits
    auto osmId = point.getOsmId();
    file.write(reinterpret_cast<const char *>(&osmId),8);
    for(auto c: point.getCoordinates()) {
        file.write(reinterpret_cast<char *>(&c),sizeof(double));
    }
    // Name length as 8-bit integer
    auto nameLength = static_cast<uint8_t>(point.getName().size());
    file.write(reinterpret_cast<const char *>(&nameLength), sizeof(nameLength));
    // Name
    file.write(point.getName().c_str(),nameLength);
}

unsigned int Block::getFreeBytes() const {
    return sizeInBytes - bytesUsed;
}

unsigned int Block::getBytesUsed() const {
    return bytesUsed;
}

unsigned Block::getBytesUsed(std::istream &file, unsigned int blockOffset, unsigned maxBlockSize) {
    // Browse through records until we find a record with recordId 0 or we reach the end of the block.
    auto blockEnd = blockOffset + maxBlockSize;
    file.seekg(blockOffset);
    while(file.tellg() < blockEnd){
        // Read the record id
        uint32_t recordId;
        file.read(reinterpret_cast<char *>(&recordId), sizeof(recordId));
        if(recordId == 0) {
            // Add the size of uint32 to compensate for reading the record id
            return (static_cast<unsigned>(file.tellg()) - blockOffset - sizeof(uint32_t));
        }
        // If recordId != 0, skip the osmId, read the length of the name and skip the name data.
        file.seekg(sizeof(uint64_t), std::ios::cur);
        uint8_t nameLength;
        file.read(reinterpret_cast<char *>(&nameLength),sizeof(nameLength));
        file.seekg(nameLength, std::ios::cur);
    }
    // If the loop has ended we have reached the max block size.
    return maxBlockSize;
}

std::vector<Point>
Block::findPoints(const std::vector<double> &lowLimits, const std::vector<double> &upperLimits) {
    auto matches = std::vector<Point>();
    for(auto& p: points){
        bool match = true;
        const auto& coordinates = p.getCoordinates();
        for(unsigned i=0; i < coordinates.size(); i++ && match){
            if(coordinates.at(i) <= lowLimits.at(i) || coordinates.at(i) >= upperLimits.at(i)) {
                match = false;
            }
        }
        if(match) {
            matches.emplace_back(p);
        }
    }
    return matches;
}

pointMaxHeap Block::nearestPoints(const std::vector<double> &startingPoint, unsigned int numberOfPoints) {
    // We want a maxheap, so we have the furthest away point available at any given time. When we see a new point,
    // we check if it is closer than the furthest away point. If it is, we remove the old one and insertData it in the
    // queue
    auto nearest = pointMaxHeap();
    // Populate heap
    for(unsigned i=0; i < numberOfPoints && i < points.size(); i++){
        auto point = points.at(i);
        nearest.push(std::make_pair(point, Point::getDistance(startingPoint, point.getCoordinates())));
    }
    // Check the rest of the points
    for(unsigned i=numberOfPoints; i < points.size(); i++){
        auto point = points.at(i);
        auto distance = Point::getDistance(startingPoint, point.getCoordinates());
        // If the new point is closer than the one currently further away
        if(nearest.top().second > distance){
            nearest.pop();
            nearest.push(std::make_pair(point, distance));
        }
    }
    return nearest;
}

Point Block::findPoint(unsigned int recordId) {
    for(auto &point: points){
        if(point.getRecordId() == recordId){
            return point;
        }
    }
}
