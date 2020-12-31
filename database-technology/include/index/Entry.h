#ifndef DATABASE_PROJECT_ENTRY_H
#define DATABASE_PROJECT_ENTRY_H
#include "index/Rectangle.h"

class Entry{
public:
    virtual Rectangle getRectangle() const = 0;
};

#endif //DATABASE_PROJECT_ENTRY_H
