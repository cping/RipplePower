package org.ripple.power.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.ripple.power.config.LSystem;

public class DBMapDatabase {

	public static DBMapDatabase create(String name) {
		return new DBMapDatabase(LSystem.getDirectory() + "/" + name);
	}

	public static DBMapDatabase createFromFile(Path path) {
		return new DBMapDatabase(path.toFile());
	}

	public static DBMapDatabase createFromTempFile(String prefix, String suffix) {
		try {
			Path path = Files.createTempFile(prefix, suffix);
			return createFromFile(path);
		} catch (IOException e) {
			return null;
		}
	}

	private File _dbFile;
	private DB _db;

	@SuppressWarnings("rawtypes")
	private HashMap<String, DBMapCollection> collections;

	public DBMapDatabase(String file) {
		this(new File(file));
	}

	public DBMapDatabase(File file) {
		_dbFile = file;
		collections = new HashMap<>();
	}

	public void connect() {
		connect(false);
	}

	public void connect(boolean readOnly) {
		if (_db != null) {
			return;
		}
		DBMaker dbMaker;
		if (_dbFile != null) {
			dbMaker = DBMaker.newFileDB(_dbFile);
		} else {
			dbMaker = DBMaker.newMemoryDB();
		}
		if (readOnly) {
			dbMaker = dbMaker.readOnly();
		}
		_db = dbMaker.make();
	}

	public void close() {
		if (_db != null) {
			_db.close();
			_db = null;
		}
	}

	public DB getDB() {
		return _db;
	}

	@SuppressWarnings("unchecked")
	public <Key, Value> DBMapCollection<Key, Value> getCollection(
			String collectionName) {
		if (collections.get(collectionName) != null) {
			return collections.get(collectionName);
		}
		BTreeMap<Key, Value> treeMap = _db.getTreeMap(collectionName);
		DBMapCollection<Key, Value> collection = new DBMapCollection<Key, Value>(
				this, treeMap);
		collections.put(collectionName, collection);
		return collection;
	}

	public void commit() {
		_db.commit();
	}

	public void rollback() {
		_db.rollback();
	}
}
