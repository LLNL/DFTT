package llnl.gnem.core.util;

import java.io.Serializable;



public class PairT<X, Y> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final X first;
    private final Y second;

    public PairT(X first, Y second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PairT<X, Y> other = (PairT<X, Y>) obj;
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        return this.second == other.second || (this.second != null && this.second.equals(other.second));
    }

    public X getFirst() {
        return first;
    }

        public Y getSecond() {
            return second;
        }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 37 * hash + (this.second != null ? this.second.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "PairT{" + "first=" + first + ", second=" + second + '}';
    }

}
