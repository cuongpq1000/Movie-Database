package com.example.t04

import android.app.Application
import android.graphics.Movie
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MovieViewModel(application: Application): AndroidViewModel(application){

    private var parentJob = Job()
    private val currentMovie: ArrayList<MovieItem> = ArrayList()
    private val coroutineContext: CoroutineContext
        get() = parentJob+ Dispatchers.Main


    private val scope = CoroutineScope(coroutineContext)


    private var disposable: Disposable? = null


    private val repository: MovieItemRepository = MovieItemRepository(MovieRoomDatabase.getDatabase(application).movieDao())

    val allMovies: LiveData<List<MovieItem>>

    init {
        allMovies = repository.allMovies
    }


    fun refreshMovies(page: Int){

        //TODO: add your API key from themoviedb.org
        disposable =
            RetrofitService.create("https://api.themoviedb.org/3/").getNowPlaying("a9c01466954dff1e659b265e4d0b5c29",page).subscribeOn(
                Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                {result -> showResult(result)},
                {error -> showError(error)})
    }
    fun setMovie(movie: MovieItem) {
        if(currentMovie.size > 0)
        {
            currentMovie.clear()
        }
        currentMovie.add(movie)
    }
    fun getMovie(): MovieItem {
        return currentMovie.elementAt(0)
    }


    private fun showError(error: Throwable?) {

       //TODO: handle error


    }

    private fun showResult(movies: Movies?) {
        deleteAll()
        movies?.results?.forEach { movie ->
            insert(movie)
        }
    }

    private fun insert(movie: MovieItem) = scope.launch(Dispatchers.IO) {
        repository.insert(movie)
    }

    private fun deleteAll() = scope.launch (Dispatchers.IO){
        repository.deleteAll()
    }




}