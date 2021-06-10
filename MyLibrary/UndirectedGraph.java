class UndirectedGraph{
    private static long INF = 1L << 60;
    class Edge implements Comparable<Edge>{
        int u,v;
        long length;
        public Edge(int u, int v, long l){
            this.u = Math.min(u, v);
            this.v = Math.max(u, v);
            this.length = l;
        }
        public long getLength(){return length;}
        public int getLeftVertex(){return u;}
        public int getRightVertex(){return v;}
        public int getOtherVertex(int x){
            return u+v-x;
        }
        public String toString(){
            return String.format("[%d<->%d, %d]", u, v, length);
        }
        public int compareTo(Edge o){
            return java.util.Comparator.comparingLong(Edge::getLength)
                   .thenComparingInt(Edge::getLeftVertex)
                   .thenComparingInt(Edge::getRightVertex)
                   .compare(this, o);
        }
    }

    int V, E;
    java.util.List<Edge>[] edge;
    java.util.ArrayList<Edge> allEdge;

    public UndirectedGraph(int V){
        this.V = V;
        this.E = 0;

        allEdge = new java.util.ArrayList<>();
        edge = new java.util.ArrayList[V];
        for(int v=0; v<V; v++) edge[v] = new java.util.ArrayList<>();
    }
    public void addEdge(int u, int v, long length){
        E++;
        Edge e = new Edge(u, v, length);
        allEdge.add(e);
        edge[u].add(e);
        edge[v].add(e);
    }
    public void addEdge(int u, int v){
        addEdge(u, v, 1L);
    }

    public java.util.List<Edge> getEdges(int v){return edge[v];}
    public java.util.List<Edge> getAllEdges(){return allEdge;}

    public long[] dijkstra(int start){
        long[] ans = new long[V];
        java.util.Arrays.fill(ans, INF);
        java.util.PriorityQueue<Pair<Long,Integer>> queue = new java.util.PriorityQueue<>();
        queue.add(new Pair<>(0L, start));

        while(!queue.isEmpty()){
            Pair<Long,Integer> top = queue.poll();
            long d = top.getFirst();
            int v = top.getSecond();
            
            if(d > ans[v]) continue;

            ans[v] = d;
            for(Edge e: edge[v]){
                if(d + e.getLength() < ans[e.getOtherVertex(v)]){
                    queue.add(new Pair<>(d + e.getLength(), e.getOtherVertex(v)));
                }                
            }
        }
        return ans;
    }
    public long[] bellmanford(int start) throws Exception{
        long[] distance = new long[V];
        java.util.Arrays.fill(distance, INF);
        distance[start] = 0;
        for(int i=0; i<V; i++){
            for(Edge e: allEdge){
                int u = e.getLeftVertex(), v = e.getRightVertex();
                long l = e.getLength();
                if(distance[v] > distance[u] + l) distance[v] = distance[u] + l;
            }
        }

        for(Edge e: allEdge){
            int u = e.getLeftVertex(), v = e.getRightVertex();
            long l = e.getLength();
            if(distance[v] > distance[u] + l) throw new Exception("Bellman-Ford algorithm: negative-weight cycle found");
        }

        return distance;
    }
    public long[][] warshallfloyd(){
        long[][] ans = new long[V][V];
        for(int i=0; i<V; i++)for(int j=0; j<V; j++){
            ans[i][j] = INF;
        }
        for(Edge e: allEdge){
            ans[e.getLeftVertex()][e.getRightVertex()] = e.getLength();
            ans[e.getRightVertex()][e.getLeftVertex()] = e.getLength();
        }

        for(int k=0; k<V; k++)for(int i=0; i<V; i++)for(int j=0; j<V; j++){
            ans[i][j] = Math.min(ans[i][j], ans[i][k]+ans[k][j]);
        }
        return ans;
    }

    public UndirectedGraph kruskal() throws Exception{
        UndirectedGraph ans = new UndirectedGraph(V);

        java.util.Collections.sort(allEdge);

        DSU uf = new DSU(V);
        for(Edge e:allEdge){
            if(!uf.same(e.getLeftVertex(), e.getRightVertex())){
                uf.merge(e.getLeftVertex(), e.getRightVertex());
                ans.addEdge(e.getLeftVertex(), e.getRightVertex(), e.getLength());
            }
        }

        if(uf.groups().size() > 1) throw new Exception("kruskal algorithm: the graph is disconnected");
        return ans;
    }

    public String toString(){return allEdge.toString();}
}
