package com.example.carolsnest.data

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class BirdData(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("description") @set:PropertyName("description") var description: String? = null,
    @get:PropertyName("imageUrls") @set:PropertyName("imageUrls") var imageUrls: List<String> = emptyList(),
    @get:PropertyName("timestamp") @set:PropertyName("timestamp") var timestamp: Long = 0L,
    @get:PropertyName("userId") @set:PropertyName("userId") var userId: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        description = parcel.readString(),
        imageUrls = parcel.createStringArrayList() ?: emptyList(),
        userId = parcel.readString() ?: ""
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(description)
        dest.writeStringList(imageUrls)
        dest.writeString(userId)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BirdData> {
        override fun createFromParcel(parcel: Parcel): BirdData = BirdData(parcel)
        override fun newArray(size: Int): Array<out BirdData?>? = arrayOfNulls(size)
    }
}
