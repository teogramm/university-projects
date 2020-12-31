#ifndef DATABASE_PROJECT_HELPER_QUEUE_H
#define DATABASE_PROJECT_HELPER_QUEUE_H
/*
 * Helper functions, structs and classes used for implementing maxHeaps with (point,distance) pairs.
 */

using pointWithDistance = std::pair<Point,double>;

// Queue puts on top points that are last, so we want the largest element to be last so queue is a maxheap
struct pointWithDistanceCompare{
    bool operator()(const pointWithDistance& a, const pointWithDistance& b) {
        return a.second < b.second;
    }
};

using pointMaxHeap = std::priority_queue<pointWithDistance, std::vector<pointWithDistance>, pointWithDistanceCompare>;

#endif //DATABASE_PROJECT_HELPER_QUEUE_H
