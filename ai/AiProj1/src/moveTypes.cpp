//
// Created by theodore on 18/03/2020.
//

#include "moveTypes.h"

std::string moveTypesToSting(moveTypes m){
    switch (m){
        case up:
            return "up";
        case down:
            return "down";
        case right:
            return "right";
        case left:
            return "left";
        case none:
            return "";
    }
    return "";
}