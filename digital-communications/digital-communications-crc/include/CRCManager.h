#ifndef AIPROJ1_CRCMANAGER_H
#define AIPROJ1_CRCMANAGER_H

class CRCManager {
public:
    CRCManager();
    static unsigned long createMessage();
    static unsigned long addFCS(const unsigned long &data);
    static bool addErrors(unsigned long &message);
    static bool verify(const unsigned long &message);
};


#endif //AIPROJ1_CRCMANAGER_H
