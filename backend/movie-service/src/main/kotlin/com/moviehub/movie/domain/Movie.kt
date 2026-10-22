package com.moviehub.movie.domain

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.util.UUID

/**
 * Entity representing a movie in movie_db with associated cast/creator IDs.
 */
@Entity
@Table(name = "movies")
class Movie(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    var title: String,

    @Column(name = "artwork_url")
    var artworkUrl: String? = null,

    @Column(name = "release_year")
    var releaseYear: Int? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "movie_people",
        joinColumns = [JoinColumn(name = "movie_id")]
    )
    @Column(name = "ppl_id")
    var pplIds: MutableSet<UUID> = mutableSetOf()
) {
    var castIds: MutableSet<UUID>
        get() = pplIds
        set(value) {
            pplIds = value
        }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Movie) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String =
        "Movie(id=$id, title='$title', releaseYear=$releaseYear, artworkUrl=$artworkUrl, pplIds=$pplIds)"
}
