#include "the4.h"

// do not add extra libraries here


#define DEBUG 
#define DEBUG_STDERR(x) do { std::cerr << (x) << endl; } while(0)
#define DEBUG_ARRAY(a, n) do { for (int i = 0; i < n; i++) std::cerr << a[i] << " "; std::cerr << std::endl; } while(0)

// for example usage of DEBUG macros check test.cpp

std::pair <int, int> rechelper(int*& a1, int*& a2, int*& t1, int*& t2, const int& e1, const int& e2, int whichline, int& counter, const int& num){
    ++counter;
    std::pair <int, int > retVal(0, 0);
    if(num == 0){
        if(whichline == 1){
            retVal.first  += e1+a1[num];
            // retVal.second += counter;
        }
        else if(whichline == 2){
            retVal.first  += e2+a2[num];
            // retVal.second += counter;
        }
        return retVal;
    }
    else{
        if(whichline == 1){
            std::pair <int, int> line1 = rechelper(a1, a2, t1, t2, e1, e2, 1, counter, num-1);
            std::pair <int, int> line2 = rechelper(a1, a2, t1, t2, e1, e2, 2, counter, num-1);
            if(line1.first + a1[num] < line2.first + t2[num-1]+ a1[num]){
                retVal.first += line1.first + a1[num];
            }
            else{
                retVal.first += line2.first + t2[num-1] + a1[num];
            }
        }
        else if(whichline == 2){
            std::pair <int, int> line4 = rechelper(a1, a2, t1, t2, e1, e2, 2, counter, num-1);
            std::pair <int, int> line3 = rechelper(a1, a2, t1, t2, e1, e2, 1, counter, num-1);
            if(line4.first + a2[num]  <  line3.first + t1[num-1] + a2[num] ){
                retVal.first += line4.first + a2[num] ;
            }
            else{
                retVal.first += line3.first + t1[num-1] + a2[num];
            }
        }
    }
    return retVal;
}

std::pair<int,int> Assembly_Rec (int*& a1, int*& a2, int n, int*& t1, int*& t2, int e1, int e2, int x1, int x2)
{
    int counter = 0;
    std::pair<int,int> retVal;
    retVal.first  = 0;
    retVal.second = 0;
    std::pair<int, int> line1 = rechelper(a1, a2, t1, t2, e1, e2, 1, counter, n-1);
    std::pair<int, int> line2 = rechelper(a1, a2, t1, t2, e1, e2, 2, counter, n-1);
    if(line1.first + x1 < line2.first + x2){
        retVal.first += line1.first + x1;
        retVal.second = counter; 
    }
    else{
        retVal.first += line2.first + x2;
        retVal.second = counter;
    }
    return retVal;
}

int numer(int*& line, const int& num, int& counter){
    ++counter;
    return line[num];
}
int memohelper(int*& a1, int*& a2,  int*& t1, int*& t2, const int& e1, const int& e2, int whichline, int& counter, const int& num, int*& line1, int*& line2){
    ++counter;
    int retValue = 0;
    if(num == 0){
        if(whichline == 1){
            retValue += e1+a1[num];
            line1[0] = e1+a1[num];
        }
        else if(whichline == 2){
            retValue += e2+a2[num];
            line2[0] = e2+a2[num];
        }
        return retValue;
    }
    else{
        if(whichline == 1){
            int value1 = (line1[num-1] == -1) ? memohelper(a1, a2, t1, t2, e1, e2, 1, counter, num-1, line1, line2) : numer(line1, num-1, counter);
            int value2 = (line2[num-1] == -1) ? memohelper(a1, a2, t1, t2, e1, e2, 2, counter, num-1, line1, line2) : numer(line2, num-1, counter);
            retValue += (value1 + a1[num] < value2 + t2[num-1] + a1[num]) ? value1 + a1[num] : value2 + t2[num-1] + a1[num];
            line1[num] = retValue;
        }
        else if(whichline == 2){
            int value1 = (line1[num-1] == -1) ? memohelper(a1, a2, t1, t2, e1, e2, 1, counter, num-1, line1, line2) : numer(line1, num-1, counter);
            int value2 = (line2[num-1] == -1) ? memohelper(a1, a2, t1, t2, e1, e2, 2, counter, num-1, line1, line2) : numer(line2, num-1, counter);
            retValue += (value2 + a2[num] < value1 + t1[num-1] + a2[num]) ? value2 + a2[num] : value1 + t1[num-1] + a2[num];
            line2[num] = retValue;
        }
    }
    return retValue;
}
std::pair<int,int> Assembly_Memo (int*& a1, int*& a2, int n, int*& t1, int*& t2, int e1, int e2, int x1, int x2)
{
    int* line1 = new int[n];
    int* line2 = new int[n]; 
    for(int i =0; i < n; ++i) {
        line1[i] = -1;
        line2[i] = -1;
    }
    std::pair<int,int> retVal;
    int counter = 0;
    int val1 = memohelper(a1, a2, t1, t2, e1, e2, 1, counter, n-1, line1, line2);
    int val2 = memohelper(a1, a2, t1, t2, e1, e2, 2, counter, n-1, line1, line2);
    retVal.first = (val1 + x1 < val2 + x2) ? val1+x1 : val2+x2;
    retVal.second = counter;
    delete[] line1; delete[] line2;
    return retVal;
}

std::pair<int,int> Assembly_Tab (int*& a1, int*& a2, int n, int*& t1, int*& t2, int e1, int e2, int x1, int x2)
{
    int line1[n] = {0};
    int line2[n] = {0};
    int i = 0;
    for(; i < n; ++i){
        if(i == 0){
            line1[0] = a1[0]+e1;
            line2[0] = a2[0]+e2;
        }
        else{
            if(line1[i-1]+a1[i] < line2[i-1] + t2[i-1] + a1[i]){
                line1[i] = line1[i-1]+a1[i];
            }
            else{
                line1[i] = line2[i-1]+t2[i-1]+a1[i];
            }
            if(line2[i-1] + a2[i] < line1[i-1] + t1[i-1] + a2[i]){
                line2[i] = line2[i-1]+a2[i]; 
            }
            else{
                line2[i] = line1[i-1] + t1[i-1] + a2[i];
            }
        }
    }
    
    std::pair<int, int> retVal;
    retVal.first = (line1[n-1]+x1 < line2[n-1]+x2) ? line1[n-1]+x1 : line2[n-1]+x2;
    
    retVal.second = i;
    return retVal;
}
