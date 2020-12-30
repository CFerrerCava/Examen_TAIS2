package com.example.loginmaterialg1.exam1

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.loginmaterialg1.Datos.ProductDatabase
import com.example.loginmaterialg1.Model.Categories
import com.example.loginmaterialg1.Model.Product
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalDateTime


class functionProduct(){
    private var mStorageRef: StorageReference? = null
    private val database = FirebaseDatabase.getInstance()
    private val ref_database = database.reference
    val db_model= ProductDatabase()
    fun listProdcuts(): DatabaseReference {
        return  ref_database
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun register(product: Product):Boolean {
        try {
            var id = LocalDateTime.now().toString()
            id = id.replace(":", "")
            id = id.replace("-", "")
            id = id.replace(".", "")
            product.id = id
            ref_database.child(db_model.database_name).child(id).setValue(product)
            return true
        }catch (ex: Exception){
            return false
        }
    }
    fun uploadImages(uri: Uri?): StorageReference? {
        mStorageRef = FirebaseStorage.getInstance().reference;
        return uri?.lastPathSegment?.let { mStorageRef!!.child("products").child(it) }
    }
    fun upload(product: Product):Boolean {
        try {
            ref_database.child(db_model.database_name).child(product.id).setValue(product)
            return true
        }catch (ex: Exception){
            return false
        }
    }
    fun delete(id: String): Boolean {
        ref_database.child(db_model.database_name).child(id).removeValue()
        return true
        //return ref_database.child(db_model.database_name).orderByChild(id).equalTo(id)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun registerCategory(item: Categories): Boolean {
        try {
            var id = LocalDateTime.now().toString()
            id = id.replace(":", "")
            id = id.replace("-", "")
            id = id.replace(".", "")
            item.id = id
            ref_database.child("categories").child(id).setValue(item)
            return true
        }catch (ex: Exception){
            return false
        }
    }

}