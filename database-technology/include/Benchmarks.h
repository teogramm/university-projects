#ifndef DATABASE_PROJECT_BENCHMARKS_H
#define DATABASE_PROJECT_BENCHMARKS_H
#include "Database.h"

void timeRange(Database& database);
void timeNearestNeighbors(Database& database);

void testRange(Database& database);
void testNearestNeighbors(Database& database);

void testIndex();

#endif //DATABASE_PROJECT_BENCHMARKS_H
