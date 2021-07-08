TAKE HOME EXAM 8 – 2 Source 2 Destination Transportation Problem

int minCost(int**& graph, int V, int s1, int s2, int d1, int d2, int A, int B);
Consider you are given a directed weighted graph G(V,E) with  N nodes and M edges and 2 source nodes as s1 and s2, and 2 destination nodes as d1 and d2. Assume that nodes represent locations and edges represent roads and weight is the distance (let's say in km) between the locations. We would like to transport some goods from s1 to d1 and s2 to d2. If we transport them separately the the cost is A per km. If we transport them together, then the cost is B per km (we know that A<B<2A), so that carrying them together will cost less when shared edges available on some part of the road. 

Determine and return the minimum total cost to perform 2 transportation operations from s1 to d1 and s2 to d2.

Constraints
Nodes are number with consecutive integers from 0 to V-1, and V<100.

Edge weights are positive integers, and weights < 100.

A and B are 2 positive integers, both are smaller than 100 and A<B<2A.

Your code should work in O(V^3) complexity.

Consider INF as any number greater than 99.