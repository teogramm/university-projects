#ifndef DATABASE_PROJECT_RECTANGLE_H
#define DATABASE_PROJECT_RECTANGLE_H
#include <vector>

class Rectangle{
public:
    Rectangle() = default;
    Rectangle(std::vector<double> &&min, std::vector<double> &&max);
    double getArea() const;
    /**
     * Combine the two rectangles and return a new rectangle enclosing both.
     * @param r2 Rectangle to add to this rectangle
     * @return A rectangle that combines the two rectangles.
     */
    Rectangle combineWith(const Rectangle &r2) const;

    Rectangle getOverlapRectangle(const Rectangle& r2);

    double getPerimeter();
    Rectangle(const std::vector<double> &min, const std::vector<double> &max);
    std::vector<double> min;
    std::vector<double> max;

    bool overlaps(const Rectangle &r2) const;

    bool containsPoint(const std::vector<double> &coordinates) const;
};

#endif //DATABASE_PROJECT_RECTANGLE_H
