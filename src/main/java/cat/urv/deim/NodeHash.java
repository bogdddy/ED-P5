package cat.urv.deim;

public class NodeHash<K, V> {

    public K key;
    public V value;
    public NodeHash<K, V> seguent;

    public NodeHash (K k, V v) {
        key = k;
        value = v;
        seguent = null;
    }

}
