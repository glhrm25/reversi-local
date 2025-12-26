package model

@JvmInline
value class Name(private val name: String) { // Since we have just one constructor, it could be a value class
    init {
        require(isValid(name)) {"Invalid name $name"}
    }

    override fun toString() = name

    // Call isValid name before creating the object name:
    companion object {
        fun isValid(name: String) = name.isNotBlank() && name.all { it.isLetterOrDigit() }
    }
}