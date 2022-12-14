package com.example.panaderia_pan_de_oro;

import static com.example.panaderia_pan_de_oro.R.id.ettotal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText jetcodigo, jetproducto, jetstock, jetprecio;
    EditText jetTotal;
    CheckBox jcbpagado;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String codigo, producto, stock, precio, total, ident_doc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // vamos a borrar la barra con  el nombre de la app y a traer los datos de la vista xml
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
        jetcodigo=findViewById(R.id.etcodigo);
        jetproducto=findViewById(R.id.etproducto);
        jetstock=findViewById(R.id.etstock);
        jetprecio=findViewById(R.id.etprecio);
        jcbpagado=findViewById(R.id.cbpagado);
       // jetTotal = findViewById(R.id.etTotal);
    }

    public void Agregar(View view){
        codigo=jetcodigo.getText().toString();
        producto=jetproducto.getText().toString();
        stock=jetstock.getText().toString();
        precio=jetprecio.getText().toString();


        if (codigo.isEmpty()||producto.isEmpty()||stock.isEmpty()||precio.isEmpty()){
            Toast.makeText(this, "Por favor digite todos los campos", Toast.LENGTH_SHORT).show();
        }else{
            // Create a new user with a first and last name
            Map<String, Object> inventario = new HashMap<>();
            inventario.put("codigo", codigo);
            inventario.put("producto", producto);
            inventario.put("stock", stock);
            inventario.put("precio", precio);
            inventario.put("Existente", "si");

            // Add a new document with a generated ID
            db.collection("panaderia")
                    .add(inventario)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(MainActivity.this, "Producto almacenado", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Limpiar_campos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error almacenando producto", Toast.LENGTH_SHORT).show();
                            //Log.w(TAG, "Error adding document", e);


                        }
                    });

        }

    }
    public void Consultar (View view){
        Buscar();
    }

    private  void Buscar(){
        codigo=jetcodigo.getText().toString();
        if (codigo.isEmpty()){
            Toast.makeText(this, "Se requiere el codigo del producto", Toast.LENGTH_SHORT).show();

        }else{
            db.collection("panaderia").whereEqualTo("codigo",codigo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                   // Log.d(TAG, document.getId() + " => " + document.getData());
                                    if (document.getString("Existente").equals("no")){
                                        Toast.makeText(MainActivity.this, "No hay stock del producto", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        ident_doc=document.getId();
                                        jetproducto.setText(document.getString("producto"));
                                        jetstock.setText(document.getString("stock"));
                                        jetprecio.setText(document.getString("precio"));
                                        if (document.getString("Existente").equals("si"))
                                            jcbpagado.setChecked(true);
                                        else
                                            jcbpagado.setChecked(false);
                                    }
                                }
                            } else {

                                //Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }


    private void Limpiar_campos(){
        jetcodigo.setText("");
        jetproducto.setText("");
        jetstock.setText("");
        jetprecio.setText("");
        jcbpagado.setChecked(false);
    }
}