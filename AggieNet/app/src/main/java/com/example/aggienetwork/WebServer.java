package com.example.aggienetwork;

import com.example.aggienetwork.Model.User;
import com.google.firebase.database.*;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class WebServer {

    private DatabaseReference database;

    public static void main(String[] args) {
        new WebServer().startServer();
    }

    public WebServer() {
        initializeFirebase();
    }

    private void initializeFirebase() {
        database = FirebaseDatabase.getInstance("https://aggienet-users.firebaseio.com/").getReference();

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

            if (method.equals("GET") && path.startsWith("/user/")) {
                String userId = path.substring(6); // Extracting userId from the path
                User user = getUserById(userId);
                if (user != null) {
                    String userJson = new Gson().toJson(user);
                    sendResponse(clientSocket, userJson);
                } else {
                }
            } else if (method.equals("POST") && path.equals("/user/update")) {
                StringBuilder requestBody = new StringBuilder();

                User user = new Gson().fromJson(requestBody.toString(), User.class);

                updateUser(user.getId(), user);

                // Send a response to the client
                sendResponse(clientSocket, "{\"message\":\"User updated successfully\"}");

            }

            // Close the client socket
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User getUserById(String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        final User[] users = new User[1];
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    users[0] = user;
                } else {
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return users[0];
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

    private void updateUser(String userId, User user) {
        DatabaseReference ref = database.child("Users").child(userId);
        ref.setValue(user);
    }


}
