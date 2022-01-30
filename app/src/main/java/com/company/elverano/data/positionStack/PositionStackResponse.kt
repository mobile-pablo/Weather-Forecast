package com.company.elverano.data.positionStack

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "position_stack_response")
data class PositionStackResponse(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerializedName("data") val data: List<PositionStack>? = null
)
