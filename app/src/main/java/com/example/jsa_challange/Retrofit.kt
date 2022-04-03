package com.example.jsa_challange

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit class that retrieves data about user's repos from api.github.com and then puts them
// into list that I will be later use to display information on about Repos

class Retrofit(name:String) {
    var retrofit:Retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var service:GitHubService = retrofit.create(GitHubService::class.java)
    var repos: Call<List<Repo>> = service.listRepos(name)
}