package com.example.dbasy.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Column {
    public final Table parent;
    public final String name;
    public final List<Key> keys = new ArrayList<>();

    public Column(Table parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public Column(Table parent, String name, List<Key> keys) {
        this.parent = parent;
        this.name = name;
        this.keys.addAll(keys);
    }

    public boolean isPrimaryKey() {
        for(Key k : keys) {
            if(k.type.equals(Key.Type.PRIMARY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return name.equals(column.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
