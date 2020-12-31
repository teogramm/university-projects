#ifndef DATABASE_POINT_H
#define DATABASE_POINT_H
#include <vector>
#include <memory>
#include <string>

class Point {
public:
    /**
     * Create a new Point in n-dimensional space. Number of dimensions is
     * the number of elements in the vector.
     */
    explicit Point(unsigned recordId, uint64_t osmId, const std::vector<double>& coordinates, const std::string& name);
    explicit Point(unsigned recordId, uint64_t osmId, std::vector<double> &&coordinates, std::string  name);

    unsigned getDimensions();
    [[nodiscard]] uint64_t getOsmId() const;
    [[nodiscard]] uint32_t getRecordId() const;
    [[nodiscard]] const std::string &getName() const;
    [[nodiscard]] const std::vector<double> &getCoordinates() const;

    /**
     * Returns the size required to store all the fields of the point, including a 16-bit field to store the
     * name string length.
     * @return Size of the point in bytes
     */
    [[nodiscard]] size_t getSize() const;

    /**
     * Changes the record ID of the point.
     * @param rId New record ID of the point
     */
    void setRecordId(uint32_t rId);
    [[nodiscard]] std::string describe() const;

    /**
     * Returns the euclidean distance between two points, but NOT SQUARE ROOTED. This function is intended to be
     * used as a comparison metric when comparing distances between points.
     */
    static double getDistance(const std::vector<double> &a, const std::vector<double> &b);
private:
    std::vector<double> coordinates;
    uint64_t osmId;
    uint32_t recordId;
    std::string name;
};


#endif //DATABASE_POINT_H
