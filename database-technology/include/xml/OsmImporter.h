#ifndef DATABASE_PROJECT_OSMIMPORTER_H
#define DATABASE_PROJECT_OSMIMPORTER_H
#include <string>
#include "Database.h"

class OsmImporter {
public:
    /**
     * Imports points from an XML file to the database
     * @param xmlFileName File name of the XML File to import
     * @param database Database to import the points to
     */
    static void importPoints(const std::string &xmlFileName, Database& database);
};

#endif //DATABASE_PROJECT_OSMIMPORTER_H
