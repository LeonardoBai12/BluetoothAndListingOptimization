package io.lb.bleandlistingopt.core.common

/** A value in flight from a data source to the screen: loading, arrived, or failed. */
sealed interface Resource<out T> {
    data object Loading : Resource<Nothing>
    data class Success<T>(val data: T) : Resource<T>
    data class Error(val message: String? = null) : Resource<Nothing>
}
