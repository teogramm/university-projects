#ifndef DATABASE_PROJECT_BLOCK_H
#define DATABASE_PROJECT_BLOCK_H
#include <vector>
#include <cstdint>
#include <fstream>
#include <unordered_set>
#include <queue>
#include "Point.h"
#include "helper_queue.h"

class Block{

private:
    unsigned sizeInBytes;
    unsigned bytesUsed;
    unsigned pointDimensions;
    // TODO: Maybe store this as a pointer
    std::vector<Point> points;

    /**
     * Reads the points from a block and populates the class fields.
     * @param blockData Byte array containing the data of the block.
     */
    void readPoints(const std::vector<uint8_t> &blockData);

public:
    /**
    * Writes the given Point to the given file at the given offset.
    */
    static void writePoint(std::ostream &file, const Point &point,unsigned offset);

    /**
     * I believe we have all experienced this: You return home, you are tired and you just want to find out
     * how much space is occupied in a block but you can't be bothered creating lots of Point objects just for this.
     * This function is for us. The only thing it does is calculate the used space of the block at the given offset.
     * Nothing more, nothing less.
     * @param file Database file
     * @param blockOffset Offset to the start of the block
     * @param maxBlockSize The block size used by the application. Used to stop the search if a block is full.
     * @return Number of used bytes
     */
     static unsigned getBytesUsed(std::istream& file, unsigned blockOffset, unsigned maxBlockSize);

    /**
     * Creates a new block with the given bytes. Size of the block is the number of bytes in the vector.
     * @param blockData
     */
    explicit Block(const std::vector<uint8_t>& blockData, unsigned pointDimensions);

    std::vector<Point> findPoints(const std::vector<double>& lowLimits, const std::vector<double>& upperLimits);

    [[nodiscard]] unsigned int getFreeBytes() const;
    [[nodiscard]] unsigned int getBytesUsed() const;
    /**
     * Returns the points nearest to the given starting coordinate.
     */
     pointMaxHeap nearestPoints(const std::vector<double>& startingPoint, unsigned numberOfPoints);

     /**
      * Find the point with the given recordId in the block.
      */
     Point findPoint(unsigned recordId);
};

#endif //DATABASE_PROJECT_BLOCK_H
