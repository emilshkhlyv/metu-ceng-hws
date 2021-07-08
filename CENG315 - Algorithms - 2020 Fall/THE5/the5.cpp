#include "the5.h"

// do not add extra libraries here


#define DEBUG 
#define DEBUG_STDERR(x) do { std::cerr << (x) << endl; } while(0)
#define DEBUG_ARRAY(a, n) do { for (int i = 0; i < n; i++) std::cerr << a[i] << " "; std::cerr << endl; } while(0)

// for example usage of DEBUG macros check test.cpp


int BuyStocks (int**& market, int n1, int n2, vector<int>& solution)
{
    std::vector<std::vector<std::pair<int,int> > > dp (n1,
        std::vector<std::pair<int, int> > (n2, std::make_pair(-2000000000, 0)));
    
    dp[0][0] = std::make_pair (market[0][0], 0);
    
    for (int i = 1; i < n2; ++ i) {
        dp[0][i] = std::make_pair(market[0][i], i);
        if (dp[0][i].first < dp[0][i-1].first) {
            dp[0][i] = dp[0][i-1];
        }
    }
    
    for (int i = 1; i < n1; ++ i) {
        for (int j = i; j < n2; ++ j) {
            
            dp[i][j].first = market[i][j];
            dp[i][j].first += dp[i-1][j-1].first;
            dp[i][j].second = j;
            
            if (dp[i][j].first < dp[i][j-1].first) {
                dp[i][j] = dp[i][j-1];
            }
        }
    }
    
    
    int bestIndex = 0;
    for (int i = 0; i < n2; ++ i) {
        if (dp[n1-1][i].first > dp[n1-1][bestIndex].first) {
            bestIndex = i;
        }
    }
    
    int i = n1-2, j = bestIndex-1;
    
    solution.push_back (bestIndex);
    while (i >= 0) {
        solution.push_back (dp[i][j].second);
        -- i; -- j;
    }
    
    for (int i = 0; i < solution.size()/2; ++ i) {
        std::swap(solution[i], solution[solution.size()-i-1]);
    }
    
    return dp[n1-1][n2-1].first;
}
