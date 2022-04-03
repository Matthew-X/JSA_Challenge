package com.example.jsa_challange

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

// Repo class that allows to store several different values in one Variable

@JsonIgnoreProperties(ignoreUnknown = true)
class Repo {
    @JsonProperty("name")
    var name:String = ""
    @JsonProperty("description")
    var description:String = ""
    @JsonProperty("html_url")
    var html_url:String = ""
}