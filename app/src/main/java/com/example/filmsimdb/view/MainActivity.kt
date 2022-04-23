package com.example.filmsimdb.view

import android.R
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmsimdb.adapter.FilmListAdapter
import com.example.filmsimdb.databinding.ActivityMainBinding
import com.example.filmsimdb.model.Film
import com.example.filmsimdb.viewmodel.FilmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var job: Job? = null


    lateinit var filmViewModel: FilmViewModel

    // Views
    private lateinit var searchView: SearchView
    private lateinit var filmsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMsg : TextView

    private lateinit var filmAdapter: FilmListAdapter

    private lateinit var filmList: ArrayList<Film>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        filmsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {

                filmAdapter.filter.filter(p0)
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
        })

        observeData()

    }

    private fun observeData() {

        filmViewModel.filmsLiveData.observe(this, Observer {

            if (it.isNotEmpty()) {
                filmsRecyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.INVISIBLE
                errorMsg.visibility = View.INVISIBLE
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

        filmViewModel.isloadingLiveData.observe(this, Observer {

            if (it){
                errorMsg.visibility =View.INVISIBLE
                progressBar.visibility = View.VISIBLE
            }else{
                progressBar.visibility = View.INVISIBLE
            }
        })
    }

    private fun init() {

        errorMsg = binding.mainPageErrorMsgTW
        progressBar = binding.mainPagePB
        searchView = binding.searchView
        filmsRecyclerView = binding.filmsRecyclerView
        filmViewModel = FilmViewModel()

        job = CoroutineScope(Dispatchers.IO).launch {

            // filmList = filmViewModel.getScrapedFilmList()
            filmList = filmViewModel.getFilmList()

            withContext(Dispatchers.Main) {

                filmAdapter = FilmListAdapter(filmList, applicationContext)
                filmsRecyclerView.adapter = filmAdapter

            }
        }
    }

    companion object {
        private const val  ERROR_MSG = "No Results Found"
        private const val  NO_RESULT_MSG = ""
    }
}