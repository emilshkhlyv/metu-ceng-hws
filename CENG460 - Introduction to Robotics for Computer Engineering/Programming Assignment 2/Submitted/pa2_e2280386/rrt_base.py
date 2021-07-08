import numpy as np

class GoalBiasedGreedySteerKNeighborhoodRRTStarBase:
    def __init__(self, seed, c_init, c_goal):
        """Constructor with seed, to be supplied to the np.random.RandomState object held inside. Feel free to add
        things. """
        self.random = np.random.RandomState(seed)
        self.c_init = c_init
        self.c_goal = c_goal
        self.nodes = []
        self.edges = []
        self.costs = []
        self.nodes.append(c_init)
        self.edges.append(np.array([c_init, c_init]))

    def distance(self, c1, c2):
        """returns the distance between two configurations c1, c2."""
        pass

    def steer(self, c0, c, step_size):
        """starting from the configuration c0, gets closer to
        c in discrete steps of size step_size in terms of distance(c0, c). returns the last no-collision
        configuration. If no collisions are hit during steering, returns c2. If
        steering is not possible at all, returns None."""
        pass

    def allclose(self, c1, c2):
        """returns True if two configurations are numerically very close (just use np.allclose default values)."""
        pass

    def sample(self, p):
        """either returns goal configuration with some goal bias probability p or returns
        a no collision configuration sample with 1-p. The goal bias becomes 0 as soon as goal node is found."""
        pass

    def neighborhood(self, c, k):
        """returns a list of k closest nodes to configuration c in terms of distance(q.value, c)"""
        nearest_nodes = []
        for node in self.nodes:
            if len(nearest_nodes) == k:
                nearest_nodes.sort(key=lambda t: self.distance(c, t))
                if self.distance(c, nearest_nodes[-1]) >= self.distance(c, node):
                    nearest_nodes.pop()
                    nearest_nodes.append(node)
            else:
                nearest_nodes.append(node)
        return nearest_nodes

    def init_rrt(self, c_init, c_goal):
        """initializes/resets rrt with the root node holding c_init and goal configuration c_goal."""
        self.c_init = c_init
        self.c_goal = c_goal
        self.nodes = []
        self.edges = []
        self.nodes.append(c_init)
        self.edges.append(np.array([c_init, c_init]))

    def valid(self, c):
        """returns True if configuration c is non-colliding."""
        pass

    def collision_free(self, c1, c2, step_size):
        """returns True if the linear trajectory between c1 and c2 are collision free."""
        pass

    # helper function for calculating path to node
    def calculatePath(self, x_min):
        path = []
        last_edge = None
        for edge in self.edges:
            if self.allclose(edge[1][1], x_min[1]) and self.allclose(edge[1][0], x_min[0]):
                last_edge = edge
                path.append(edge)
                break
        if last_edge is not None:
            while not self.allclose(last_edge[0], self.c_init):
                for edge in self.edges:
                    if self.allclose(last_edge[0], edge[1]):
                        last_edge = edge
                        path.append(edge)
                        break
        return path

    # helper function for calculating cost of path
    def calculateCost(self, path):
        distance = 0
        for edge in path:
            distance += self.distance(edge[0], edge[1])
        return distance

    def add_node(self, p, k, step_size):
        """adds a node to the rrt with goal bias probability p, near function with k closest neighbors,
        and step_size for greedy steering. returns the new configuration that is now part of the tree."""
        x_rand = self.sample(p)
        x_nearest = self.neighborhood(x_rand, 1)[0]
        x_new = self.steer(x_nearest, x_rand, step_size)
        if x_new is not None:
            if self.collision_free(x_nearest, x_new, step_size):
                X_near = self.neighborhood(x_new, k)

                self.nodes.append(x_new)

                x_min = x_nearest
                path_to_xnearest = self.calculatePath(x_nearest)
                c_min = self.calculateCost(path_to_xnearest)
                c_min += self.distance(x_nearest, x_new)

                # Connect along a minimum path
                for x_near in X_near:
                    if self.collision_free(x_near, x_new, step_size):
                        path_to_xnear = self.calculatePath(x_near)
                        cost_of_xnear = self.calculateCost(path_to_xnear)
                        cost_of_xnear += self.distance(x_near, x_new)
                        if cost_of_xnear < c_min:
                            x_min = x_near
                            c_min = cost_of_xnear

                self.edges.append(np.array([x_min, x_new]))

                # Rewiring Tree
                for x_near in X_near:
                    if self.collision_free(x_new, x_near, step_size):
                        path_to_xnew = self.calculatePath(x_new)
                        cost_of_xnew = self.calculateCost(path_to_xnew)
                        cost_of_xnew += self.distance(x_new, x_near)

                        path_to_xnear_new = self.calculatePath(x_near)
                        cost_of_xnear_new = self.calculateCost(path_to_xnear_new)

                        if cost_of_xnear_new > cost_of_xnew:
                            i = 0
                            append = False
                            while i < len(self.edges):
                                if not self.allclose(self.edges[i][0], x_new) and self.allclose(self.edges[i][1], x_near):
                                    self.edges.pop(i)
                                    if append == False:
                                        for f in range(len(self.edges)):
                                            if self.allclose(self.edges[f][0], x_near) and self.allclose(self.edges[f][1], x_new):
                                                self.edges.pop(f)
                                                break
                                        self.edges.append(np.array((x_new, x_near)))
                                        append = True
                                    i = 0
                                    continue
                                i += 1
                return x_new

    def get_path_to_goal(self):
        """returns the path to goal node as a list of tuples of configurations[(c_init, c1),(c1, c2),...,(cn,c_goal)].
        If the goal is not reachable yet, returns None."""
        # I calculated path with helper function and reverse it for right order and direction
        path = self.calculatePath(self.c_goal)
        if path != []:
            return path[::-1]
        else:
            return None

    def is_goal_reachable(self):
        """returns True if goal configuration is reachable."""
        # I used helper function for calculating path
        path = self.calculatePath(self.c_goal)
        if path == []:
            return False
        else:
            return True

    def simplify_path(self, path, step_size):
        """greedily removes redundant edges from a configuration path represented as a list of tuples
        of configurations [(c_init,c1),(c1,c2),(c2,c3)...(cn,c_goal)], as described
        Principles of Robot Motion, Theory, Algorithms and Implementations (2005), p. 213,
        Figure 7.7.(use the default version, always try to connect to c_goal, not the other way around"""
        if path != None:
            path = path[::-1]
            real_reverse_path = []
            for edge in path:
                real_reverse_path.append(np.array((edge[1], edge[0])))

            i = 0
            while i < len(real_reverse_path):
                dolar = False
                j = i+1
                while j < len(real_reverse_path):
                    if self.collision_free(real_reverse_path[i][0], real_reverse_path[j][1], step_size):
                        edge = np.array((real_reverse_path[i][0], real_reverse_path[j][1]))
                        num = j - i
                        original = i
                        for k in range(num+1):
                            real_reverse_path.pop(i)
                        real_reverse_path.insert(original, edge)
                        dolar = True
                        break
                    j += 1
                if dolar:
                    i = 0
                    continue
                i += 1

            ret = []
            for edge in real_reverse_path:
                ret.append(np.array((edge[1], edge[0])))
            ret.reverse()
            return ret

    def get_all_edges(self):
        """returns all of the edges in the tree as a list of tuples of configurations [(c1,c2),(c3,c4)...]. The
        order of the edges, The order of edges in the list and their direction is not important."""
        return self.edges

    def get_goal_cost(self):
        """returns the non-simplified goal path length in terms of distance. Returns np.Inf if goal is not reachable."""
        if not self.is_goal_reachable():
            return np.Inf
        else:
            path = self.calculatePath(self.c_goal)
            if path != []:
                # I used helper function for calculating cost
                cost = self.calculateCost(path)
                return cost
            else:
                return np.Inf