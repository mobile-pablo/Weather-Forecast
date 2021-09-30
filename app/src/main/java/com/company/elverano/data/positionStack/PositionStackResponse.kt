package com.company.elverano.data.positionStack

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "position_stack_response")
data class PositionStackResponse(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val data: ArrayList<PositionStack>
)
