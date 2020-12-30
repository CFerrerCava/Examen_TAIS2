package com.example.loginmaterialg1.exam1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginmaterialg1.Model._Product
import com.example.loginmaterialg1.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_form_produc.*
import kotlinx.android.synthetic.main.activity_unidad1.*
import kotlinx.android.synthetic.main.activity_unidad1.llProgressBar
import org.json.JSONObject
import java.lang.Exception

class unidad1Activity : AppCompatActivity(), productoList_adapter.OnItemClickListener {
    private val  product_Class = functionProduct()
    private lateinit var productList : MutableList<_Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unidad1)
        getList(this)
        insertProduct()
    }
    fun getList(unidad1Activity: unidad1Activity) {
        load_init()
        val ref = product_Class.listProdcuts()
        try {
            ref.addValueEventListener( object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        productList = mutableListOf()
                        for (item in snapshot.child("products").children){
                            val hero: _Product? = item.getValue(_Product::class.java)
                            productList.add(hero!!)
                        }
                        initRecycler(productoList_adapter(productList, unidad1Activity))
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }catch (ex: Exception){
            //initRecycler(productoList_adapter(productList, unidad1Activity))
        }
    }
    fun initRecycler(productolistAdapter: productoList_adapter) {
        recycler_view.adapter = productolistAdapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        load_close()
    }
    fun insertProduct(){
        btn_add.setOnClickListener {
            startActivity(Intent(this, form_product::class.java))
        }
    }
    fun createIntent(item: _Product){
        val intent = Intent(this, form_product::class.java)
        val jsonObject = JSONObject()
        jsonObject.put("id",item.id)
        jsonObject.put("description",item.description)
        jsonObject.put("stock",item.stock)
        jsonObject.put("weight",item.weight)
        jsonObject.put("photo",item.photo)
        jsonObject.put("category",item.category)
        intent.putExtra("product", jsonObject.toString())
        startActivity(intent)
    }
    override fun onItemClick(position: Int) {
        val item = productList[position]
        createIntent(item)
    }

    override fun onItemLongClick(position: Int) {
        val item = productList[position]
        delete(item)
    }

    fun delete(item: _Product){
        val dialog= AlertDialog.Builder(this)
        dialog.setTitle("Confirmación")
        dialog.setMessage("Confirme si desea eliminar? ")
        dialog.setPositiveButton("SI"){ _, _->
            product_Class.delete(item.id)
        }
        dialog.setNegativeButton("NO"){ dialogInterface, _->
            dialogInterface.dismiss()
        }
        dialog.show()
    }
    fun load_init(){
        llProgressBar.isVisible = true
    }
    fun load_close(){
        llProgressBar.isGone = true
    }
}