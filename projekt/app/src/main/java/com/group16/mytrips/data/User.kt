package com.group16.mytrips.data

import com.group16.mytrips.R

data class User(
    val id: String = "",
    val name: String = "",
    var avatar: Int = R.drawable.ic_dummyprofilepic,
    var overallxp: Int = 0
)