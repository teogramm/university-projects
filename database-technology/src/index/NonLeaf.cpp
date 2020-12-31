#include "index/NonLeaf.h"

#include <utility>

NonLeafEntry::NonLeafEntry(uint64_t childOffset, Rectangle rectangle): mbr(std::move(rectangle)) {
    this->childOffset = childOffset;
}

Rectangle NonLeafEntry::getRectangle() const {
    return mbr;
}

uint64_t NonLeafEntry::getChildOffset() const {
    return childOffset;
}