package com.tigerobo.x.pai.api.entity;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: yicun.chen@tigerobo.com
 * @date: Created in 2021/1/4 9:14 PM
 * @description: 二元组-Pair, two elements group
 * @modified By:
 * @version: $
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pair<V1, V2> implements Serializable {
    private V1 v1;
    private V2 v2;

    public static <V1, V2> Pair<V1, V2> create(V1 v1, V2 v2) {
        return new Pair<>(v1, v2);
    }

    @Override
    public String toString() {
        return "[" + v1 + ", " + v2 + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equal(v1, pair.v1) && Objects.equal(v2, pair.v2);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(v1, v2);
    }
}
