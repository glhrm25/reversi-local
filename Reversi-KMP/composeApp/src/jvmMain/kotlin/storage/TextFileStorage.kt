package storage

import okio.*
import okio.Path.Companion.toPath

// Okio -> Library for file handling
// We use this because it works independently of the platform where our class is used.

class TextFileStorage<Key,Data>(
    baseNameDirectory: String,
    private val serializer: Serializer<Data>,
) : Storage<Key,Data>{
    private val fs = FileSystem.SYSTEM
    private val basePath: Path = baseNameDirectory.toPath()

    private fun Path.exists() = fs.exists(this)
    private fun Path.delete() = fs.delete(this)
    private fun Path.createDirectory() = fs.createDirectories(this)
    private fun Path.isDirectory() = fs.metadata(this).isDirectory
    private fun Path.writeText(data: Data) {
        fs.write(this) { writeUtf8(serializer.serialize(data)) }
    }
    private fun Path.readText(): Data =
        fs.read(this) { serializer.deserialize(readUtf8()) }

    init { with(basePath) { // Create base folder if it does not exist
        if (!exists()) createDirectory()
        else check(isDirectory()) { "$name is not a directory" }
    } }

    // Apply a function to the path of the file for the given key.
    private inline fun <R> withPath(key: Key, fx: Path.() -> R): R =
        (basePath / "$key.txt").fx()

    override fun create(k: Key, data: Data) =
        withPath(k) {
            check(!exists()) { "File $k already exists" }
            writeText(data)
        }

    override fun read(k: Key): Data? =
        withPath(k) {
            if (exists()) readText() else null
        }

    override fun update(k: Key, data: Data) =
        withPath(k) {
            check(exists()) {"File $k does not exist" }
            writeText(data)
        }

    override fun delete(k: Key) =
        withPath(k) {
            check(exists()) {"File $k does not exist" }
            delete()
        }
}