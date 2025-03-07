package com.dimensiondelvers.dimensiondelvers.client.map;


import com.mojang.blaze3d.vertex.BufferBuilder;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static com.dimensiondelvers.dimensiondelvers.client.map.Utils3D.calculateVertices;
import static com.dimensiondelvers.dimensiondelvers.client.map.Utils3D.projectPoint;

/**
 * Represents a single cell in the map
 * Contains the position and type of the cell
 */
public class MapCell {
    public Vector3f pos1; // should only be used for rendering, TODO: convert the constructors to use x,y,z position
    private Vector3f pos2;
    // connection is 1wide tunnel between rooms
    public int connections = 0; // 0 - no connection, 1 - connection North, 2 - connection East, 3 - connection both
    public int openings = 0; // places where connection can happen, 1000 - North, 0100 - East, 0010 - South, 0001 - West
    int x, y, z;
    private int type;
    // TODO: move rendering over to MapRoom

    public MapCell(Vector3f loc, float size, int type) {
        this.pos1 = loc;
        this.pos2 = new Vector3f(loc.x + size, loc.y + size, loc.z + size);
        this.type = type;
    }

    public MapCell(Vector3f pos1, Vector3f pos2, int type) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public int getType() {
        return type;
    }

    /**
     * Adds the cube to the buffer for rendering, assumes DEBUG_LINES renderer is active
     * @param buffer
     * @param camera
     */
    public void renderWireframe(BufferBuilder buffer, com.dimensiondelvers.dimensiondelvers.client.map.VirtualCamera camera, Vector2i mapPosition, Vector2i mapSize) {
        float[][] vertices = calculateVertices(pos1, pos2);

        int[][] edges = {
                { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 },
                { 4, 5 }, { 5, 6 }, { 6, 7 }, { 7, 4 },
                { 0, 4 }, { 1, 5 }, { 2, 6 }, { 3, 7 }
        };

        int i = 0; // i is there to just color one edge so we can maintain direction better
        for (int[] edge : edges) {
            Vector4f start = new Vector4f(vertices[edge[0]][0], vertices[edge[0]][1], vertices[edge[0]][2], 1.0f);
            Vector4f end = new Vector4f(vertices[edge[1]][0], vertices[edge[1]][1], vertices[edge[1]][2], 1.0f);

            Vector3f sProj = projectPoint(new Vector3f(start.x, start.y, start.z), camera, mapPosition, mapSize);
            Vector3f eProj = projectPoint(new Vector3f(end.x, end.y, end.z), camera, mapPosition, mapSize);

            if (i==4) { // color one edge
                buffer.addVertex(sProj.x, sProj.y, sProj.z)
                        .setColor(1f, 0f, 0f, 1f);
                buffer.addVertex(eProj.x, eProj.y, eProj.z)
                        .setColor(1f, 0f, 0f, 1f);
            } else {
                buffer.addVertex(sProj.x, sProj.y, sProj.z)
                        .setColor(1f, 1f, 1f, 1f);
                buffer.addVertex(eProj.x, eProj.y, eProj.z)
                        .setColor(1f, 1f, 1f, 1f);
            }
            i++;
        }
    }

    /**
     * Adds the cube to the buffer for rendering, assumes QUADS renderer is active
     * @param buffer
     * @param camera
     */
    public void renderCube(BufferBuilder buffer, VirtualCamera camera, Vector4f color, Vector2i mapPosition, Vector2i mapSize) {
        float[][] vertices = calculateVertices(pos1, pos2);

        int[][] faces = {
                {0, 1, 2, 3}, // bottom
                {7, 6, 5, 4}, // top
                {4, 5, 1, 0}, // front
                {6, 7, 3, 2}, // back
                {0, 3, 7, 4}, // left
                {5, 6, 2, 1}  // right
        };

        for (int[] face : faces) {
            Vector4f v1 = new Vector4f(vertices[face[0]][0], vertices[face[0]][1], vertices[face[0]][2], 1.0f);
            Vector4f v2 = new Vector4f(vertices[face[1]][0], vertices[face[1]][1], vertices[face[1]][2], 1.0f);
            Vector4f v3 = new Vector4f(vertices[face[2]][0], vertices[face[2]][1], vertices[face[2]][2], 1.0f);
            Vector4f v4 = new Vector4f(vertices[face[3]][0], vertices[face[3]][1], vertices[face[3]][2], 1.0f);

            Vector3f p1 = projectPoint(new Vector3f(v1.x, v1.y, v1.z), camera, mapPosition, mapSize);
            Vector3f p2 = projectPoint(new Vector3f(v2.x, v2.y, v2.z), camera, mapPosition, mapSize);
            Vector3f p3 = projectPoint(new Vector3f(v3.x, v3.y, v3.z), camera, mapPosition, mapSize);
            Vector3f p4 = projectPoint(new Vector3f(v4.x, v4.y, v4.z), camera, mapPosition, mapSize);

            buffer.addVertex(p1.x, p1.y, p1.z).setColor(color.x, color.y, color.z, color.w);
            buffer.addVertex(p2.x, p2.y, p2.z).setColor(color.x, color.y, color.z, color.w);
            buffer.addVertex(p3.x, p3.y, p3.z).setColor(color.x, color.y, color.z, color.w);
            buffer.addVertex(p4.x, p4.y, p4.z).setColor(color.x, color.y, color.z, color.w);
        }
    }

}
