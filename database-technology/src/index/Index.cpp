#include <iostream>
#include <algorithm>
#include "index/Index.h"
#include <queue>
#include <cmath>

/**
 * For function comments see .h files
 */

Index::Index(const std::string& filename, uint8_t theDimensions, unsigned int maxEntries, bool createNew): dimensions(theDimensions), maxEntries(maxEntries){
    if(createNew){
        indexFile = std::fstream(filename,std::ios::in | std::ios::out | std::ios::binary | std::ios::trunc);
        indexFile.seekp(0);
        uint8_t isLeaf = 1;
        indexFile.write(reinterpret_cast<const char *>(&isLeaf), sizeof(isLeaf));
        // Create enough room for the root
        auto dummy = std::vector<uint8_t>(getNonLeafNodeSize(),0);
        indexFile.write(reinterpret_cast<const char *>(dummy.data()), dummy.size());
    }else{
        indexFile = std::fstream(filename,std::ios::in | std::ios::out | std::ios::binary);
    }
    if(!indexFile.is_open()){
        throw std::runtime_error("Could not create index file");
    }
}

unsigned Index::getLeafNodeSize() const{
    return maxEntries*(getLeafEntrySize());
}

unsigned Index::getLeafEntrySize() const {
    return 2*sizeof(uint32_t)+ dimensions*sizeof(double);
}

unsigned Index::getNonLeafNodeSize() const{
    return maxEntries*getNonLeafEntrySize();
}

unsigned Index::getNonLeafEntrySize() const {
    return sizeof(uint64_t) + 2*dimensions*sizeof(double);
}

Rectangle createRectangle(const nonLeafNode &node) {
    auto rect = node.at(0)->getRectangle();
    for(unsigned i=1; i<node.size(); i++){
        rect = rect.combineWith(node.at(i)->getRectangle());
    }
    return rect;
}

Rectangle createRectangle(const leafNode &node) {
    auto rect = node.at(0)->getRectangle();
    for(unsigned i=1; i<node.size(); i++){
        rect = rect.combineWith(node.at(i)->getRectangle());
    }
    return rect;
}

std::size_t minimizeArea(const nonLeafNode &entries, const Point& p){
    double minAreaEnlargement = -1;
    std::size_t minAreaEnlargementIndex = -1;
    auto pointRectangle = Rectangle(p.getCoordinates(),p.getCoordinates());
    for(unsigned i=0; i < entries.size(); i++){
        // Try to insertData the new point in every rectangle and choose the one with the least area enlargement
        auto combinedRect = entries.at(i)->getRectangle().combineWith(pointRectangle);
        auto areaEnlargement = combinedRect.getArea() - entries.at(i)->getRectangle().getArea();
        if(areaEnlargement < minAreaEnlargement || minAreaEnlargementIndex == -1){
            minAreaEnlargement = areaEnlargement;
            minAreaEnlargementIndex = i;
        }else if(areaEnlargement == minAreaEnlargement && entries.at(i)->getRectangle().getArea() < entries.at(minAreaEnlargementIndex)->getRectangle().getArea()){
            minAreaEnlargement = areaEnlargement;
            minAreaEnlargementIndex = i;
        }
    }
    return minAreaEnlargementIndex;
}

/**
 * Calculates overlap difference for entry at given index, after adding the point with pointRectangle
 */
double calculateOverlapDifference(const nonLeafNode &entries, const Rectangle &pointRectangle, std::size_t index){
    auto thisRectangle = entries.at(index)->getRectangle();
    auto rectangleWithPoint = thisRectangle.combineWith(pointRectangle);
    double overlapWithoutPoint = 0.0;
    double overlapWithPoint = 0.0;
    for(unsigned i=0; i<entries.size();i++){
        if(i != index){
            overlapWithoutPoint += thisRectangle.combineWith(entries.at(i)->getRectangle()).getArea();
            overlapWithPoint += rectangleWithPoint.combineWith(entries.at(i)->getRectangle()).getArea();
        }
    }
    return overlapWithPoint - overlapWithoutPoint;
}

