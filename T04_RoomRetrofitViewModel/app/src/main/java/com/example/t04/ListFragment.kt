package com.example.t04

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.Menu
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.t04.databinding.FragmentListBinding
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*


import android.view.MenuInflater
import androidx.fragment.app.activityViewModels


class ListFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private var string:String = ""
    private var string2:String = ""
    val adapter = MovieListAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val v = binding.root
        val recyclerView = binding.movieList

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val model = ViewModelProviders.of(this).get(MovieViewModel::class.java)

        model.allMovies.observe(
            viewLifecycleOwner,
            Observer<List<MovieItem>> { movies ->
                movies?.let { adapter.setMovies(it) }
            }
        )
        binding.refresh.setOnClickListener{
            model.refreshMovies(1)
        }
        binding.filter.setOnClickListener {
            string2 = binding.filter.getText().toString()
            if(string2.equals("FILTER: ALL")){
                binding.filter.setText("FILTER: LIKED")
                adapter.filterLike()

            }
            else{
                binding.filter.setText("FILTER: ALL")
                adapter.restore()
            }
        }
        binding.sort.setOnClickListener {
            string = binding.sort.getText().toString()
            if(string.equals("SORT BY: TITLE")){
                binding.sort.setText("SORT BY: RATING")
                adapter.sortByRating()
            }
            else{
                binding.sort.setText("SORT BY: TITLE")
                adapter.sortByTitle()
            }
        }
        return v
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu,menu)
        val item = menu?.findItem(R.id.search_movie)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.search(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean{
                adapter.restore()
                return true
            }

        })
    }
    inner class MovieListAdapter():
        RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>(){
        private var movies = emptyList<MovieItem>()
        private var moviesBackup = emptyList<MovieItem>()
        internal fun setMovies(movies: List<MovieItem>) {
            moviesBackup = movies
            this.movies = movies
            notifyDataSetChanged()
        }
        fun restore(){
            movies = moviesBackup
            notifyDataSetChanged()
        }
        override fun getItemCount(): Int {

            return movies.size
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {


            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view, parent, false)
            return MovieViewHolder(v)
        }

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {


            //holder.bindItems(movieList[position])
            val model: MovieViewModel by activityViewModels()
            Glide.with(this@ListFragment).load(resources.getString(R.string.picture_base_url)+movies[position].poster_path).apply( RequestOptions().override(128, 128)).into(holder.view.findViewById(R.id.poster))

            holder.view.findViewById<TextView>(R.id.title).text=movies[position].title

            holder.view.findViewById<TextView>(R.id.rating).text=movies[position].vote_average.toString()


            holder.itemView.setOnClickListener(){


                // interact with the item
                model.setMovie(movies[position])
                //Toast.makeText(context,movies[position].popularity.toString(), Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_listFragment_to_detailFragment, null)

            }

        }

        fun search(query: String?) {
            movies = movies.filter {it.title.contains(query!!)}
            notifyDataSetChanged()
        }
        fun filterLike(){
            movies = movies.filter { it.popularity == 1F }
            notifyDataSetChanged()
        }
        fun sortByTitle(){
            movies = movies.sortedBy { it.title }

            notifyDataSetChanged()
        }
        fun sortByRating(){
            movies = movies.sortedByDescending { it.vote_average }
            notifyDataSetChanged()
        }


        inner class MovieViewHolder(val view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
            override fun onClick(view: View?){

                if (view != null) {


                }


            }


        }
    }
}