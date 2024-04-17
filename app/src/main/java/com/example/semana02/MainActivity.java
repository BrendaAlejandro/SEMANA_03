package com.example.semana02;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.semana02.adapter.UserAdapter;
import com.example.semana02.entity.Address;
import com.example.semana02.entity.User;
import com.example.semana02.service.ServiceUser;
import com.example.semana02.util.ConnectionRest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // Variables de la vista
    ListView lstUser;
    Button btnFiltrar;

    // Lista de usuarios y adaptador
    ArrayList<User> listaUser = new ArrayList<>();
    UserAdapter userAdapter;

    // Servicio REST
    ServiceUser serviceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicialización de variables de la vista
        lstUser = findViewById(R.id.lstUsuarios);
        btnFiltrar = findViewById(R.id.btnFiltrar);

        // Inicialización del adaptador
        userAdapter = new UserAdapter(this, R.layout.user_item, listaUser);
        lstUser.setAdapter(userAdapter);

        // Inicialización del servicio REST
        serviceUser = ConnectionRest.getConnecion().create(ServiceUser.class);

        // Acción del botón "Filtrar"
        btnFiltrar.setOnClickListener(v -> cargaUsuarios());

        // Acción al hacer clic en un usuario
        lstUser.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = listaUser.get(position);
            showAddressDialog(selectedUser.getAddress());
        });
    }

    // Método para cargar usuarios desde el servicio REST
    void cargaUsuarios() {
        Call<List<User>> call = serviceUser.listausuarios();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    listaUser.clear();
                    listaUser.addAll(response.body());
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para mostrar los detalles de la dirección en un diálogo
    private void showAddressDialog(Address address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Dirección");

        if (address != null) {
            String message = "Calle: " + address.getStreet() + "\n" +
                    "Suite: " + address.getSuite() + "\n" +
                    "Ciudad: " + address.getCity() + "\n" +
                    "Código Postal: " + address.getZipcode();
            builder.setMessage(message);
        } else {
            builder.setMessage("Dirección no disponible");
        }

        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