std::size_t minimizeOverlap(const nonLeafNode &entries, const Point& p){
    double minOverlapDiff = -1;
    std::size_t minOverlapDiffIndex = -1;
    auto pointRectangle = Rectangle(p.getCoordinates(),p.getCoordinates());
    for(unsigned i=0; i < entries.size(); i++) {
        double overlap = calculateOverlapDifference(entries, pointRectangle, i);
        if(overlap < minOverlapDiff || minOverlapDiffIndex == -1){
            minOverlapDiff = overlap;
            minOverlapDiffIndex = i;
        }else if(overlap == minOverlapDiff){
            auto minRectangleWithPoint = entries.at(minOverlapDiffIndex)->getRectangle().combineWith(pointRectangle);
            auto newRectangleWithPoint = entries.at(i)->getRectangle().combineWith(pointRectangle);
            // Compare area increase after adding point
            if(newRectangleWithPoint.getArea() - entries.at(i)->getRectangle().getArea() <
                    minRectangleWithPoint.getArea() - entries.at(minOverlapDiffIndex)->getRectangle().getArea()){
                minOverlapDiff = overlap;
                minOverlapDiffIndex = i;
            }
        }
    }
    return minOverlapDiffIndex;
}

std::size_t Index::chooseSubtree(const nonLeafNode &entries, const Point& p){
    // Check if childPointers point to leaves
//    if(isLeaf(entries.at(0)->getChildOffset())){
//        return minimizeOverlap(entries, p);
//    }else{
//        return minimizeArea(entries, p);
//    }
    return minimizeArea(entries, p);
}

std::vector<offsetType> Index::chooseLeaf(const Point &p){
    auto offsets = std::vector<offsetType>();
    offsets.push_back(1);
    while(true){
        auto leaf = isLeaf(offsets.back());
        if(leaf){
            return offsets;
        }else{
            // Read the non-leaf node
            auto contents = readNonLeafNode(offsets.back());
            auto subtreeIndex = chooseSubtree(contents, p);
            offsets.push_back(contents.at(subtreeIndex)->getChildOffset());
        }
    }
}

nonLeafNode Index::readNonLeafNode(offsetType offset){
    auto nodeEntries = nonLeafNode();
    indexFile.seekg(offset);
    auto nodeLimit = static_cast<offsetType>(indexFile.tellg()) + getNonLeafNodeSize();
    while(indexFile.tellg() < nodeLimit){
        offsetType childOffset;
        indexFile.read(reinterpret_cast<char *>(&childOffset), sizeof(childOffset));
        if(childOffset != 0){
            auto mbrMin = std::vector<double>(dimensions,0);
            auto mbrMax = std::vector<double>(dimensions,0);
            indexFile.read(reinterpret_cast<char *>(mbrMin.data()), sizeof(double)*mbrMin.size());
            indexFile.read(reinterpret_cast<char *>(mbrMax.data()), sizeof(double)*mbrMax.size());
            nodeEntries.push_back(std::make_shared<NonLeafEntry>(childOffset, Rectangle(mbrMin,mbrMax)));
        }else{
            break;
        }
    }
    return nodeEntries;
}

leafNode Index::readLeafNode(offsetType offset) {
    auto entries = leafNode();
    auto limit = offset + getLeafNodeSize();
    uint64_t entryOffset;
    indexFile.seekg(offset);
    while(indexFile.tellg() < limit){
        indexFile.read(reinterpret_cast<char *>(&entryOffset), sizeof(entryOffset));
        if(entryOffset == 0){
            break;
        }
        auto coordinates = std::vector<double>(dimensions,0);
        indexFile.read(reinterpret_cast<char *>(coordinates.data()), sizeof(double) * coordinates.size());
        entries.push_back(std::make_shared<LeafEntry>(entryOffset, coordinates));
    }
    return entries;
}

bool Index::isLeaf(offsetType offset) {
    indexFile.seekg(offset - 1);
    uint8_t isLeaf;
    indexFile.read(reinterpret_cast<char *>(&isLeaf), sizeof(isLeaf));
    if(isLeaf == 0){
        return false;
    }else if(isLeaf == 1){
        return true;
    }else{
        throw std::runtime_error("Byte read not 0 or 1. Make sure the offset given is 1 after the isLeaf byte.");
    }
}

