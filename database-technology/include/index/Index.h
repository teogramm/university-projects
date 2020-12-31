#ifndef DATABASE_PROJECT_INDEX_H
#define DATABASE_PROJECT_INDEX_H
#include <string>
#include <fstream>
#include <memory>
#include <vector>
#include <queue>
#include "index/NonLeaf.h"
#include "index/Leaf.h"
#include "Point.h"

using leafWithDistance = std::pair<std::shared_ptr<LeafEntry>,double>;

// Queue puts on top points that are last, so we want the largest element to be last so queue is a maxheap
struct leafWithDistanceCompare{
    bool operator()(const leafWithDistance& a, const leafWithDistance& b) {
        return a.second < b.second;
    }
};

using leafMaxHeap = std::priority_queue<leafWithDistance, std::vector<leafWithDistance>, leafWithDistanceCompare>;

// If you update nodes, make sure to update rectangle.h
using nonLeafNode = std::vector<std::shared_ptr<NonLeafEntry>>;
using leafNode = std::vector<std::shared_ptr<LeafEntry>>;
using offsetType = uint64_t;

class Index{
public:
    Index() = default;
    Index(const std::string& filename, uint8_t dimensions, unsigned maxEntries, bool createNew);
    bool insert(const Point &point, uint64_t offset);
    void walkTree();
    /**
     * Finds all the points in given range. Limits are interpreted pairwise from the two vectors. For example,
     * in two dimensions lowLimits[0] is the lower limit for the longitude and upperLimits[0] is the upper limit
     * for the longitude, lowLimits[1] is the lower limit for the latitude etc.
     * @param lowLimits Lower limits for the points' coordinates.
     * @param upperLimits Upper limits for the points' coordinates.
     * @return Vector with LeafEntries for the given points.
     */
    std::vector<std::shared_ptr<LeafEntry>> findPoints(const std::vector<double>& lowLimits, const std::vector<double>& upperLimits);

    std::vector<std::shared_ptr<LeafEntry>> nearestPoints(const std::vector<double> &startingPoint, unsigned int numberOfPoints);
private:
    std::fstream indexFile;
    unsigned dimensions;
    unsigned maxEntries;

    unsigned getLeafNodeSize() const;
    unsigned getLeafEntrySize() const;
    unsigned getNonLeafNodeSize() const;
    unsigned getNonLeafEntrySize() const;

    /**
     * Choose a leaf to place the new point.
     * @return Vector with the offsets of all the blocks, starting from the root and ending at the leaf.
     */
    std::vector<offsetType> chooseLeaf(const Point &p);

    /**
     * Reads the Non-Leaf node at the given offset
     * @return vector with the entries of the non-leaf node.
     */
    nonLeafNode readNonLeafNode(offsetType offset);

    /**
     * Reads the Leaf node at the given offset
     * @return vector with the entries of the leaf node.
     */
    leafNode readLeafNode(offsetType offset);

    /**
     * Returns true if the node at the given offset is a leaf.
     * The offset must be AFTER the isLeaf byte.
     */
    bool isLeaf(offsetType offset);

    /**
     * Return index of the best subtree to insertData point p.
     * @param entries Subtrees.
     * @param p Point to insertData
     * @return Index in the entries vector
     */
    std::size_t chooseSubtree(const nonLeafNode &entries, const Point& p);

    /**
     * Attempt to insert the given entry at leaf beginning in offset.
     * @returrn true if point was inserted false, otherwise
     */
    bool insertAtLeaf(const Point& p, offsetType offset, uint64_t dataOffset);

    /**
     * Attempt to insert the given entry at the non-leaf node beginning in offset.
     * @return true if the entry was inserted, false otherwise
     */
    bool insertAtNonLeaf(const NonLeafEntry &entry, offsetType offset);

    std::pair<std::vector<std::shared_ptr<Entry>>, std::vector<std::shared_ptr<Entry>>>
        split(const std::vector<std::shared_ptr<Entry>> &entries);

    std::pair<leafNode, leafNode>
        split(const std::vector<std::shared_ptr<LeafEntry>> &entries);

    void adjustTree(const std::vector<offsetType> &nodeOffsets);
    /**
     * Adjust tree, but the last node in nodeOffsets was split.
     */
    void adjustTree(const std::vector<offsetType> &nodeOffsets, const leafNode &newNode1, const leafNode &newNode2);

    /**
     * Updates the rectangle entry of N at P with an updated rectangle based on N's contents.
     */
    void adjustParentEntry(offsetType N, offsetType P);

    /**
     * Creates new leaf at the given offset.
     * @param offset Offset to write the block at. If replacing existing block make sure offset it after isLeaf.
     * @param writingToEnd When writing to end write the isLeaf without rewinding.
     * @return offset of the newly created leaf
     */
    unsigned int writeLeaf(const leafNode &entries, offsetType offset, bool writingToEnd = false);

    /**
     * Creates new leaf at the end of the file.
     * @return offset of the newly created leaf
     */
    unsigned int writeLeaf(const leafNode &entries);

    /**
     * Creates a new non leaf node at the end of the file.
     * @return offset of the newly created node
     */
    unsigned int writeNonLeaf(const nonLeafNode &entries);
    /**
     * Creates a new non leaf node at the given offset.
     * @return offset of the newly created node
     */
    unsigned int writeNonLeaf(const nonLeafNode &entries, offsetType offset, bool writingToEnd = false);

    std::pair<nonLeafNode,nonLeafNode>
    split(const std::vector<std::shared_ptr<NonLeafEntry>> &entries);

    void splitRoot(offsetType newNode1, offsetType newNode2);

    std::pair<std::shared_ptr<Entry>, std::shared_ptr<Entry>>  pickSeeds(const std::vector<std::shared_ptr<Entry>> &entries) const;

    std::vector<std::shared_ptr<LeafEntry>> searchLeaf(unsigned int offset, const Rectangle &searchRectangle);

    void nearestPointRecursive(unsigned int currentOffset, const std::vector<double> &startingPoint,
                          unsigned int numberOfPoints,
                          std::priority_queue<std::pair<std::shared_ptr<LeafEntry>, double>,
                          std::vector<leafWithDistance>, leafWithDistanceCompare> &nearestPoints);
};

#endif //DATABASE_PROJECT_INDEX_H
