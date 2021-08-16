###TAKE HOME EXAM 6 – GRAPH BASICS

```void SCC_TopSort (int**& graph, int n, vector<vector<int>>& scc, vector<int>& topSort);```

In this exam you are asked to find either strongly connected components or topological ordering of a given directed unweighted graph with node count as ```n```. For this, you first need to determine whether the graph is cyclic or not. If you determine that the graph has cycles you need to find strongly connected components, if it is a DAG then you need to find topological ordering of nodes.

You need to save the strongly connected components in the vector of vectors ```scc``` such that each vector in ```scc``` should contain node ids of a strongly connected component. In addition, you need to save the topological ordering of nodes in the vector ```topSort```.

Note that the node ids in ```scc``` and ```topSort``` can be in any order.

Constraints
2 < N < 128, where N is the node number

Your code should work in ```O(N^2)``` complexity.

