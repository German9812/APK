package com.example.pruebaapk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CrudActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var editTextName: EditText
    private lateinit var editTextDesc: EditText
    private lateinit var buttonAddProduct: Button
    private lateinit var buttonViewProducts: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud)

        db = DatabaseHelper(this)

        editTextName = findViewById(R.id.editTextName)
        editTextDesc = findViewById(R.id.editTextDesc)
        buttonAddProduct = findViewById(R.id.buttonAddProduct)
        buttonViewProducts = findViewById(R.id.buttonViewProducts)

        buttonAddProduct.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val desc = editTextDesc.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val isInserted = db.insertProduct(name, desc)
                if (isInserted) {
                    Toast.makeText(this, "Producto añadido", Toast.LENGTH_SHORT).show()
                    editTextName.text.clear()  // Limpia el campo de nombre
                    editTextDesc.text.clear()  // Limpia el campo de descripción
                } else {
                    Toast.makeText(this, "Error al añadir el producto", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonViewProducts.setOnClickListener {
            val intent = Intent(this, ViewProductsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        db.close() // Cerrar la base de datos cuando se destruye la actividad
        super.onDestroy()
    }
}