std::pair<std::shared_ptr<Entry>,unsigned> pickNext(const std::vector<std::shared_ptr<Entry>> &entries, const Rectangle &group1, const Rectangle &group2){
    double maxDiff = -1;
    std::shared_ptr<Entry> maxDiffEntry;
    unsigned maxDiffGroup;
    for(const auto& entry: entries){
        double d1 = group1.combineWith(entry->getRectangle()).getArea() - group1.getArea();
        auto d2Rect = group2.combineWith(entry->getRectangle());
        double d2 = group2.combineWith(entry->getRectangle()).getArea() - group2.getArea();
        if(std::abs(d1-d2) > maxDiff){
            maxDiff = std::abs(d1-d2);
            maxDiffEntry = entry;
            if(d1 < d2){
                maxDiffGroup = 0;
            }else{
                maxDiffGroup = 1;
            }
        }
    }
    return std::make_pair(maxDiffEntry,maxDiffGroup);
}

std::pair<std::vector<std::shared_ptr<Entry>>, std::vector<std::shared_ptr<Entry>>>
    Index::split(const std::vector<std::shared_ptr<Entry>> &entries){
    auto remainingEntries = entries;
    auto newNodesSeeds = pickSeeds(entries);
    // Remove the seeds from the remaining entries
    remainingEntries.erase(std::find(remainingEntries.begin(),remainingEntries.end(),newNodesSeeds.first));
    remainingEntries.erase(std::find(remainingEntries.begin(),remainingEntries.end(),newNodesSeeds.second));
    auto newNodes = std::pair<std::vector<std::shared_ptr<Entry>>,std::vector<std::shared_ptr<Entry>>>();
    newNodes.first.push_back(newNodesSeeds.first);
    newNodes.second.push_back(newNodesSeeds.second);
    auto rect1 = newNodesSeeds.first->getRectangle();
    auto rect2 = newNodesSeeds.second->getRectangle();
    while(!remainingEntries.empty()){
        auto newPoint = pickNext(remainingEntries, rect1, rect2);
        if(newPoint.second == 0){
            newNodes.first.push_back(newPoint.first);
            rect1 = rect1.combineWith(newPoint.first->getRectangle());
        }else{
            newNodes.second.push_back(newPoint.first);
            rect2 = rect2.combineWith(newPoint.first->getRectangle());
        }
        remainingEntries.erase(std::find(remainingEntries.begin(), remainingEntries.end(), newPoint.first));
    }
    return newNodes;
}

std::pair<leafNode ,leafNode>
Index::split(const std::vector<std::shared_ptr<LeafEntry>> &entries){
    // Convert LeafEntry pointers to generic Entry pointers
    auto convEntries = std::vector<std::shared_ptr<Entry>>();
    for(auto& entry: entries){
        convEntries.push_back(std::reinterpret_pointer_cast<Entry>(entry));
    }
    auto result = split(convEntries);
    auto newNode1 = leafNode();
    auto newNode2 = leafNode();
    for(auto &entry: result.first){
        newNode1.push_back(std::reinterpret_pointer_cast<LeafEntry>(entry));
    }
    for(auto &entry: result.second){
        newNode2.push_back(std::reinterpret_pointer_cast<LeafEntry>(entry));
    }
    return std::make_pair(newNode1,newNode2);
}

std::pair<nonLeafNode,nonLeafNode>
Index::split(const std::vector<std::shared_ptr<NonLeafEntry>> &entries){
    // Convert LeafEntry pointers to generic Entry pointers
    auto convEntries = std::vector<std::shared_ptr<Entry>>();
    for(auto& entry: entries){
        convEntries.push_back(std::reinterpret_pointer_cast<Entry>(entry));
    }
    auto result = split(convEntries);
    auto newNode1 = nonLeafNode ();
    auto newNode2 = nonLeafNode ();
    for(auto &entry: result.first){
        newNode1.push_back(std::reinterpret_pointer_cast<NonLeafEntry>(entry));
    }
    for(auto &entry: result.second){
        newNode2.push_back(std::reinterpret_pointer_cast<NonLeafEntry>(entry));
    }
    return std::make_pair(newNode1,newNode2);
}

