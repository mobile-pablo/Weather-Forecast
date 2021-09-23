package com.company.elverano

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.decodeStream


fun readAsset(context: Context, fileName: String): Bitmap =
    context
        .assets
        .open(fileName)
        .use(BitmapFactory::decodeStream)


