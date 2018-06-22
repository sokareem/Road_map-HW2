import sun.security.provider.certpath.Vertex;
//By Sinmisola Kareem o

import javax.json.Json;
import javax.json.JsonObject;
import java.io.*;
import javax.json.JsonValue;
import java.io.IOException;
import java.util.Scanner;
import java.net.URL;
import java.util.NavigableSet;
import java.util.HashMap;
import java.util.TreeSet;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class Edge{
    int edgeID;
    int startNodeId;
    int endNodeId;
    float l2Distance;
}
class Nodes{
    int nodeID;
}


public class Main {

    Edge edge = new Edge();
    Nodes node = new Nodes();
    List<Edge> edges = new ArrayList<Edge>();
    List<Nodes> nodes = new ArrayList<Nodes>();

    public static void read_in(Edge edge,List<Edge> edges, List<Nodes> nodes, Nodes node){
        try {
            URL edgesUrl = new URL("https://gist.githubusercontent.com/BenjaminMalley/9eadf45dbe11ba9c3ac34c45f905cfe8/raw/2c363711b601fa39a5d0071f10158b86217e530f/edges.json");
            URL nodesUrl = new URL("https://gist.githubusercontent.com/BenjaminMalley/9eadf45dbe11ba9c3ac34c45f905cfe8/raw/2c363711b601fa39a5d0071f10158b86217e530f/nodes.json");
            URLConnection conn = edgesUrl.openConnection();
            URLConnection conn1 = nodesUrl.openConnection();
            JsonObject edgesObject = Json.createReader(conn.getInputStream()).readObject();
            JsonObject nodesObject = Json.createReader(conn1.getInputStream()).readObject();
            for (Map.Entry<String, JsonValue> entry : edgesObject.entrySet()) {
                JsonObject edgeObject = entry.getValue().asJsonObject();
                edge.edgeID = edgeObject.getInt("EdgeId");
                edge.startNodeId = edgeObject.getInt("  node.nodeID = nodesObject.getInt(\"NodeID\");StartNodeId");
                edge.endNodeId = edgeObject.getInt("EndNodeId");
                edge.l2Distance = edgeObject.getJsonNumber("L2Distance").numberValue().floatValue();
                edges.add(edge);
                nodes.add(node);
            }
            //---------------------------- code goes here!! ------------------/


        } catch (IOException e) {

        }
    }

    public  class Dijkstra {
        private static final int START = 12;
        private  static final int END = 60;
        public static void generate(Map<String, Edge> Graph_E){
            final Graph.Edge[] GRAPH = new Graph.Edge[Graph_E.size()];
            int i = 0;

            for (Edge edge : Graph_E.values()){
                // we want to insert graph into Graph of Edges(Type) : new Graph.Edge is (2, 6, 0.03442),
                GRAPH[i] = new Graph.Edge(edge.startNodeId, edge.endNodeId, edge.l2Distance
                );
                i++;
            }
            Graph route = new Graph(GRAPH); // replaces graph with array of edges

            route.dijkstra(START);
            route.printRoute(END);
            route.printAllRoutes();
        }
    }

    class Graph {
        private final Map<Integer, Vertex> graph; // map of Node ids to node objects, derived from Edge Map

        //Edge struct
        public static class Edge {
            public final int start_edge, end_edge;
            float distance;

            public Edge(int start_edge, int end_edge, float distance) {
                this.start_edge = start_edge;
                this.end_edge = end_edge;
                this.distance = distance;
            }
        }


        //vertex Struct
        public class Vertex implements Comparable<Vertex>{
            public final int name;
            public float distance = Integer.MAX_VALUE; // assumed to store infinity value
            public Vertex previous = null;
            public final Map<Vertex, Float> neighbours = new HashMap<>(); // this should store vertex info and distance as value.

            public Vertex(int name) {
                this.name = name;
            } // Constructor

            private void printRoute() {
                if (this == this.previous) {
                    System.out.printf("%s", this.name);
                } else if (this.previous == null) {
                    System.out.printf("%s (unreachable)", this.name);
                } // if node is unreachable
                else {
                    this.previous.printRoute();
                    System.out.printf(" -> %s(%f)", this.name, this.distance);
                }
            }

            public int comparison(Vertex other) { // comparison operator
                if (distance == other.distance)
                    return Integer.compare(this.name, other.name);

                return Float.compare(distance, other.distance);
            }

            @Override
            public String toString() {
                return "(" + name + ", " + distance + ")";
            }
        }


        /**
         * Building a graph from a set of edges
         */
        public Graph(Edge[] edges) {
            graph = new HashMap<>(edges.length);

            for (Edge e : edges) {
                if (e == null) {
                    System.out.println("NULL edges");

                    if (!graph.containsKey(e.start_edge)) {
                        //if graph doesnt contain the start edge add it to the graph
                        graph.put(e.start_edge, new Vertex(e.start_edge));
                    }
                    if (!graph.containsKey(e.end_edge)) {
                        //if graph doesnt contain the end edge add it to the graph
                        graph.put(e.end_edge, new Vertex(e.end_edge));
                    }

                    // now we put in the neighbouring vertices
                    for (Edge e1 : edges) {
                        graph.get(e1.start_edge).neighbours.put(graph.get(e1.end_edge), e1.distance);
                    }
                }
            }
        }
            /** Run dijstra depending on given start vertex */
            public void dijkstra ( int startId){
                if (!graph.containsKey(startId)) {
                    System.err.printf("Graph doesnt contain start vertex \"%s\n", startId);
                    return;
                }
                // if start ID does exist then...
                final Vertex source = graph.get(startId);
                NavigableSet<Vertex> p = new TreeSet<>();

                //build vertices
                for (Vertex v: graph.values()) {
                    v.previous = v == source ? source : null;
                    v.distance = v == source ? 0 : Integer.MAX_VALUE;
                    p.add(v);
                }
                dijkstra(p);
            }

            // IMPLEMENTATION OF DIJKSTRAS ALGORITHM USING A BINARY HEAP.
            private void dijkstra ( final NavigableSet<Vertex> p){
                // setting up vertices
                Vertex u, v;
                while (!p.isEmpty()) {
                    u = p.pollFirst(); // Node with the shortest distance
                    if (u.distance == Integer.MAX_VALUE) break; // this ignores unreachable nodes

                    //distances to each neighbouring Node
                    for (Map.Entry<Vertex, Float> a : u.neighbours.entrySet()) {
                        v = a.getKey(); // neighbour

                        final float currDistance = u.distance + a.getValue();
                        if (currDistance < v.distance) {
                            p.remove(v);
                            v.distance = currDistance;
                            v.previous = u;
                            p.add(v);
                        }
                    }

                }
            }

            /** Prints path from startNode to endNodeId */
            public void printRoute(int endNodeID){
                if (!graph.containsKey(endNodeID)) {
                    System.err.printf("Graph doesnt exist in endNode \"%s\"\n", endNodeID);
                    return;
                }
                graph.get(endNodeID).printRoute();
                System.out.println();
            }



        /**
         * Print Path from startNode to every other node
         */
        public void printAllRoutes() {
            for (Vertex v : graph.values()) {
                v.printRoute();
                System.out.println();
            }
        }
    }


    public static void main(String[] args) {
        Edge edge = new Edge();
        Nodes node = new Nodes();
        List<Edge> edges = new ArrayList<Edge>();
        List<Nodes> nodes = new ArrayList<Nodes>();






    }
}