std::pair<std::shared_ptr<Entry>, std::shared_ptr<Entry>> Index::pickSeeds(const std::vector<std::shared_ptr<Entry>> &entries) const{
    // Algorithm implemented according to notes
    double maxDiff = -1;
    std::shared_ptr<Entry> maxDiffEntry1, maxDiffEntry2;
    for(unsigned i=0;i < dimensions; i++){
        auto lowSideComparator = [&i](const std::shared_ptr<Entry>& a, const std::shared_ptr<Entry>& b){return a->getRectangle().min.at(i) < b->getRectangle().min.at(i);};
        auto highSideComparator = [&i](const std::shared_ptr<Entry>& a, const std::shared_ptr<Entry>& b){return a->getRectangle().max.at(i) < b->getRectangle().max.at(i);};
        auto lowSide = std::minmax_element(entries.begin(), entries.end(), lowSideComparator);
        auto highSide = std::minmax_element(entries.begin(), entries.end(), highSideComparator);
        // Highest low side - lowest high side
        auto separation = std::abs((*lowSide.second)->getRectangle().min.at(i) - (*highSide.first)->getRectangle().max.at(i));
        // Width = highest high side - lowest low side
        auto width = std::abs((*highSide.second)->getRectangle().max.at(i) - (*lowSide.first)->getRectangle().min.at(i));
        double diff;
        if(separation == width){
            diff = separation;
        }else{
            diff = (double) separation/width;
        }
        if(diff > maxDiff){
            maxDiff = diff;
            maxDiffEntry1 = *(lowSide.second);
            maxDiffEntry2 = *(highSide.first);
        }
    }
    return std::make_pair(maxDiffEntry1, maxDiffEntry2);
}

/**
 * Splits the root and makes it point to the given nodes
 */
void Index::splitRoot(offsetType newNode1, offsetType newNode2){
    auto newRootEntries = nonLeafNode();
    Rectangle rect1,rect2;
    if(isLeaf(newNode1) && isLeaf(newNode2)){
        rect1 = createRectangle(readLeafNode(newNode1));
        rect2 = createRectangle(readLeafNode(newNode2));
    }else if(!isLeaf(newNode1) && !isLeaf(newNode2)){
        rect1 = createRectangle(readNonLeafNode(newNode1));
        rect2 = createRectangle(readNonLeafNode(newNode2));
    }else{
        throw std::runtime_error("New root has different typed leaves.");
    }
    // Move the existing stuff out of the root
    if(newNode1 == 1){
        if(isLeaf(newNode1)){
            auto existingContents = readLeafNode(1);
            newNode1 = writeLeaf(existingContents);
        }else{
            auto existingContents = readNonLeafNode(1);
            newNode1 = writeNonLeaf(existingContents);
        }
    }
    newRootEntries.emplace_back(std::make_shared<NonLeafEntry>(newNode1,rect1));
    newRootEntries.emplace_back(std::make_shared<NonLeafEntry>(newNode2,rect2));
    writeNonLeaf(newRootEntries,1);
}

void Index::adjustParentEntry(offsetType N, offsetType P){
    Rectangle entryRectangle;
    // Create a rectangle containing all of node N's children
    if(isLeaf(N)){
        entryRectangle = createRectangle(readLeafNode(N));
    }else{
        entryRectangle = createRectangle(readNonLeafNode(N));
    }
    // Find N's entry at P
    auto limit = P + getNonLeafNodeSize();
    indexFile.seekg(P);
    while(indexFile.tellg() < limit){
        offsetType childOffset;
        indexFile.read(reinterpret_cast<char *>(&childOffset), sizeof(childOffset));
        if(childOffset == 0){
            break;
        }
        if(childOffset == N){
            indexFile.seekp(indexFile.tellg());
            indexFile.write(reinterpret_cast<const char *>(entryRectangle.min.data()), sizeof(double)*entryRectangle.min.size());
            indexFile.write(reinterpret_cast<const char *>(entryRectangle.max.data()), sizeof(double)*entryRectangle.max.size());
            return;
        }else{
            indexFile.seekg(getNonLeafEntrySize()-sizeof(childOffset),std::ios::cur);
        }
    }
    throw std::runtime_error("Entry for node N was not found in parent node P");
}

void Index::adjustTree(const std::vector<offsetType> &nodeOffsets){
    auto nodesRemaining= nodeOffsets;
    auto N = nodesRemaining.back();
    nodesRemaining.pop_back();
    while(N != 1){
        auto P = nodesRemaining.back();
        adjustParentEntry(N,P);
        N = P;
        nodesRemaining.pop_back();
    }
}

