#include "xml/OsmImporter.h"
#include <pugixml.hpp>
#include <string>

using string = std::string;


void OsmImporter::importPoints(const std::string &xmlFileName, Database &database) {
    if(database.getDimensions() != 2) {
        throw std::invalid_argument("Database must have 2 dimensions for import to work.");
    }
    unsigned count = 0;
    pugi::xml_document doc;
    auto result = doc.load_file(xmlFileName.c_str());
    for(auto node: doc.child("osm").children("node")){
        // Check the node's name
        auto name = std::string();
        for(auto tag: node.children("tag")) {
            if(tag.attribute("k").value() == std::string("name")) {
                name = tag.attribute("v").value();
                break;
            }
        }
        auto id = node.attribute("id").as_ullong();
        auto latitude = node.attribute("lat").as_double();
        auto longitude = node.attribute("lon").as_double();
        auto point = Point(0,id,{latitude,longitude},name);
        database.insertPoint(point);
    }
}
