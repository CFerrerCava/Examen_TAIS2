package com.example.loginmaterialg1.Model

data class Product(
    var id: String,
    var photo: String,
    var description: String,
    var category: String,
    var weight: String,
    var stock:String
)
class _Product
{
    var id: String = ""
    var photo: String= ""
    var description: String= ""
    var category: String= ""
    var weight: String= ""
    var stock:String= ""
    fun _Product(){

    }
}
class Categories
{
    var id: String = ""
    var description: String= ""
    fun Categories(){

    }
}