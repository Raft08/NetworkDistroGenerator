package fr.atlasworld.generator.config;

import com.google.gson.JsonObject;

public record ServerConfigFile(String id, String version, String image, Location location, Node node, Resources resources,
                               JsonObject variables) {
    public record Location(int nest, int egg) {
    }

    public record Node(int[] nodes, String balancer) {
    }

    public record Resources(int allocations, int memory, int swap, int cpu, int disk) {
    }

    public static ServerConfigFile create(String id) {
        return new ServerConfigFile(
                id,
                "1.0.0",
                "ghcr.io/software-noob/pterodactyl-images",
                new Location(
                        1,
                        1
                ),
                new Node(
                        new int[] {1, 2, 3},
                        "round_robin"
                ),
                new Resources(
                        1,
                        1024,
                        1024,
                        100,
                        1024
                ),
                new JsonObject()
        );
    }
}
