package com.example.filmsimdb.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.filmsimdb.R
import com.example.filmsimdb.model.Film
import com.example.filmsimdb.view.FilmPageActivity

class FilmListAdapter(
    private val films: ArrayList<Film>,
    private val context: Context
) : RecyclerView.Adapter<FilmListAdapter.ViewHolder>(), Filterable {

    private var filteredList = films

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val img: ImageView = view.findViewById(R.id.filmItemImg)
        val order: TextView = view.findViewById(R.id.filmItemOrder)
        val name: TextView = view.findViewById(R.id.filmItemName)
        val year: TextView = view.findViewById(R.id.filmItemYear)
        val rating: TextView = view.findViewById(R.id.filmItemRating)

        val filmItem: LinearLayout = view.findViewById(R.id.filmItemView)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.film_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val film = filteredList[position]

        Glide
            .with(context)
            .load(film.img)
            .placeholder(R.drawable.loading_spinner)
            .into(holder.img)

        holder.order.text = film.order.toString()
        holder.name.text = film.name
        holder.year.text = film.year
        holder.rating.text = film.rating

        holder.filmItem.setOnClickListener {

            val intent = Intent(context, FilmPageActivity::class.java).apply {
                putExtra("link", film.link)
                putExtra("order", film.order)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charSearch = p0.toString().filter { !it.isWhitespace() }

                if (charSearch.isEmpty()) {
                    filteredList = films

                } else {
                    val resultList = ArrayList<Film>()

                    for (item in films) {
                        if (item.name.lowercase().filter { !it.isWhitespace() }
                                .contains(charSearch.lowercase())) {
                            resultList.add(item)
                        }
                    }

                    filteredList = resultList
                }

                val filterResults = FilterResults()
                filterResults.values = filteredList

                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }

}