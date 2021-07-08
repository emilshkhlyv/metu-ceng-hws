#include "the9.h"

// do not add extra libraries here


#define DEBUG 
#define DEBUG_STDERR(x) do { std::cerr << (x) << endl; } while(0)
#define DEBUG_ARRAY(a, n) do { for (int i = 0; i < n; i++) std::cerr << a[i] << " "; std::cerr << endl; } while(0)

// for example usage of DEBUG macros check test.cpp

void computeLSP (char*& pattern, int patternSize, int*& LSP){
    int size = 0; 
    LSP[0] = 0;
    for(int i = 1; i < patternSize; ){
        if(pattern[i] == pattern[size]) {
            ++size;
            LSP[i] = size;
            ++i;
        }
        else {
            if(size != 0){
                size = LSP[size - 1];
            }
            else {
                LSP[i] = 0;
                ++i;
            }
        }
    }
    
}

void calc(int*& iList, int i, int*& jList, int j, char* txt, char*& pattern, int*& LSP, int*& list){
    if(pattern[jList[j]] == txt[iList[i]]){
        ++jList[j];
        ++iList[i];
    }
    if(jList[j] == strlen(pattern)){
        list[iList[i]] = iList[i]+1-jList[j];
        jList[j] = LSP[jList[j]-1];
    }
    
    else if(iList[i] < strlen(txt) && pattern[jList[j]] != txt[iList[i]]){
        if(jList[j] != 0){
            jList[j] = LSP[jList[j]-1];
        }
        else{
            ++iList[i];
        }
    }
}



char* search (char txt[], char pat[],char alphabet[]){
    int patternSize = strlen(pat);
    int textSize    = strlen(txt);
    int pos         = -1;
    int alpCount    = alphabet[1] - alphabet[0] + 1;

    for(int i = 0; i < patternSize; ++i) {
        if(pat[i] == '?') {
            pos = i;
        }
    }    
    char** patterns = (char **) malloc(sizeof(char*)*alpCount);
    for(int i = 0; i < alpCount; ++i) {
        patterns[i] = new char[patternSize];
    }
    
    if(pos != -1){
        int** LSP = new int*[alpCount];
        for(int i = 0; i < alpCount; ++i) {
            LSP[i]      = new int[patternSize];
        }
        for(int i = 0; i < alpCount; ++i){
            for(int j = 0; j < patternSize; ++j){
                LSP[i][j] = 0;
            }
        }
        for(int i = 0; i < alpCount; ++i){
            for(int j = 0; j < patternSize; ++j){
                patterns[i][j] = pat[j];
            }
            patterns[i][pos] = char(alphabet[0]+i);
        }
        for(int i = 0; i < alpCount; ++i){
            computeLSP(patterns[i], patternSize, LSP[i]);
        }
        int* list = new int[textSize];
        for(int i = 0; i < textSize; ++i){
            list[i] = -1;
        }
        int* iList = new int[alpCount];        
        int* jList = new int[alpCount];
        for(int i = 0; i < alpCount; ++i){
            iList[i] = 0;
            jList[i] = 0;
        }
        bool exist = true;
        
        while(exist){
            
            for(int k = 0; k < alpCount; ++k){
                if(iList[k] < textSize){
                    calc(iList, k, jList, k, txt, patterns[k], LSP[k], list);
                }
            }
            exist = false;
            for(int i = 0; i < alpCount; ++i){
                if(iList[i] < textSize){
                    exist = true;
                }
            }
        }
        std::sort(list, list+textSize);
        int counter = 0;
        for(int i = 0; i < textSize; ++i){
            if(list[i]!=-1){
                ++counter;
            }
        }
        string p = "\"";
        for(int i = 0; i < textSize; ++i){
            if(list[i] != -1){
                p += to_string(list[i]);
                p += " ";
            }
        }
        p += "\"";
        char* v = new char [p.length()];
        for (int i = 0; i < sizeof(v); i++) {
            v[i] = p[i];
        }
        return v;
    }
    else {
        int* LSP = new int[alpCount];
        for(int j = 0; j < patternSize; ++j){
            LSP[j] = 0;
        }
        string k = "";
        for(int i = 0; i < alpCount; ++i){
            k += char(alphabet[0]+i);
        }
        char* patternor = new char[patternSize];
        for(int i = 0; i < patternSize; ++i){
            patternor[i] = pat[i];
        }
        int* list = new int[textSize];
        for(int i = 0; i < textSize; ++i){
            list[i] = -1;
        }
        computeLSP(patternor, patternSize, LSP);
        
        int* iList = new int[alpCount];        
        int* jList = new int[alpCount];
        for(int i = 0; i < alpCount; ++i){
            iList[i] = 0;
            jList[i] = 0;
        }
        bool exist = true;
        
        while(exist){
            calc(iList, 0, jList, 0, txt, patternor, LSP, list);
            exist = false;
            if(iList[0] < textSize){
                exist = true;
            }
        }
        std::sort(list, list+textSize);
        int counter = 0;
        for(int i = 0; i < textSize; ++i){
            if(list[i]!=-1){
                ++counter;
            }
        }
        string p = "\"";
        for(int i = 0; i < textSize; ++i){
            if(list[i] != -1){
                p += to_string(list[i]);
                p += " ";
            }
        }
        p += "\"";
        char* v = new char [p.length()];
        for (int i = 0; i < sizeof(v); i++) {
            v[i] = p[i];
        }
        return v;
    }
    return 0;
}