void Index::adjustTree(const std::vector<offsetType> &nodeOffsets, const leafNode &newNode1,
                            const leafNode &newNode2) {
    auto nodesRemaining = nodeOffsets;
    auto N = nodesRemaining.back();
    nodesRemaining.pop_back();
    // Write one new leaf in the place of the old one
    writeLeaf(newNode1,N);
    // Create a new leaf at the end for the second one
    auto newNode2Offset = writeLeaf(newNode2);
    while(N != 1){
        auto P = nodesRemaining.back();
        nodesRemaining.pop_back();
        // Update the entry for N at P
        adjustParentEntry(N,P);
        // Try to create a new entry for newNode2 at P
        Rectangle rect;
        if(isLeaf(newNode2Offset)){
            rect = createRectangle(readLeafNode(newNode2Offset));
        }else{
            rect = createRectangle(readNonLeafNode(newNode2Offset));
        }
        auto node2Entry = NonLeafEntry(newNode2Offset, rect);
        auto entryInserted = insertAtNonLeaf(node2Entry, P);
        if(!entryInserted){
            // We need to split P
            auto nodesToSplit = readNonLeafNode(P);
            nodesToSplit.push_back(std::make_shared<NonLeafEntry>(node2Entry));
            auto splitNodes = split(nodesToSplit);
            // P is always a non-leaf node
            // Write half the new P over the existing P
            auto pSplit1Offset = writeNonLeaf(splitNodes.first, P);
            // Create a new node for the other half
            auto pSplit2Offset = writeNonLeaf(splitNodes.second);
            N = pSplit1Offset;
            newNode2Offset = pSplit2Offset;
        }else{
            // Proceed as normal starting from P's parent
            if(!nodesRemaining.empty()) {
                adjustTree(nodesRemaining);
            }
            return;
        }
    }
    // This is executed only if we have reached the root and we have a node remaining.
    splitRoot(N,newNode2Offset);
}

bool Index::insert(const Point &point, uint64_t offset) {
    auto nodesPassed = chooseLeaf(point);
    auto insertedAtLeaf = insertAtLeaf(point, nodesPassed.back(), offset);
    if(!insertedAtLeaf){
        // We need to split the bottom leaf node
        auto entriesToSplit = readLeafNode(nodesPassed.back());
        // Add the new entry in the entries we have to distribute
        entriesToSplit.emplace_back(std::make_shared<LeafEntry>(offset, point.getCoordinates()));
        auto splitNodes = split(entriesToSplit);
        // Convert to concrete types
        adjustTree(nodesPassed,splitNodes.first,splitNodes.second);
    }else{
        adjustTree(nodesPassed);
    }
    return true;
}

bool Index::insertAtLeaf(const Point &p, offsetType offset, uint64_t dataOffset) {
    auto limit = offset + getLeafNodeSize();
    auto contents = readLeafNode(offset);
    indexFile.seekg(offset);
    while(indexFile.tellg() < limit){
        // Note where the entry starts in case it is empty so we can rewind
        auto entryStart = indexFile.tellg();
        uint64_t entryOffset;
        indexFile.read(reinterpret_cast<char *>(&entryOffset), sizeof(entryOffset));
        // If the entry is empty
        if(entryOffset == 0){
            // Write the new leaf point
            indexFile.seekp(entryStart);
            entryOffset = dataOffset;
            indexFile.write(reinterpret_cast<char *>(&entryOffset), sizeof(entryOffset));
            for(auto& c: p.getCoordinates()){
                indexFile.write(reinterpret_cast<const char *>(&c), sizeof(c));
            }
            return true;
        }
        else{
            // Seek to the next point
            indexFile.seekg(getLeafEntrySize()-sizeof(entryOffset), std::ios::cur);
        }
    }
    return false;
}

