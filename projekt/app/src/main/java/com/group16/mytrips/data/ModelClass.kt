package com.group16.mytrips.data

import com.group16.mytrips.R

class ModelClass {
    val defaultSightList = mutableListOf<DefaultSight>()
    val sightList = mutableListOf<Sight>()
    var sightNumber = 0
        private set


    val listOfAvatars = listOf(R.drawable.ic_av_cherry2,R.drawable.ic_av_fries,
        R.drawable.ic_av_ironman,R.drawable.ic_av_mickey,R.drawable.ic_av_naruto,
        R.drawable.ic_av_pikachu,R.drawable.ic_av_strawberry,R.drawable.ic_av_taco,
        R.drawable.ic_dummyprofilepic, R.drawable.ic_dummylocationpic
    )

    init {
        defaultSightList.add(
            DefaultSight(
                sightNumber,
                R.drawable.im_seven, R.drawable.im_seven_thumbnail,
                "SEVEN Kinocenter Gummersbach",
                51.0244537,
                7.5645057
            )
        )
        sightNumber++
        defaultSightList.add(
            DefaultSight(
                sightNumber,
                R.drawable.im_campusgm, R.drawable.im_campusgm_thumbnail,
                "TH Köln – Campus Gummersbach",
                51.0230899,
                7.5620239
            )
        )
        sightNumber++
        defaultSightList.add(
            DefaultSight(
                sightNumber,
                R.drawable.im_campusbib, R.drawable.im_campusbib_thumbnail,
                "Campusbibliothek Gummersbach TH Köln",
                51.0236436,
                7.5633091
            )
        )
        sightNumber++
        defaultSightList.add(
            DefaultSight(
                sightNumber,
                R.drawable.im_kunstwerk, R.drawable.im_kunstwerk_thumbnail,
                "Kunstwerk",
                51.0245796,
                7.5650897
            )
        )
        sightNumber++
        defaultSightList.add(
            DefaultSight(
                sightNumber,
                R.drawable.im_beach51,R.drawable.im_beach51_thumbnail,
                "Beach51",
                51.0228596,
                7.5650471
            )
        )
        sightNumber++
        defaultSightList.add(
            DefaultSight(
                sightNumber,
                R.drawable.im_schwalbearena, R.drawable.im_schwalbearena_thumbnail,
                "SCHWALBE arena",
                51.0248248,
                7.5626268
            )
        )
        sightNumber++
        defaultSightList.add(
            DefaultSight(
                sightNumber,
                R.drawable.im_halle32, R.drawable.im_halle32_thumbnail,
                "Halle 32",
                51.0251882,
                7.5637061
            )
        )
        sightNumber++
        defaultSightList.add(
            DefaultSight(
                sightNumber,
                R.drawable.im_steinmuellerhotel, R.drawable.im_steinmuellerhotel_thumbnail,
                "Das Steinmüller Hotel",
                51.0230970,
                7.5643766
            )
        )
        sightNumber++
        defaultSightList.add(
            DefaultSight(
                sightNumber,
                R.drawable.im_polizei,R.drawable.im_polizei_thumbnail,
                "Der Landrat als Kreispolizeibehörde Oberbergischer Kreis",
                51.0211180,
                7.5620283
            )
        )
        sightNumber++
        for (sight in defaultSightList) sightList.add(Sight(sight.sightId, sight.defualtPicture, sight.pictureThumbnail, date = "15.05.2023", latitude = sight.latitude, longitude = sight.longitude, sightName = sight.sightName ))
    }


}