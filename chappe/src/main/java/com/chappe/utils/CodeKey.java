package com.chappe.utils;

public class CodeKey {
    public int seite;
    public int position;

    public CodeKey(int s, int p) {
        this.seite = s;
        this.position = p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CodeKey key = (CodeKey) o;
        return seite == key.seite && position == key.position;
    }

    @Override
    public int hashCode() {
        return 31 * seite + position;
    }
}
