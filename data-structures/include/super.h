#ifndef DOMES_2019_SUPER_H
#define DOMES_2019_SUPER_H

// Superclass containing basic methods common for all structures
class DataStructure{
    public:
        virtual void insert(int) = 0;
        virtual int getSize() = 0;
};
#endif