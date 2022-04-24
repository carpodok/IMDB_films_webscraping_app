package com.example.filmsimdb.model

class Film(
    val img: String,
    val order: Int,
    val name: String,
    val year: String,
    val rating: String,
    val link: String,
) {


    constructor(
        img: String,
        order: Int,
        name: String,
        year: String,
        rating: String,
        plot: String,
        director: String,
        writers: String,
        stars: String,
    ) : this(img, order, name, year, rating, ""){
        this.plot = plot
        this.director = director
        this.writers = writers
        this.stars = stars
    }

    private var plot = ""
    private var director = ""
    private var writers = ""
    private var stars = ""

    fun getPlot() : String{

        return plot
    }

    fun getDirector() : String{

        return director
    }

    fun getWriters() : String{

        return writers
    }

    fun getStars() : String{

        return stars
    }

}