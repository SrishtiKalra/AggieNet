package com.example.aggienetwork;

import com.example.aggienetwork.Model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;



public class PostWebServer {

    private DatabaseReference database;

    public static void main(String[] args) {
        new com.example.aggienetwork.WebServer().startServer();
    }

    public PostWebServer() {
        initializeFirebase();
    }

    private void initializeFirebase() {
        database = FirebaseDatabase.getInstance("https://aggienet-posts.firebaseio.com/").getReference();

    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server started on port 8080");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClientRequest(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientRequest(Socket clientSocket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String line = reader.readLine();
            if (line == null) {
                return;
            }

            String[] requestLineParts = line.split(" ");
            String method = requestLineParts[0];
            String path = requestLineParts[1];

            if (method.equals("GET") && path.startsWith("/post/")) {
                String postId = path.substring(6); // Extracting postId from the path
                Post post = getPostById(postId);
                if (post != null) {
                    String postJson = new Gson().toJson(post);
                    sendResponse(clientSocket, postJson);
                } else {
                }
            } else if (method.equals("POST") && path.equals("/post/update")) {
                StringBuilder requestBody = new StringBuilder();

                Post post = new Gson().fromJson(requestBody.toString(), Post.class);

                updatePost(post.getPostid(), post);

                // Send a response to the client
                sendResponse(clientSocket, "{\"message\":\"Post updated successfully\"}");

            }

            // Close the client socket
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Post getPostById(String postId) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://aggienet-posts.firebaseio.com/").getReference("Posts").child(postId);

        final Post[] posts = new Post[1];
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    posts[0] = post;
                } else {
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return posts[0];
    }

    private void sendResponse(Socket clientSocket, String response) throws IOException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
        out.println(response);
        out.flush();
        clientSocket.close();
    }

    private void updatePost(String postId, Post post) {
        DatabaseReference ref = database.child("Posts").child(postId);
        ref.setValue(post);
    }


}