bool Index::insertAtNonLeaf(const NonLeafEntry &entry, offsetType offset) {
    auto limit = offset + getNonLeafNodeSize();
    indexFile.seekg(offset);
    while(indexFile.tellg() < limit){
        // Note where the entry starts in case it is empty so we can rewind
        auto entryStart = indexFile.tellg();
        offsetType childOffset;
        indexFile.read(reinterpret_cast<char *>(&childOffset), sizeof(childOffset));
        if(childOffset == 0){
            indexFile.seekp(entryStart);
            auto currentChildOffset = entry.getChildOffset();
            auto mbr = entry.getRectangle();
            indexFile.write(reinterpret_cast<const char *>(&currentChildOffset), sizeof(currentChildOffset));
            indexFile.write(reinterpret_cast<const char *>(mbr.min.data()), sizeof(double) * mbr.min.size());
            indexFile.write(reinterpret_cast<const char *>(mbr.max.data()), sizeof(double) * mbr.max.size());
            return true;
        }else{
            // Scroll to the next entry position
            indexFile.seekg(getNonLeafEntrySize()-sizeof(childOffset), std::ios::cur);
        }
    }
    return false;
}


unsigned Index::writeLeaf(const leafNode &entries) {
    indexFile.seekp(0,std::ios::end);
    return writeLeaf(entries, indexFile.tellp(), true);
}

unsigned Index::writeLeaf(const leafNode &entries, offsetType offset, bool writingToEnd){
    // Offset is after the isLeaf byte, which we overwrite
    if(!writingToEnd) {
        indexFile.seekp(offset - 1);
    }else{
        indexFile.seekp(offset);
    }
    uint8_t isLeaf = 1;
    indexFile.write(reinterpret_cast<const char *>(&isLeaf), sizeof(isLeaf));
    // Keep track of bytes written, so we can apply padding
    unsigned written = 0;
    for(auto &entry: entries){
        //TODO: Check this if it randomly breaks
        auto entryOffset = entry->getOffset();
        auto rectangle = entry->getRectangle();
        indexFile.write(reinterpret_cast<const char *>(&entryOffset), sizeof(entryOffset));
        written += sizeof(entryOffset);
        indexFile.write(reinterpret_cast<const char *>(rectangle.min.data()), sizeof(double)*rectangle.min.size());
        written += sizeof(double)*rectangle.min.size();
    }
    if(written < getLeafNodeSize()){
        auto buffer = std::vector<uint8_t>(getLeafNodeSize() - written,0);
        indexFile.write(reinterpret_cast<const char *>(buffer.data()), buffer.size());
    }
    if(writingToEnd){
        return offset+1;
    }else{
        return offset;
    }
}

unsigned Index::writeNonLeaf(const nonLeafNode &entries) {
    indexFile.seekp(0, std::ios::end);
    return writeNonLeaf(entries, indexFile.tellp(), true);
}

unsigned Index::writeNonLeaf(const nonLeafNode &entries, offsetType offset, bool writingToEnd){
    // Root is always large enough
    if(!writingToEnd && isLeaf(offset) && offset != 1){
        throw std::runtime_error("Cant write non leaf over existing leaf! (non leaf is bigger).");
    }
    if(!writingToEnd) {
        indexFile.seekp(offset - 1);
    }else{
        indexFile.seekp(offset);
    }
    uint8_t isLeaf = 0;
    indexFile.write(reinterpret_cast<const char *>(&isLeaf), sizeof(isLeaf));
    // Keep track of bytes written, so we can apply padding
    unsigned written = 0;
    for(auto &entry:entries){
        auto childOffset = entry->getChildOffset();
        auto mbr = entry->getRectangle();
        indexFile.write(reinterpret_cast<const char *>(&childOffset), sizeof(childOffset));
        indexFile.write(reinterpret_cast<const char *>(mbr.min.data()), sizeof(double) * mbr.min.size());
        indexFile.write(reinterpret_cast<const char *>(mbr.max.data()), sizeof(double) * mbr.max.size());
        written += sizeof(childOffset) + 2*sizeof(double)*mbr.min.size();
    }
    if(written < getNonLeafNodeSize()){
        auto buffer = std::vector<uint8_t>(getNonLeafNodeSize() - written,0);
        indexFile.write(reinterpret_cast<const char *>(buffer.data()), buffer.size());
    }
    if(writingToEnd){
        return offset + 1;
    }else{
        return offset;
    }
}

