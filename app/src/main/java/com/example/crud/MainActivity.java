package com.example.crud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crud.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText ed_nombre;
    EditText ed_correo;
    EditText ed_contra;

    ListView lv_lista;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Persona> personaList = new ArrayList<Persona>();
    ArrayAdapter<Persona> personaArrayAdapter;

    Persona personaSelect;
    Persona persona = new Persona();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ed_nombre = findViewById(R.id.ed_nombre);
        ed_correo = findViewById(R.id.ed_correo);
        ed_contra = findViewById(R.id.ed_contra);

        lv_lista = findViewById(R.id.lv_clietes);

        iniciarFirabase();
        
        listaDatos();

        lv_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSelect = (Persona) parent.getItemAtPosition(position);
                ed_nombre.setText(personaSelect.getNombre());
                ed_correo.setText(personaSelect.getCorreo());
                ed_contra.setText(personaSelect.getContrasenia());
            }
        });

    }

    private void listaDatos()
    {

        databaseReference.child("Cliente").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                personaList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Persona persona = dataSnapshot1.getValue(Persona.class);
                    personaList.add(persona);

                    personaArrayAdapter = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1,personaList);
                    lv_lista.setAdapter(personaArrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void iniciarFirabase()
    {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String nombre = ed_nombre.getText().toString().trim();
        String correo = ed_correo.getText().toString().trim();
        String contra = ed_contra.getText().toString().trim();



        switch (item.getItemId())
        {
            case R.id.mu_add:
                if (nombre.equals("") || correo.equals("") || contra.equals(""))
                {
                    valida(nombre,correo,contra,ed_nombre,ed_correo,ed_contra);
                }else {

                    persona.setUid(UUID.randomUUID().toString());
                    persona.setNombre(nombre );
                    persona.setCorreo(correo);
                    persona.setContrasenia(contra);

                    databaseReference.child("Cliente").child(persona.getUid()).setValue(persona);

                    Toast.makeText(this, "Cliente agregado ", Toast.LENGTH_SHORT).show();
                    limpiarCajas();
                }
                break;
            case R.id.mu_save:

                AlertDialog.Builder aleBuilder = new AlertDialog.Builder(MainActivity.this);
                aleBuilder.setMessage("Esta seguro de actualizar?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                persona.setUid(personaSelect.getUid());
                                persona.setNombre(ed_nombre.getText().toString().trim());
                                persona.setCorreo(ed_correo.getText().toString().trim());
                                persona.setContrasenia(ed_contra.getText().toString().trim());
                                databaseReference.child("Cliente").child(persona.getUid()).setValue(persona);
                                limpiarCajas();
                                Toast.makeText(MainActivity.this,"Cliente actualizado",Toast.LENGTH_SHORT).show();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                limpiarCajas();
                            }
                        });
                AlertDialog titulo = aleBuilder.create();
                titulo.setTitle("Advertencia");
                titulo.show();

                break;
            case R.id.mu_delete:

                final AlertDialog.Builder aleBuilder2 = new AlertDialog.Builder(MainActivity.this);
                aleBuilder2.setMessage("Esta seguro de Eliminar")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                persona.setUid(personaSelect.getUid());
                                databaseReference.child("Cliente").child(persona.getUid()).removeValue();
                                limpiarCajas();

                                Toast.makeText(MainActivity.this,"Cliente eliminado",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                limpiarCajas();
                            }
                        });
                AlertDialog titulo2 = aleBuilder2.create();
                titulo2.setTitle("Advertencia");
                titulo2.show();



                break;
                default: break;
        }
        return true;
    }

    private void limpiarCajas()
    {
        ed_nombre.setText("");
        ed_contra.setText("");
        ed_correo.setText("");
    }

    public  void valida(String string1, String string3,String string4, EditText nombre, EditText correo, EditText contra)
    {
        if (string1.equals(""))
        {
            nombre.setError("Campo requerido");
        }else if (string3.equals(""))
        {
            correo.setError("Campo requerido");
        }else if (string4.equals(""))
        {
            contra.setError("Campo requerido");
        }
    }
}
