package com.example.filmsimdb.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.filmsimdb.model.Film
import dagger.hilt.android.lifecycle.HiltViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import javax.inject.Inject

@HiltViewModel
class FilmViewModel
@Inject constructor() : ViewModel() {

    var filmsLiveData: MutableLiveData<ArrayList<Film>> = MutableLiveData()
    var errorLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    var isloadingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)

    var directoriesLiveData: MutableLiveData<String> = MutableLiveData()
    var writersLiveData: MutableLiveData<String> = MutableLiveData()
    var starsLiveData: MutableLiveData<String> = MutableLiveData()

    private var directories = ArrayList<String>()
    private var writers = ArrayList<String>()
    private var stars = ArrayList<String>()
    private var filmDes = ""

    private lateinit var filmFullImg: String

    private var filmURL: String = ""

    constructor(filmURL: String) : this() {

        this.filmURL = filmURL

    }

    fun getFilmList(): ArrayList<Film> {


        if (getScrapedFilmList().isEmpty()) {
            errorLiveData.postValue(true)
        } else {
            isloadingLiveData.postValue(false)
            filmsLiveData.postValue(getScrapedFilmList())
        }


        return getScrapedFilmList()
    }

    fun getDirectories(): ArrayList<String> {
        return directories

    }

    fun getWriters(): ArrayList<String> {
        return writers
    }

    fun getStars(): ArrayList<String> {
        return stars
    }

    fun getFilmDes(): String {

        return filmDes
    }

    private fun getScrapedFilmList(): ArrayList<Film> {

        isloadingLiveData.postValue(true)

        val scrapedList = ArrayList<Film>()

        val document: Document = Jsoup.connect(FILM_LIST_URL).get()

        val body: Elements = document.select("tbody.lister-list")

        var order = 0

        for (i in body.select("tr")) {
            order++
            val img = i.select("td.posterColumn img").attr("src")
            val name = i.select("td.titleColumn a").text()
            val link = LINK_PREFIX + i.select("td.titleColumn a").attr("href").toString()
            val year = i.select("td.titleColumn span.secondaryInfo").text()
            val rating = i.select("td.ratingColumn.imdbRating strong").text()

            scrapedList.add(Film(img, order, name, year, rating, link))

        }

        isloadingLiveData.postValue(false)

        return scrapedList
    }

    fun scrapFilm() {

        isloadingLiveData.postValue(true)

        val document: Document = Jsoup.connect(filmURL).get()

        val body: Elements = document.select("div.sc-fa02f843-0.fjLeDR")
        val body2: Elements =
            body.select("ul.ipc-metadata-list.ipc-metadata-list--dividers-all.title-pc-list.ipc-metadata-list--baseAlt")



        for ((idx, i) in body2.select("li.ipc-metadata-list__item").withIndex()) {

            for (j in i.select("a.ipc-metadata-list-item__list-content-item.ipc-metadata-list-item__list-content-item--link")) {
                when (idx) {
                    0 -> directories.add(j.text())
                    1 -> writers.add(j.text())
                    2 -> stars.add(j.text())
                }
            }
        }

        val des = document.select("span.sc-16ede01-1.kgphFu").text()

        isloadingLiveData.postValue(false)

        filmDes = des
    }

    companion object {
        private const val FILM_LIST_URL = "https://www.imdb.com/chart/top/"
        private const val LINK_PREFIX = "https://www.imdb.com"
    }
}
