#ifndef DATABASE_PROJECT_DATABASE_H
#define DATABASE_PROJECT_DATABASE_H

#include <cstdint>
#include <string>
#include <fstream>
#include <index/Index.h>
#include <chrono>
#include "Block.h"

class Database{

private:
    static constexpr unsigned METADATA_BLOCK_SIZE_BYTES = 1024;

    uint8_t blockSizeKB{};
    uint32_t blockCount{};
    uint32_t recordCount{};
    uint8_t dimensions{};
    std::fstream databaseFile;
    Index index;

    /*
     * Variables used to speed up successive insertions. Instead of reading the last block on each insertion, the first
     * time we insertData a point we save the index and the free space of the block. Then we can use these values for
     * the next insertions instead of reading the file from disk.
     */
    uint32_t lastInsertedIndex = 0;
    uint32_t lastInsertedFreeBytes = 0;

    /**
     * Gets the information about the last block and writes it to the lastInsertedIndex and lastInsertedFreeBytes
     * variables.
     */
    void getLastBlockInformation();

    /**
     * Reads and populates metadata fields from the databaseFile. Sets the read pointer right at the beginning of the
     * first data block.
     */
    void readMetadata();
    /**
     * Creates a metadata block with values from the class fields. Sets the read pointer right at the beginning of the
     * first data block.
     */
    void writeMetadata();

    /**
     * Returns the offset from the start of the file for the block at the given position.
     * @param blockIndex Index of the block. The first block is at index 0.
     * @return Position where the block starts
     */
    [[nodiscard]] unsigned getDataBlockOffset(unsigned blockIndex) const;

    /**
     * Creates a new block at the end of the data file.
     */
    void createBlock();

    /**
     * Searches for a point with the given offsetInBlock at the given blockId.
     * @return Point that was found, if no point is found an exception is thrown
     */
    Point searchPointInBlock(uint32_t blockId, uint32_t offsetInBlock);
public:
    /**
     * Constructor used to open an existing database file.
     */
    explicit Database(const std::string &filename);
    /**
     * Constructor used for creating a new database file.
     * @param blockSize Block size in KiBs
     * @param overwrite Set to false by default. When set to false, calling this constructor when a file
     * already exists will throw an std::runtime error exception. If set to true the file is overwritten and its
     * contents are erased.
     */
    Database(const std::string &filename, uint8_t blockSize, uint8_t dimensions, bool overwrite = false);

    /**
     * Returns the block at the given index.
     */
    Block getBlock(unsigned blockIndex);

    /**
     * Inserts the given point in the database. Note that a record id is automatically assigned to the point during
     * the insertion.
     * @param p The point to insertData
     */
    bool insertPoint(Point& p);

    uint32_t getBlockCount() const;
    uint8_t getDimensions() const;
    uint32_t getRecordCount() const;
    /**
     * Finds all the points in given range. Limits are interpreted pairwise from the two vectors. For example,
     * in two dimensions lowLimits[0] is the lower limit for the longitude and upperLimits[0] is the upper limit
     * for the longitude, lowLimits[1] is the lower limit for the latitude etc.
     * @param lowLimits Lower limits for the points' coordinates.
     * @param upperLimits Upper limits for the points' coordinates.
     * @return Vector with all the points in the given region.
     */
    std::vector<Point> findPoints(const std::vector<double>& lowLimits, const std::vector<double>& upperLimits);

    std::vector<Point> findPointsIndex(const std::vector<double>& lowLimits, const std::vector<double>& upperLimits);
    std::pair<std::vector<Point>, std::chrono::milliseconds> findPointsIndexTimed(const std::vector<double> &lowLimits, const std::vector<double> &upperLimits);

    /**
     * Returns the points nearest to the given starting coordinate.
     * @param startingPoint The center point.
     * @param numberOfPoints Number of nearest points to return
     * @return Vector of points, sorted from furthest to nearest
     */
    std::vector<Point> nearestPoints(const std::vector<double>& startingPoint, unsigned numberOfPoints);

    Point getPointAtOffset(unsigned int offset);

    std::vector<Point> nearestPointsIndex(const std::vector<double> &startingPoint, unsigned int numberOfPoints);
};

#endif //DATABASE_PROJECT_DATABASE_H
