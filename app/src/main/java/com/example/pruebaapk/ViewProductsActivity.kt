package com.example.pruebaapk

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebaapk.Product


class ViewProductsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: ArrayList<Product> // Cambia a ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_products)

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts)
        db = DatabaseHelper(this)
        productList = ArrayList()

        // Configura el layout manager para el RecyclerView
        recyclerViewProducts.layoutManager = LinearLayoutManager(this)

        loadProducts()  // Llama a la función para cargar los productos
    }

    private fun loadProducts() {
        try {
            val cursor: Cursor = db.getAllProducts()
            if (cursor.count == 0) {
                Toast.makeText(this, "No hay productos", Toast.LENGTH_SHORT).show()
            } else {
                productList.clear()  // Limpia la lista antes de añadir nuevos productos
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_ID)) // Asegúrate de tener el ID
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_NAME))
                    val desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_DESC))
                    productList.add(Product(id, name, desc)) // Asegúrate de que la clase Product tenga el ID, nombre y descripción
                }
                // Usa ProductAdapter en lugar de ArrayAdapter
                productAdapter = ProductAdapter(this, productList, db) // Asegúrate de que sea un adaptador correcto
                recyclerViewProducts.adapter = productAdapter
            }
            cursor.close() // Cerrar el cursor después de su uso
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar los productos", Toast.LENGTH_SHORT).show()
        }
    }

    fun addProduct(name: String, description: String) {
        if (db.insertProduct(name, description)) {
            Toast.makeText(this, "Producto añadido correctamente", Toast.LENGTH_SHORT).show()
            loadProducts() // Recargar la lista después de añadir el producto
        } else {
            Toast.makeText(this, "Error al añadir el producto", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateProduct(id: Int, name: String, description: String) {
        if (db.updateProduct(id, name, description)) {
            Toast.makeText(this, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
            loadProducts() // Recargar la lista después de actualizar el producto
        } else {
            Toast.makeText(this, "Error al actualizar el producto", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteProduct(id: Int) {
        if (db.deleteProduct(id)) {
            Toast.makeText(this, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show()
            loadProducts() // Recargar la lista después de eliminar el producto
        } else {
            Toast.makeText(this, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
        }
    }
}

 class ProductAdapter(
     private val context: Context,
     private var productList: ArrayList<Product>,
     private val db: DatabaseHelper
    ) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

        inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val productName: TextView = itemView.findViewById(R.id.productName)
            val productDesc: TextView = itemView.findViewById(R.id.productDesc)
            val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
            val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

            fun bind(product: Product) {
                productName.text = product.name
                productDesc.text = product.description

                // Configurar el botón de editar
                buttonEdit.setOnClickListener {
                    showEditDialog(product)
                }


                // Configurar el botón de eliminar
                buttonDelete.setOnClickListener {
                    // Llama al método deleteProduct para eliminar el producto de la base de datos
                    (context as ViewProductsActivity).deleteProduct(product.id)

                    // Elimina el producto de la lista y notifica el cambio al RecyclerView
                    productList.remove(product) // Elimina el producto de la lista local
                    notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
                }

            }
        }

     private fun showEditDialog(product: Product) {
         val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_product, null)
         val editProductName = dialogView.findViewById<EditText>(R.id.editProductName)
         val editProductDesc = dialogView.findViewById<EditText>(R.id.editProductDesc)

         // Rellena el diálogo con los datos actuales del producto
         editProductName.setText(product.name)
         editProductDesc.setText(product.description)

         val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
             .setTitle("Editar Producto")
             .setView(dialogView)
             .setPositiveButton("Guardar") { _, _ ->
                 val newName = editProductName.text.toString()
                 val newDesc = editProductDesc.text.toString()

                 if (newName.isNotEmpty() && newDesc.isNotEmpty()) {
                     // Actualiza el producto en la base de datos y en la lista
                     (context as ViewProductsActivity).updateProduct(product.id, newName, newDesc)
                     product.name = newName
                     product.description = newDesc
                     notifyDataSetChanged() // Refresca el RecyclerView
                 } else {
                     Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                 }
             }
             .setNegativeButton("Cancelar", null)
             .create()

         dialog.show()

     }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_view_products, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val product = productList[position]
            holder.bind(product)
        }

        override fun getItemCount(): Int {
            return productList.size
        }
    }


