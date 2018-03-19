package com.hackathon.pesttracker

import android.os.Parcel
import android.os.Parcelable

data class PhotoLocation(
        val lat: Double,
        val long: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readDouble(),
            parcel.readDouble())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(lat)
        parcel.writeDouble(long)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhotoLocation> {
        override fun createFromParcel(parcel: Parcel): PhotoLocation {
            return PhotoLocation(parcel)
        }

        override fun newArray(size: Int): Array<PhotoLocation?> {
            return arrayOfNulls(size)
        }
    }
}