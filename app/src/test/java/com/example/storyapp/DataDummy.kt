package com.example.storyapp

import com.example.storyapp.data.local.entity.StoriesEntity

object DataDummy {

    fun generateDummyQuoteResponse(): List<StoriesEntity> {
        val items: MutableList<StoriesEntity> = arrayListOf()
        for (i in 0..100) {
            val quote = StoriesEntity(
                i.toString(),
                "https://i.pinimg.com/736x/9e/40/c7/9e40c73769a01ace4e360f239ff93513.jpg",
                "2023-05-07T13:12:47.992Z",
                "Dummy data $i",
                "Description dummy data $i",
                -6.2449424,
                106.7881851
            )
            items.add(quote)
        }
        return items
    }
}