package storage


    interface Storage<Key,Data> {
        // CRUD Operations // CRUD -> "Create Read Update Delete" (Basic operations for everything that works with data storage)
        /**
         * Creates a new entry in the storage.
         * @param k the key of the entry.
         * @param data the data to store.
         * @throws IllegalStateException if the key already exists.
         */
        fun create(k: Key, data: Data)
        /**
         * Reads an entry from the storage.
         * @param k the key of the entry.
         * @return the data stored or null if the key does not exist.
         */
        fun read(k: Key): Data?
        /**
         * Updates an entry in the storage.
         * @param k the key of the entry.
         * @param data the new data to store.
         * @throws IllegalStateException if the key does not exist.
         */
        fun update(k: Key, data: Data)
        /**
         * Deletes an entry from the storage.
         * @param k the key of the entry.
         * @throws IllegalStateException if the key does not exist.
         */
        fun delete(k: Key)
    }

// val value: T
// val s: Serializer<T>
// s.serialize(value): String
// s.deserialize(s.serialize(value)): value
interface Serializer<Data>{
    fun serialize(data: Data): String
    fun deserialize(text: String): Data
}