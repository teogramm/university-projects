#include "Database.h"
#include <filesystem>
#include <iostream>
#include <algorithm>
#include <queue>

namespace fs = std::filesystem;

Database::Database(const std::string &filename) {
    auto path = fs::path{filename};
    if(!fs::exists(path)){
        throw std::runtime_error("Specified file does not exist.");
    }
    databaseFile = std::fstream(filename,std::ios::in | std::ios::out | std::ios::binary);
    if(!databaseFile.is_open()){
        throw std::runtime_error("Could not open database file");
    }
    readMetadata();
    getLastBlockInformation();
    index = Index(filename + ".index", dimensions, 10, false);
}

Database::Database(const std::string &filename, uint8_t blockSize, uint8_t dimensions, bool overwrite): index(Index(filename + ".index", dimensions, 10, true)) {
    // Initialize metadata
    this->blockCount = 0;
    this->recordCount = 0;
    this->blockSizeKB = blockSize;
    if(dimensions < 1) {
        throw std::invalid_argument("Dimensions must be at least 1.");
    }
    this->dimensions = dimensions;
    // We only care if the file already exists if overwrite is false.
    if(!overwrite) {
        auto path = fs::path{filename};
        if(fs::exists(path)){
            throw std::runtime_error("Specified file exists. If you want to erase its contents set the overwrite parameter");
        }
    }
    // Create a new file
    databaseFile = std::fstream(filename,std::ios::in | std::ios::out | std::ios::binary | std::ios::trunc);
    if(!databaseFile.is_open()){
        throw std::runtime_error("Could not create database file");
    }
    writeMetadata();
    // When creating a new block the last block information variables are already initialized and we don't need to
    // call getLastBlockInformation
}

void Database::writeMetadata() {
    databaseFile.seekg(0);
    databaseFile.seekp(0);
    databaseFile.write(reinterpret_cast<const char *>(&blockCount), sizeof(blockCount));
    databaseFile.write(reinterpret_cast<const char *>(&blockSizeKB), sizeof(blockSizeKB));
    databaseFile.write(reinterpret_cast<const char *>(&recordCount), sizeof(recordCount));
    databaseFile.write(reinterpret_cast<const char *>(&dimensions), sizeof(dimensions));
    // To check how many bytes have been written, check the position of the write pointer, since we started at 0
    auto bytesWritten = databaseFile.tellp();
    if(bytesWritten > METADATA_BLOCK_SIZE_BYTES) {
        throw std::runtime_error("Metadata size is larger than the metadata block size. The program will not function correctly.");
    }
    // Seek the file pointers to the start of the first data block.
    databaseFile.seekg(METADATA_BLOCK_SIZE_BYTES);
    databaseFile.seekg(METADATA_BLOCK_SIZE_BYTES);
}

void Database::readMetadata() {
    databaseFile.seekg(0);
    databaseFile.seekp(0);
    unsigned char buffer[8];
    // Read each value and then store it
    databaseFile.read(reinterpret_cast<char *>(buffer), sizeof(blockCount));
    blockCount = *(reinterpret_cast<typeof(blockCount)*>(buffer));
    databaseFile.read(reinterpret_cast<char *>(buffer),sizeof(blockSizeKB));
    blockSizeKB = *(reinterpret_cast<typeof(blockSizeKB)*>(buffer));
    databaseFile.read(reinterpret_cast<char *>(buffer),sizeof(recordCount));
    recordCount = *(reinterpret_cast<typeof(recordCount)*>(buffer));
    databaseFile.read(reinterpret_cast<char *>(buffer), sizeof(dimensions));
    dimensions = *(reinterpret_cast<typeof(dimensions)*>(buffer));
    // Seek the file pointers to the start of the first data block.
    databaseFile.seekg(METADATA_BLOCK_SIZE_BYTES);
    databaseFile.seekg(METADATA_BLOCK_SIZE_BYTES);
}

unsigned Database::getDataBlockOffset(unsigned int blockIndex) const {
    return METADATA_BLOCK_SIZE_BYTES + blockSizeKB * blockIndex * 1024;
}

