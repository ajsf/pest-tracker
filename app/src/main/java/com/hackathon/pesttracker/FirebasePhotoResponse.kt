package com.hackathon.pesttracker

import android.os.Parcel
import android.os.Parcelable

data class PhotoResponseList(
        val list: List<FirebasePhotoResponse> = listOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(FirebasePhotoResponse)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(list)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PhotoResponseList> {
        override fun createFromParcel(parcel: Parcel): PhotoResponseList {
            return PhotoResponseList(parcel)
        }

        override fun newArray(size: Int): Array<PhotoResponseList?> {
            return arrayOfNulls(size)
        }
    }
}

data class FirebasePhotoResponse(
        val location: PhotoLocation,
        val path: String,
        val thumbnail: String,
        val fbId: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(PhotoLocation::class.java.classLoader),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(location, flags)
        parcel.writeString(path)
        parcel.writeString(thumbnail)
        parcel.writeString(fbId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FirebasePhotoResponse> {
        override fun createFromParcel(parcel: Parcel): FirebasePhotoResponse {
            return FirebasePhotoResponse(parcel)
        }

        override fun newArray(size: Int): Array<FirebasePhotoResponse?> {
            return arrayOfNulls(size)
        }
    }
}