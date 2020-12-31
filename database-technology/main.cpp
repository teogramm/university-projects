#include <iostream>
#include "Database.h"
#include "Benchmarks.h"
#include "Point.h"
#include "xml/OsmImporter.h"
#include "index/Rectangle.h"

int main() {
    auto db2 = Database("saloniki.db",16,2,true);
    OsmImporter::importPoints("../saloniki.osm",db2);
    testRange(db2);
    timeRange(db2);
    timeNearestNeighbors(db2);
}
