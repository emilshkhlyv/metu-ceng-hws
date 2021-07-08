TAKE HOME EXAM 5 – WANNA BE RICH ?

int BuyStocks (int**& market, int n1, int n2, vector<int>& solution);

In this exam, you are asked to implement a decision making mechanism for stock buying from the financial market. Your aim is to find the investment that generates the best outcome for you.


For this, our AI module generated ( <$n1 x n2$> - <time x stocks> ) 2-d array $market$ where each $market[ i ][ j ]$ showing the future outcome of stock [ j ] if bought at time [ i ]. However, there are 3 rules in this market:

You must buy only one stock at a given time (discrete time steps)
You cannot buy a stock twice.
You cannot buy a stock at a given time if a bigger id stock is already been bought in earlier time steps. (e.g. if you choose to buy stock 5 at time t=2, you cannot consider buying stocks 0,1,2,3 or 4 after that point, i.e. for t = 3,4 ...)
Your task is to generate a stock buying order sequence to maximize your outcome and fill the $solution$ vector with the order (with respect to their time steps, so order is important) and return the total outcome of that order.


For quick checking of variables given:

market: (n1 x n2)  2D-array holding the stock values where $market[ i ][ j ]$ denoting the value of stock j if bought in time i.
n1  : total time steps
n2  : number of total stocks
solution  : your solution stored in this vector  where $solution[ i ]$ denotes the bought stock in time i



Constraints
n1 <= n2 < 128, (and n1 > 0). Market values are from the set {-50, -49, ... 49, 50}. 

Your code should work in O(n1*n2) complexity.

