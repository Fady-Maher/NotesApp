package com.example.notes.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.AddNote;
import com.example.notes.CustomAdapterRecycle;
import com.example.notes.R;
import com.example.notes.information;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    FirebaseAuth mAuth;

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ArrayList<information> arrayList;
    CustomAdapterRecycle customAdapterRecycle;

    FloatingActionButton floatingActionButton2;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ////////////////////////////////////////////////////////////////////////////////////////////////

        floatingActionButton2=root.findViewById(R.id.floatingActionButton2);

        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), AddNote.class);
                startActivity(intent);
            }
        });

        recyclerView=root.findViewById(R.id.recyckerviewshownote);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrayList=new ArrayList<>();

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

        databaseReference= FirebaseDatabase.getInstance().getReference("note").child(mAuth.getCurrentUser().getUid()).child("normal");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    information informations = dataSnapshot.getValue(information.class);
                    arrayList.add(informations);
                }
                customAdapterRecycle = new CustomAdapterRecycle(getActivity(),arrayList);
                recyclerView.setAdapter(customAdapterRecycle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Failed in download notes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}