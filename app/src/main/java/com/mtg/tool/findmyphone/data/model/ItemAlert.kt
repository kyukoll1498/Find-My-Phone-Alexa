package com.mtg.tool.findmyphone.data.model

import java.io.Serializable

data class ItemAlert(
    var imageRaw: Int,
    var image: Int,
    var imageButton: Int,
    var colorText: Int,
    var isSelected: Boolean = false
) : Serializable