bool Database::insertPoint(Point& p) {
    if(p.getCoordinates().size() != dimensions) {
        auto message = std::stringstream ();
        message << "Point has invalid dimensions for database (Is " << p.getCoordinates().size() << ", should be " <<
            static_cast<unsigned>(dimensions) << ").";
        throw std::invalid_argument(message.str());
    }
    /*
     We only append to the last block. This can cause issues if points with large names are inserted and leave
     lots of space in a block empty. An alternative would be checking all blocks for empty space while inserting
     but that would be bad for speed.
     A better alternative would be checking the remaining space in the last block when creating the new block and if it
     has lots of space empty, like 10%, we note its index in the metadata block. Then we check these blocks first when
     inserting a new block.
    */
    // Record IDs start at 1 and are assigned incrementally. For now we just assign a new record ID to the point, but
    // we only increment the recordCount after successful insertion.
    p.setRecordId(recordCount + 1);
    unsigned newPointOffset;
    // If there are no blocks create a new one
    auto newPointSize = p.getSize();
    // Check if point size is larger than block size, in that case we will not be able to store this point
    // TODO: Move validation to separate function and check name length
    if (newPointSize > blockSizeKB * 1024) {
        auto message = std::stringstream();
        message << "Point " << p.getOsmId() << " is too large to be stored in the database.";
        throw std::runtime_error(message.str());
    }
    // Used the cached information about the last inserted block
    auto lastBlockUsed = blockSizeKB * 1024 - lastInsertedFreeBytes;
    auto lastBlockFree = lastInsertedFreeBytes;
    // Check if we need to create a new block
    if (lastBlockFree <= newPointSize) {
        createBlock();
        // createBlock alters the blockCount
        newPointOffset = getDataBlockOffset(blockCount - 1);
        // Point was inserted in new block
        // TODO: Move these after the write so we don't update values without write
        lastInsertedIndex = blockCount - 1;
        lastInsertedFreeBytes = blockSizeKB * 1024 - newPointSize;
    } else {
        newPointOffset = getDataBlockOffset(blockCount - 1) + lastBlockUsed;
        // The same last block was used, update just the free space
        lastInsertedFreeBytes -= newPointSize;
    }
    Block::writePoint(databaseFile, p, newPointOffset);
    recordCount++;
    writeMetadata();
    p.setRecordId(getDataBlockOffset(lastInsertedIndex) - newPointOffset);
    // The point was inserted at block with index lastInsertedIndex
    index.insert(p, newPointOffset+1);
//    index.walkTree();
    return true;
}

Block Database::getBlock(unsigned int blockIndex) {
    if(blockIndex > blockCount - 1){
        throw std::out_of_range("Block index is out of bounds");
    }
    databaseFile.seekg(getDataBlockOffset(blockIndex));
    auto buffer = std::vector<uint8_t>(blockSizeKB * 1024);
    databaseFile.read(reinterpret_cast<char *>(buffer.data()), buffer.size());
    return Block(buffer, dimensions);
}

void Database::createBlock() {
    blockCount++;
    auto offset = getDataBlockOffset(blockCount -1);
    databaseFile.seekp(offset);
    auto zeros = std::vector<char>(blockSizeKB * 1024, 0);
    databaseFile.write(zeros.data(),zeros.size());
    writeMetadata();
}

std::vector<Point> Database::findPoints(const std::vector<double>& lowLimits, const std::vector<double>& upperLimits){
    if(lowLimits.size() != upperLimits.size() || lowLimits.size() != dimensions) {
        throw std::invalid_argument("Limits must have the same dimensions as the points!");
    }
    auto results = std::vector<Point>();
    for(unsigned i =0; i < blockCount; i++) {
        auto block = getBlock(i);
        auto blockResults = block.findPoints(lowLimits, upperLimits);
        results.insert(std::end(results), std::begin(blockResults), std::end(blockResults));
    }
    return results;
}

