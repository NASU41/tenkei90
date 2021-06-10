class DirectedGraph{
    private static long INF = 1L << 60;
    
    private class Edge implements Comparable<Edge>{
        int from, to;
        long length;
        public Edge(int from, int to, long len){
            this.from = from;
            this.to = to;
            this.length = len;
        }
        public int getFrom(){return from;}
        public int getTo(){return to;}
        public long getLength(){return length;}

        public int compareTo(Edge o){
            return java.util.Comparator.comparingLong(Edge::getLength)
                   .thenComparingInt(Edge::getFrom)
                   .thenComparingInt(Edge::getTo).compare(this, o);
        }
        public String toString(){
            return String.format("[%d->%d, %d]", from,to, length);
        }
    }

    private int V;
    private int E;
    private java.util.List<Edge>[] in, out;
    private java.util.List<Edge> allEdge;

    public DirectedGraph(int V){
        this.V = V;
        this.E = 0;
        in = new java.util.ArrayList[V];
        out = new java.util.ArrayList[V];
        for(int v=0; v<V; v++) in[v] = new java.util.ArrayList<>();
        for(int v=0; v<V; v++) out[v] = new java.util.ArrayList<>();
        allEdge = new java.util.ArrayList<>();
    }

    public void addEdge(int from, int to, long length){
        Edge e = new Edge(from, to, length);
        out[from].add(e);
        in[to].add(e);
        allEdge.add(e);
        E++;
    }
    public void addEdge(int from, int to){
        addEdge(from, to, 1);
    }

    public java.util.List<Edge> outEdges(int v){
        return out[v];
    }
    public java.util.List<Integer> outNeighbours(int v){
        java.util.ArrayList<Integer> ans = new java.util.ArrayList<>();
        for(Edge e: outEdges(v)) ans.add(e.getTo());
        return ans;
    }

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
            for(Edge e: outEdges(v)){
                if(d + e.getLength() < ans[e.getTo()]){
                    queue.add(new Pair<>(d + e.getLength(), e.getTo()));
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
                int u = e.getFrom(), v = e.getTo();
                long l = e.getLength();
                if(distance[v] > distance[u] + l) distance[v] = distance[u] + l;
            }
        }

        for(Edge e: allEdge){
            int u = e.getFrom(), v = e.getTo();
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
        for(Edge e: allEdge) ans[e.getFrom()][e.getTo()] = e.getLength();

        for(int k=0; k<V; k++)for(int i=0; i<V; i++)for(int j=0; j<V; j++){
            ans[i][j] = Math.min(ans[i][j], ans[i][k]+ans[k][j]);
        }
        return ans;
    }
    public String toString(){return java.util.Arrays.toString(out);}
}