void Index::walkTree(){
    auto offsets = std::vector<offsetType>();
    offsets.push_back(1);
    unsigned count = 0;
    while(!offsets.empty()){
        if(isLeaf(offsets.front())){
            auto contents = readLeafNode(offsets.front());
            count += contents.size();
            offsets.erase(offsets.begin());
        }else{
            auto newNodes = readNonLeafNode(offsets.front());
            for(auto &node: newNodes){
                offsets.push_back(node->getChildOffset());
            }
            offsets.erase(offsets.begin());
        }
    }
    std::cout << count << std::endl;
}

std::vector<std::shared_ptr<LeafEntry>> Index::searchLeaf(unsigned int offset, const Rectangle &searchRectangle) {
    auto leafEntries = readLeafNode(offset);
    auto results = std::vector<std::shared_ptr<LeafEntry>>();
    for(auto &entry: leafEntries){
        if(searchRectangle.containsPoint(entry->getCoordinates())){
            results.push_back(entry);
        }
    }
    return results;
}

std::vector<std::shared_ptr<LeafEntry>> Index::findPoints(const std::vector<double>& lowLimits, const std::vector<double>& upperLimits){
    auto searchRectangle = Rectangle(lowLimits,upperLimits);
    auto offsets = std::vector<offsetType>();
    auto results = std::vector<std::shared_ptr<LeafEntry>>();
    // Traverse the tree in BFS starting at the root
    offsets.push_back(1);
    while(!offsets.empty()){
        if(isLeaf(offsets.front())){
            auto leafResults = searchLeaf(offsets.front(), searchRectangle);
            results.insert(results.end(), leafResults.begin(), leafResults.end());
            offsets.erase(offsets.begin());
        }else{
            auto newNodes = readNonLeafNode(offsets.front());
            for(auto &node: newNodes){
                // For each child MBR check if it overlaps with search area
                if(node->getRectangle().overlaps(searchRectangle)){
                    offsets.push_back(node->getChildOffset());
                }
            }
            offsets.erase(offsets.begin());
        }
    }
    return results;
};

/*
 * ======================== NEAREST POINT SEARCH ================================
 */

double minDist(const std::vector<double> &point, const Rectangle &r){
    // Algorithm according to paper
    double distance = 0.0;
    for(unsigned i=0 ; i<point.size(); i++){
        double ri;
        if(point.at(i) < r.min.at(i)){
            ri = r.min.at(i);
        }else if(point.at(i) > r.max.at(i)){
            ri = r.max.at(i);
        }else{
            ri = point.at(i);
        }
        distance += pow(std::abs(point.at(i) - ri),2);
    }
    return distance;
}

double minMaxDistance(const std::vector<double> &point, const Rectangle &r){
    // Algorithm according to paper
    double S = 0.0;
    for(unsigned i=0;i<point.size();i++){
        double rMi;
        if(point.at(i) >= (r.min.at(i)+r.max.at(i))/2){
            rMi = r.min.at(i);
        }else{
            rMi = r.max.at(i);
        }
        S += pow(std::abs(point.at(i) - rMi),2);
    }
    double minDistance = 0;
    for(unsigned k=0; k<point.size(); k++){
        double rmk,rMk,distance;
        double pk = point.at(k);
        if(pk <= (r.min.at(k)+r.max.at(k))/2){
            rmk = r.min.at(k);
        }else{
            rmk = r.max.at(k);
        }
        if(pk >= (r.min.at(k)+r.max.at(k))/2){
            rMk = r.min.at(k);
        }else{
            rMk = r.max.at(k);
        }
        distance = S - pow(std::abs(pk-rMk),2) + pow(std::abs(pk-rmk),2);
        if(k == 0 || distance < minDistance){
            minDistance = distance;
        }
    }
    return minDistance;
}

using entryScorePair = std::pair<std::shared_ptr<NonLeafEntry>,double>;

/**
 * Generates the active branch list sorted according to MINDIST and sorted according to MINMAXDIST
 */
