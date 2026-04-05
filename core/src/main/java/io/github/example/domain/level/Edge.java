package io.github.example.domain.level;

import io.github.example.domain.service.GameConfig;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.example.domain.level.Level.*;


public class Edge {
    public Map<Integer, List<Integer>> graph;
//    public GameConfig gameConfig;
    int roomsInHeight;
    int roomsInWidth;
    public Edge(int roomsInHeight, int roomsInWidth) {
            this.roomsInHeight = roomsInHeight;
            this.roomsInWidth = roomsInWidth;
        graph = new HashMap<Integer, List<Integer>>();
        for (int i = 0; i < roomsInHeight*roomsInWidth; i++) {

            if ((i + 1) % roomsInWidth != 0) {
                addEdge(i, i + 1);
            }
            // сосед снизу
            if (i + roomsInWidth < roomsInHeight * roomsInWidth) {
                addEdge(i, i + roomsInWidth);
            }
        }






    }
    public void addEdge(int u, int v) {
        graph.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
        graph.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
    }

    public void removeRandEdge() {
        int count = ThreadLocalRandom.current().nextInt(0, (roomsInHeight-1)*(roomsInWidth)+(roomsInHeight)*(roomsInWidth-1) - ((roomsInHeight*roomsInWidth)-1));
        for (int i = 0; i < count; i++) {
            int u = ThreadLocalRandom.current().nextInt(0, roomsInHeight*roomsInWidth);
            int v = ThreadLocalRandom.current().nextInt(0, graph.get(u).size());
            System.out.println(u + " " + v);
            while (checkConnectedGraph(u, v) == false) {
                u = ThreadLocalRandom.current().nextInt(0, roomsInHeight*roomsInWidth);
                v = ThreadLocalRandom.current().nextInt(0, graph.get(u).size());
            }
            graph.get(graph.get(u).get(v)).remove(graph.get(graph.get(u).get(v)).indexOf(u));
            graph.get(u).remove(v);

        }
    }

    public boolean checkConnectedGraph(int u, int v) {
        Map<Integer, List<Integer>> copygraph = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : graph.entrySet()) {
            copygraph.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        copygraph.get(copygraph.get(u).get(v)).remove(copygraph.get(copygraph.get(u).get(v)).indexOf(u));
        copygraph.get(u).remove(v);
            int start = copygraph.keySet().iterator().next();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            int current = queue.poll();
            for(int neighbor : copygraph.get(current)) {
                if(!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return visited.size() == copygraph.keySet().size();
    }
}
