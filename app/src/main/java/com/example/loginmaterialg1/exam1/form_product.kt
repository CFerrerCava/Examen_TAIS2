package com.example.loginmaterialg1.exam1

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.loginmaterialg1.Model.Categories
import com.example.loginmaterialg1.Model.Product
import com.example.loginmaterialg1.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.*
import kotlinx.android.synthetic.main.activity_form_produc.*
import kotlinx.android.synthetic.main.spinner_custom.view.*
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class form_product : AppCompatActivity() {
    private var mStorageRef: StorageReference? = null
    private val  product_Class = functionProduct()
    private val GALLERY_INTENT: Int = 1
    private var IMAGE: String = ""
    private var ID: String = "-"
    private var CREATE: Boolean = false
    private var listCateories:ArrayList<String> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_produc)
        getSpinner()
        register_event()
        uploadImage()
        spinner_register_event()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun register_event(){
        btn_register.setOnClickListener {
            if (!IMAGE.equals("")){
                load_init()
                val item_product = Product(
                    ID,
                    IMAGE,
                    txt_description.text.toString(),
                    spinner_custom.spinner3.selectedItem.toString(),
                    txt_weight.text.toString(),
                    txt_stock.text.toString()
                )
                val rpt: Boolean
                if (CREATE){
                    rpt =  product_Class.register(item_product)
                }else{
                    rpt =  product_Class.upload(item_product)
                }
                if (rpt){
                    val text = if (CREATE) "registrado" else "Actualizado"
                    Toast.makeText(this, "Producto $text", Toast.LENGTH_SHORT).show()

                }else{
                    Toast.makeText(this, "Ocurrio un error, intentarlo de nuevo", Toast.LENGTH_LONG).show()
                }
                load_close()
                Timer("SettingUp", false).schedule(500) {
                    finish()
                }
            }else{
                load_close()
                Toast.makeText(this, "Subir imagen por favor", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun uploadImage(){
        btn_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, GALLERY_INTENT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            load_init()
            val uri = data!!.data
            val response = product_Class.uploadImages(uri)
            uri?.let {
                response!!.putFile(it)
                    .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot -> // Get a URL to the uploaded ontent
                        val url2 = taskSnapshot.storage.downloadUrl
                        url2.addOnSuccessListener {
                            if (url2.isSuccessful) {
                                img_product.setImageURI(uri)
                                val downloadUrl: Uri? = url2.result
                                IMAGE = downloadUrl.toString()
                                load_close()
                            }
                        }
                    })
                    .addOnFailureListener(OnFailureListener {
                        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                    })
            }
        }
    }

    fun load_init(){
        llProgressBar.isVisible = true
    }
    fun load_close(){
        llProgressBar.isGone = true
    }
    fun verifyData(){
        val parametros = this.intent.extras
        if ( parametros != null){
            load_init()
            val product = JSONObject(parametros.getString("product").toString())
            getBitmapFromURL(product.get("photo").toString())
            ID = product.get("id").toString()
            IMAGE = product.get("photo").toString()
            val categoryPosition = listCateories.indexOf(product.get("category").toString())
            spinner_custom.spinner3.setSelection(categoryPosition)
            txt_description.setText(product.get("description").toString())
            txt_stock.setText(product.get("stock").toString())
            txt_weight.setText(product.get("weight").toString())
            load_close()
        }else{
            CREATE = true
        }
    }
    fun getBitmapFromURL(src: String?) {
        val ONE_MEGABYTE: Long = 1024 * 1024
        mStorageRef = FirebaseStorage.getInstance().reference;
        val islandRef = mStorageRef?.storage?.getReferenceFromUrl(src!!)
        try {
            islandRef!!.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                    ByteArray->
                val bmp = BitmapFactory.decodeByteArray(ByteArray, 0, ByteArray.size);
                img_product.setImageBitmap(bmp);
            }.addOnFailureListener {
                    Exception->
                Exception.message?.let { Log.d("myTag", it) }
            }
        }catch (ex: Exception){
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun spinner_register_event(){
        spinner_custom.imageButton2.setOnClickListener{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Registrar")
            dialog.setMessage("Ingrese núeva categoria")
            val input = EditText(this)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
            input.layoutParams = lp
            dialog.setView(input)
            dialog.setPositiveButton("Registrar") {_,_->
                val category = Categories()
                category.description = input.text.toString()
                if(product_Class.registerCategory(category))
                    getSpinner()
            }
            dialog.setNegativeButton("Cancelar") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            dialog.show()
        }
    }
    fun getSpinner() {
        val ref = product_Class.listProdcuts()
        try {
            ref.addValueEventListener( object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        listCateories = arrayListOf()
                        for (item in snapshot.child("categories").children){
                            val hero: Categories? = item.getValue(Categories::class.java)
                            listCateories.add(hero!!.description)
                        }
                        spinner_custom.spinner3.adapter = ArrayAdapter<String>(
                            applicationContext,
                            android.R.layout.simple_spinner_dropdown_item,
                            listCateories
                        )
                        verifyData()
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }catch (ex: Exception){
            Log.d("Spinnes",ex.message.toString())
        }
    }
}