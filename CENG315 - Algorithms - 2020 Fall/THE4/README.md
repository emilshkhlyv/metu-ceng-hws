TAKE HOME EXAM 4 – ASSEMBLY LINE SCHEDULING 

std::pair<int,int> Assembly_Rec (int*& a1, int*& a2, int n, int*& t1, int*& t2, int e1, int e2, int x1, int x2);

std::pair<int,int> Assembly_Memo (int*& a1, int*& a2, int n, int*& t1, int*& t2, int e1, int e2, int x1, int x2);

std::pair<int,int> Assembly_Tab (int*& a1, int*& a2, int n, int*& t1, int*& t2, int e1, int e2, int x1, int x2);

In this exam, you are asked to implement Assembly Line Scheduling problem in 3 different ways. In this problem there are 2 assembly lines. In the pipeline of the $n$ stations, each one of them has a particular work to accomplish. Parallel stations of assembly lines accomplish the same task in different times given in $a1[.]$ or $a2[.]$ depending on the which assembly line the station is in. In order to manufacture a car perfectly, it must pass through all of the stations.

When passed through a station $i$, it can continue on the next station $i+1$ in the same line without any extra cost or the line can be switched to the $i+1$th station of the other line incurring a cost given in $t1$ and $t2$ denoting cost of switching form line 1 to 2 and from line 2 to 1 respectively. In addition, there are two sets of costs, namely $e1,e2$ and $x1,x2$, representing entry and exit times of respective assembly lines. 

For quick checking of variables given:

a1[i] :the time for line 1 station i to complete its task
a2[i] :the time for line 2 station i to complete its task
n      : number of stations in each assembly line 
t1[i]  : cost of changing from line 1 station i-1 to line 2 station i 
t2[i]  : cost of changing from line 2 station i-1 to line 1 station i 
e1,e2    : cost of entry for assembly line 1,2 respectively, each > 0
x1,x2    : cost of exit for assembly line 1,2 respectively, each > 0


 Your task is to implement 3 different ways to compute the minimum time for manufacturing a car. 

You need to implement the recursive solution and return the optimal value and number of recursive calls ( it is easier to count at the exact time when the function starts executing)
You need to implement the recursive+memoization solution and return the optimal value and number of recursive calls ( it is easier to count at the exact time when the function starts executing)
You need to implement the tabulation solution and return the optimal value and number of total iterations in the tabulation loop 
     In each case, optimal value should be first of the pair to be returned whereas number of calls or loops are the second.



Constraints
Array sizes are less than 32 for Recursive, 256 for Memoization and 1024 for Tabular (and >= 2)
