package com.example.filmsimdb.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.filmsimdb.R
import com.example.filmsimdb.databinding.ActivityFilmPageBinding
import com.example.filmsimdb.model.Film
import com.example.filmsimdb.viewmodel.FilmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class FilmPageActivity : AppCompatActivity() {

    private lateinit var FILM_LINK: String
    private var FILM_ORDER: Int = 0

    private lateinit var binding: ActivityFilmPageBinding

    private lateinit var filmViewModel: FilmViewModel
    private lateinit var currFilm: Film


    private var job: Job? = null

    //Views
    private lateinit var filmIW: ImageView
    private lateinit var filmNameTW: TextView
    private lateinit var filmDirectorTW: TextView
    private lateinit var filmWritersTW: TextView
    private lateinit var filmStarsTW: TextView
    private lateinit var filmDesTW: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMsg : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilmPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setInit()

        job = CoroutineScope(Dispatchers.IO).launch {

            // filmList = filmViewModel.getScrapedFilmList()
            currFilm = filmViewModel.getFilmList()[FILM_ORDER - 1]


            filmViewModel.scrapFilm()
            val directoryList = filmViewModel.getDirectories()
            val writerList = filmViewModel.getWriters()
            val starList = filmViewModel.getStars()
            val filmDes = filmViewModel.getFilmDes()

            var directories = "Director : "
            var writers = "Writes : "
            var stars = "Stars : "

            for (i in 0 until directoryList.size) {
                if (i != directoryList.size - 1) {
                    directories += directoryList[i] + ", "
                }
                directories += directoryList[i]
            }

            for (i in 0 until writerList.size) {
                if (i != directoryList.size - 1) {
                    writers += writerList[i] + ", "
                }
                writers += writerList[i]
            }

            for (i in 0 until starList.size) {
                if (i != directoryList.size - 1) {
                    stars += starList[i] + ", "
                }
                stars += starList[i]
            }

            withContext(Dispatchers.Main) {

                Glide
                    .with(applicationContext)
                    .load(currFilm.img)
                    .placeholder(R.drawable.loading_spinner)
                    .into(filmIW)

                filmDirectorTW.text = directories
                filmWritersTW.text = writers
                filmStarsTW.text = stars
                filmDesTW.text = filmDes

                filmNameTW.text = currFilm.name

            }
        }
        observeData()
    }

    private fun observeData() {

        filmViewModel.isloadingLiveData.observe(this, Observer {

            if (it) {
                progressBar.visibility = View.VISIBLE
                errorMsg.visibility =View.INVISIBLE

            } else {
                progressBar.visibility = View.INVISIBLE
            }

        })

        filmViewModel.errorLiveData.observe(this, Observer {

            if (it) {
                progressBar.visibility = View.INVISIBLE
                errorMsg.visibility =View.VISIBLE
            }else{
                errorMsg.visibility =View.INVISIBLE
            }
        })


    }

    private fun setInit() {

        FILM_LINK = intent.getStringExtra("link").toString()
        FILM_ORDER = intent.getIntExtra("order", 0)

        filmIW = binding.filmPageImg
        filmDirectorTW = binding.filmDirectorTextView
        filmWritersTW = binding.filmWritersTextView
        filmNameTW = binding.filmNameTW
        filmStarsTW = binding.filmStarsTextView
        filmDesTW = binding.filmDesTW
        progressBar = binding.filmPagePB
        errorMsg = binding.filmPageErrorMsgTW

        filmViewModel = FilmViewModel(FILM_LINK)


    }

}