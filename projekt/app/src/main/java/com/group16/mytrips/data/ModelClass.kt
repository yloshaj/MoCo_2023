package com.group16.mytrips.data

import com.group16.mytrips.R

class ModelClass {
    val defaultSightList = mutableListOf<DefaultSight>()
    val sightList = mutableListOf<Sight>()
    var sightNumber = 0
        private set

    init {
        defaultSightList.add(DefaultSight(0, R.drawable.ic_dummylocationpic, "SEVEN Kino", 51.0230345, 7.5654156))
        sightNumber++
        defaultSightList.add(DefaultSight(1, R.drawable.ic_dummylocationpic, "SEVEN Kino", 51.0243073, 7.5662209))
        sightNumber++
        defaultSightList.add(DefaultSight(2, R.drawable.ic_dummylocationpic, "SEVEN Kino", 51.0230345, 7.5654156))
        sightNumber++
        defaultSightList.add(DefaultSight(3, R.drawable.ic_dummylocationpic, "SEVEN Kino", 51.0250345, 7.5654156))
        sightNumber++
        defaultSightList.add(DefaultSight(4, R.drawable.ic_dummylocationpic, "SEVEN Kino", 51.0230345, 7.5754156))
        sightNumber++
        defaultSightList.add(DefaultSight(5, R.drawable.ic_dummylocationpic, "SEVEN Kino", 51.0236345, 7.5674156))
        sightNumber++
    }


}