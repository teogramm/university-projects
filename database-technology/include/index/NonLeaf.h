#ifndef DATABASE_PROJECT_NONLEAF_H
#define DATABASE_PROJECT_NONLEAF_H
#include <cstdint>
#include "index/Rectangle.h"
#include "index/Entry.h"

class NonLeafEntry: public Entry{
public:
    NonLeafEntry(uint64_t childOffset, Rectangle rectangle);
    Rectangle getRectangle() const override;
    uint64_t getChildOffset() const;
private:
    uint64_t childOffset;
    Rectangle mbr;
};

#endif //DATABASE_PROJECT_NONLEAF_H
