package com.example.aggienetwork.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aggienetwork.Adapter.ProfileGridAdapter;
import com.example.aggienetwork.ChatActivity;
import com.example.aggienetwork.EditProfileActivity;
import com.example.aggienetwork.ImageSetupActivity;
import com.example.aggienetwork.LoginActivity;
import com.example.aggienetwork.Model.Post;
import com.example.aggienetwork.Model.User;
import com.example.aggienetwork.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    CircleImageView userprofileimage;
    TextView username;
    TextView noofposts;
    TextView nooffollowers;
    TextView nooffollowing;
    FirebaseUser firebaseUser;
    RecyclerView profilegridimagerecyclerview;
    Button logoutbutton;

    Button editProfileButton;

    List<Post> postitem;
    ProfileGridAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        userprofileimage =  view.findViewById(R.id.profileuserimage);
        username = view.findViewById(R.id.usernametextview);
        noofposts = view.findViewById(R.id.noofposts);
        nooffollowers= view.findViewById(R.id.nooffollowers);
        nooffollowing= view.findViewById(R.id.nooffollowing);
        logoutbutton = view.findViewById(R.id.logoutbutton);
        editProfileButton = view.findViewById(R.id.editprofilebutton);
        postitem= new ArrayList<>();

        profilegridimagerecyclerview= view.findViewById(R.id.profileimagerecyclerview);
        profilegridimagerecyclerview.setLayoutManager(new GridLayoutManager(getContext(),3));

        //Creating Adapter for gridimages
        adapter = new ProfileGridAdapter(getContext(),postitem);
        profilegridimagerecyclerview.setAdapter(adapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


// Calling methods

        getnooffollwers(firebaseUser.getUid(),nooffollowers);
        getnooffollwing(firebaseUser.getUid(),nooffollowing);
        getnoofposts(firebaseUser.getUid(),noofposts);
        Log.i("Uid",firebaseUser.getUid());
        getImage(firebaseUser.getUid(),userprofileimage);

        getpostitem();

        logoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                startActivity( new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();

            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity( new Intent(getActivity(), EditProfileActivity.class));
                getActivity().finish();

            }
        });

        return view;
    }

    private void getpostitem() {
        FirebaseDatabase.getInstance("https://aggienet-posts.firebaseio.com/").getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getUid().equals(firebaseUser.getUid())){
                        postitem.add(post);
                        Log.i("Postitemadded","PostAdded");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getImage(String uid, CircleImageView userprofileimage) {
        FirebaseDatabase.getInstance("https://aggienet-users.firebaseio.com/").getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User user = snapshot.getValue(User.class);
                    String image= user.getImage();
                    username.setText(user.getUsername());

                    if(image != null){
                        Picasso.get().load(image).into(userprofileimage);
                    }
                    else{
                        Picasso.get().load(R.mipmap.ic_launcher).into(userprofileimage);
                    }
                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getnooffollwers(String uid, TextView nooffollowers) {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(uid).child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numberoffollowers = String.valueOf(snapshot.getChildrenCount());
                nooffollowers.setText(numberoffollowers + "  Followers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getnooffollwing(String uid, TextView nooffollowing) {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(uid).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numberoffollowing = String.valueOf(snapshot.getChildrenCount());
                nooffollowing.setText(numberoffollowing + "  Following ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getnoofposts(String uid, TextView noofposts) {
        DatabaseReference noofpostsRef = FirebaseDatabase.getInstance("https://aggienet-users.firebaseio.com/").getReference().child("Users").child(uid).child("noofposts");

        noofpostsRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int numberOfPosts = snapshot.getValue(Integer.class);
                    String numberofposts = String.valueOf(numberOfPosts);
                    noofposts.setText(numberofposts + "  Posts ");

                    // Now you have the number of posts as an integer in the "numberOfPosts" variable
                    // You can use it as needed, for example:
                    // display it, update it, etc.
                    // Example: yourTextView.setText("Number of Posts: " + numberOfPosts);
                } else {

                    noofposts.setText("0  Posts ");
                    // Handle the case where the "noofposts" node does not exist
                }
            }
        });

    }
}