std::pair<std::vector<entryScorePair>,std::vector<entryScorePair>> generateActiveBranchList(const nonLeafNode &nonLeafEntries,
                                                                                      const std::vector<double> &coordinates){
    auto sortedBranchListMinDist = std::vector<entryScorePair>();
    auto sortedBranchListMinMaxDist = std::vector<entryScorePair>();
    for(auto &entry: nonLeafEntries){
        sortedBranchListMinDist.emplace_back(std::make_pair(entry, minDist(coordinates,entry->getRectangle())));
        sortedBranchListMinMaxDist.emplace_back(std::make_pair(entry, minMaxDistance(coordinates,entry->getRectangle())));
    }
    auto sorter = [](const entryScorePair &p1, const entryScorePair &p2){return p1.second < p2.second;};
    std::sort(sortedBranchListMinDist.begin(), sortedBranchListMinDist.end(), sorter);
    std::sort(sortedBranchListMinMaxDist.begin(), sortedBranchListMinMaxDist.end(), sorter);
    return std::make_pair(sortedBranchListMinDist,sortedBranchListMinMaxDist);
}

/**
 * Prune branch list in place.
 * @param Active branch list containing MINDIST scores and sorted by them.
 */
void pruneBranchList(std::vector<entryScorePair> &activeBranchList, std::vector<entryScorePair> &sortedByMinMaxDist){
    for(unsigned i=0; i<activeBranchList.size();i++){
        auto entry = activeBranchList.at(i);
        // If MINDIST is smaller than the largest MINMAXDIST we can continue.
        if(entry.second < sortedByMinMaxDist.begin()->second){
            continue;
        }
        if(entry.second > sortedByMinMaxDist.begin()->second){
            activeBranchList.erase(activeBranchList.begin() + i);
            i--;
            break;
        }
    }
}

double getDistance(const std::vector<double> &point1, const std::vector<double> &point2){
    double distance = 0.0;
    for(unsigned i=0; i < point1.size(); i++){
        distance += pow(point1.at(i) - point2.at(i), 2);
    }
    return distance;
}

void Index::nearestPointRecursive(unsigned currentOffset, const std::vector<double> &startingPoint,
                                  unsigned int numberOfPoints, leafMaxHeap &nearestPoints){
    if(isLeaf(currentOffset)){
        auto contents = readLeafNode(currentOffset);
        for(auto &entry: contents){
            double distance = getDistance(startingPoint, entry->getCoordinates());
            if(nearestPoints.size() < numberOfPoints || distance < nearestPoints.top().second){
                if(nearestPoints.size() >= numberOfPoints){
                    nearestPoints.pop();
                }
                nearestPoints.emplace(std::make_pair(entry,distance));
            }
        }
    }else{
        auto sortedBranchLists = generateActiveBranchList(readNonLeafNode(currentOffset), startingPoint);
        auto activeBranchList = sortedBranchLists.first;
        pruneBranchList(activeBranchList, sortedBranchLists.second);
        for(auto &branch: activeBranchList){
            auto newNode = branch.first->getChildOffset();
            nearestPointRecursive(newNode, startingPoint, numberOfPoints, nearestPoints);
        }
    }
}

std::vector<std::shared_ptr<LeafEntry>> Index::nearestPoints(const std::vector<double> &startingPoint, unsigned int numberOfPoints){
    auto leafHeap = leafMaxHeap();
    auto sortedBranchLists = generateActiveBranchList(readNonLeafNode(1), startingPoint);
    auto activeBranchList = sortedBranchLists.first;
    while(!activeBranchList.empty()){
        auto childOffset = activeBranchList.back().first->getChildOffset();
        activeBranchList.pop_back();
        if(isLeaf(childOffset)){
            auto leafEntries = readLeafNode(childOffset);
            for(auto & leafEntry : leafEntries){
                auto distance = getDistance(startingPoint, leafEntry->getCoordinates());
                if(leafHeap.size() < numberOfPoints || distance < leafHeap.top().second){
                    if(leafHeap.size() >= numberOfPoints){
                        leafHeap.pop();
                    }
                    leafHeap.emplace(std::make_pair(leafEntry, distance));
                }
            }
        }else{
            auto nonLeafSortedBranchLists = generateActiveBranchList(readNonLeafNode(childOffset), startingPoint);
            auto thisLeafActiveBranchList = nonLeafSortedBranchLists.first;
            pruneBranchList(thisLeafActiveBranchList, nonLeafSortedBranchLists.second);
            activeBranchList.insert(activeBranchList.end(), thisLeafActiveBranchList.begin(), thisLeafActiveBranchList.end());
        }
    }
    auto results = leafNode();
    while(!leafHeap.empty()){
        results.push_back(leafHeap.top().first);
        leafHeap.pop();
    }
    return results;
}
