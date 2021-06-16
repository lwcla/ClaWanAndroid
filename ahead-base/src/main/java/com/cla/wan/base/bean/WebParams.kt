package com.cla.wan.base.bean

import android.os.Parcel
import android.os.Parcelable

data class WebParams(val url: String? = "", val title: String? = "") : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WebParams> {
        override fun createFromParcel(parcel: Parcel): WebParams {
            return WebParams(parcel)
        }

        override fun newArray(size: Int): Array<WebParams?> {
            return arrayOfNulls(size)
        }
    }
}
