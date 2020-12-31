#include <iostream>
#include <string>
#include "UserInteraction/UI.h"
using namespace std;


int Helper::getIntLargerThan(int limit) {
    int sel = 0;
    while(true){
        cout << "Please enter a number larger than " << limit << ": ";
        string input;
        cin >> input;
        try{
            sel = stoi(input);
        } catch (invalid_argument&) {
            cout << "Invalid input!" << endl;
            continue;
        } catch (out_of_range&) {
            cout << "Input too large!" << endl;
            continue;
        }
        if( sel <= limit){
            cout << "Number must be larger than " << limit << "!" << endl;
            continue;
        }
        break;
    }
    return sel;
}


