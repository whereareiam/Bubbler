package me.whereareiam.socialismus.module.bubbler.api.model;

import com.github.retrooper.packetevents.util.Vector3f;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Vector {
    private float x;
    private float y;
    private float z;

    public Vector3f toVector3f() {
        return new Vector3f(x, y, z);
    }
}
