#ifndef DATABASE_PROJECT_LEAF_H
#define DATABASE_PROJECT_LEAF_H
#include <cstdint>
#include "index/Entry.h"

class LeafEntry: public Entry{
public:
    LeafEntry(uint64_t offset, std::vector<double> &&coordinates);
    LeafEntry(uint64_t offset, const std::vector<double> &coordinates);
    Rectangle getRectangle() const override;
    uint64_t getOffset() const;
    std::vector<double> getCoordinates() const;

private:
    uint64_t offset;
    std::vector<double> coordinates;
};

#endif //DATABASE_PROJECT_LEAF_H
