package org.ripple.power.database;

import org.mapdb.BTreeMap;

import java.util.Iterator;
import static java.util.Map.Entry;
import java.util.Set;

public class DBMapCollection<Key, Value>
        implements Iterable<Entry<Key, Value>>, Iterator<Entry<Key, Value>> {

    protected DBMapDatabase database;
    protected BTreeMap<Key, Value> treeMap;
    protected Set<Entry<Key, Value>> entrySet;
    protected Iterator<Entry<Key, Value>> iterator;

    public DBMapCollection(DBMapDatabase database, BTreeMap<Key, Value> treeMap) {
        this.database = database;
        this.treeMap = treeMap;
    }

    public void put(Key key, Value object) {
        treeMap.put(key, object);
    }

    public Value get(Key key) {
        return treeMap.get(key);
    }

    public Value remove(Key key) {
        return treeMap.remove(key);
    }

    public boolean containsKey(Key key) {
        return treeMap.containsKey(key);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

	@Override
    public Entry<Key, Value> next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Entry<Key, Value>> iterator() {
        entrySet = treeMap.entrySet();
        iterator = entrySet.iterator();
        return this;
    }
}