Point Database::getPointAtOffset(unsigned offset){
    databaseFile.seekg(offset);
    uint32_t recordId;
    databaseFile.read(reinterpret_cast<char *>(&recordId), sizeof(recordId));
    uint64_t osmId;
    databaseFile.read(reinterpret_cast<char *>(&osmId), sizeof(osmId));
    auto coordinates = std::vector<double>(dimensions,0);
    databaseFile.read(reinterpret_cast<char *>(coordinates.data()), sizeof(double ) * coordinates.size());
    uint8_t nameLength;
    databaseFile.read(reinterpret_cast<char *>(&nameLength), sizeof(nameLength));
    auto name = std::string();
    name.resize(nameLength);
    databaseFile.read(name.data(),name.size());
    return Point(recordId, osmId, std::move(coordinates), name);
}

uint32_t Database::getBlockCount() const {
    return blockCount;
}

void Database::getLastBlockInformation() {
    // If the database is empty reset the values.
    if(blockCount == 0) {
        lastInsertedFreeBytes = 0;
        lastInsertedIndex = 0;
    } else {
        lastInsertedIndex = blockCount - 1;
        lastInsertedFreeBytes = blockSizeKB - Block::getBytesUsed(databaseFile, getDataBlockOffset(lastInsertedIndex), blockSizeKB);
    }
}

uint8_t Database::getDimensions() const {
    return dimensions;
}

uint32_t Database::getRecordCount() const {
    return recordCount;
}

std::vector<Point> Database::nearestPoints(const std::vector<double> &startingPoint, unsigned int numberOfPoints) {
    if(startingPoint.size() != dimensions) {
        throw std::invalid_argument("Starting point has wrong number of dimensions");
    }
    // Queue puts on top points that are last, so we want the largest element to be last so queue is a maxheap
    auto nearest = pointMaxHeap ();
    for(unsigned i=0; i < blockCount; i++) {
        // Get the nearest points for the block
        auto block = getBlock(i);
        auto blockNearest = block.nearestPoints(startingPoint, numberOfPoints);
        while (!blockNearest.empty()) {
            // Check if the heap is not fully populated or if the point at the top is nearer than the point furthest
            // away from the central point
            if(nearest.size() < numberOfPoints || blockNearest.top().second < nearest.top().second) {
                if (nearest.size() > numberOfPoints) {
                    nearest.pop();
                }
                nearest.push(blockNearest.top());
            }
            blockNearest.pop();
        }
    }
    auto results = std::vector<Point>();
    while(!nearest.empty()) {
        results.push_back(nearest.top().first);
        nearest.pop();
    }
    // Reverse the vector as the first points extracted were the ones further away
    std::reverse(results.begin(), results.end());
    return results;
}

Point Database::searchPointInBlock(uint32_t blockId, uint32_t offsetInBlock) {
    // Load the block
    return getPointAtOffset(getDataBlockOffset(blockId) + offsetInBlock);
}

std::pair<std::vector<Point>,std::chrono::milliseconds>
Database::findPointsIndexTimed(const std::vector<double> &lowLimits, const std::vector<double> &upperLimits) {
    // Search index
    auto preIndex = std::chrono::high_resolution_clock::now();
    auto indexResults = index.findPoints(lowLimits, upperLimits);
    auto postIndex = std::chrono::high_resolution_clock::now();
    auto results = std::vector<Point>();
    for(auto& result: indexResults){
        results.emplace_back(getPointAtOffset(result->getOffset()-1));
    }
    return std::make_pair(results, std::chrono::duration_cast<std::chrono::milliseconds>(postIndex - preIndex));
}

std::vector<Point>
Database::findPointsIndex(const std::vector<double> &lowLimits, const std::vector<double> &upperLimits) {
    // Search index
    auto preIndex = std::chrono::high_resolution_clock::now();
    auto indexResults = index.findPoints(lowLimits, upperLimits);
    auto postIndex = std::chrono::high_resolution_clock::now();
    auto results = std::vector<Point>();
    for(auto& result: indexResults){
        results.emplace_back(getPointAtOffset(result->getOffset()-1));
    }
    return results;
}

std::vector<Point> Database::nearestPointsIndex(const std::vector<double> &startingPoint, unsigned int numberOfPoints) {
    auto indexResults = index.nearestPoints(startingPoint, numberOfPoints);
    auto points = std::vector<Point>();
    for(auto &result: indexResults){
        points.emplace_back(getPointAtOffset(result->getOffset() - 1));
    }
    return points;
}
