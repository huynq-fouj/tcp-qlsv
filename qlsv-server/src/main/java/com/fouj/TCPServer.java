package com.fouj;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCPServer {

    private final String DATA_FILE = "Data.dat";
    private Map<String, Student> students = new HashMap<>();
    private ServerSocket serverSocket;

    public TCPServer(String ipAddress, int port) {
        loadData();
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            this.serverSocket = new ServerSocket(port, 10, address);
            while (true) {
                Socket client = serverSocket.accept();
                new Thread(() -> handleClient(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream inp = new ObjectInputStream(clientSocket.getInputStream());

            InetAddress clientAddress = clientSocket.getInetAddress();
            int clientPort = clientSocket.getPort();
            System.out.println("Connected to client: " + clientAddress.getHostName() + "(" + clientAddress.getHostAddress() + ":" + clientPort + ")");

            while(true) {
                Map<String, Object> request = (Map<String, Object>) inp.readObject();
                String method = (String) request.get("method");
                switch (method) {
                    case "ADD":
                        Student studentToAdd = (Student) request.get("student");
                        students.put(studentToAdd.getId(), studentToAdd);
                        save();
                        out.writeObject("Student added successfully.");
                        break;
                    case "UPDATE":
                        String updateId = (String) request.get("id");
                        if (students.containsKey(updateId)) {
                            Student updatedStudent = (Student) request.get("student");
                            students.put(updateId, updatedStudent);
                            save();
                            out.writeObject("Student updated successfully.");
                        } else {
                            out.writeObject("Student not found.");
                        }
                        break;
                    case "DELETE":
                        String deleteId = (String) request.get("id");
                        if (students.containsKey(deleteId)) {
                            students.remove(deleteId);
                            save();
                            out.writeObject("Student deleted successfully.");
                        } else {
                            out.writeObject("Student not found.");
                        }
                        break;
                    case "SEARCH":
                        String searchId = (String) request.get("id");
                        Student foundStudent = students.get(searchId);
                        if (foundStudent != null) {
                            out.writeObject(foundStudent);
                        } else {
                            out.writeObject("Student not found.");
                        }
                        break;
                    case "EXIT":
                        out.writeObject("Connection closed.");
                        clientSocket.close();
                        return;
                    default:
                        out.writeObject("Invalid command.");
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            System.out.println("Load data");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE));
            students = (Map<String, Student>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Can not open file: " + e.getMessage());
            System.out.println("Try ");
            save();
        }
    }

    private void save() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Không thể tạo tệp dữ liệu: " + e.getMessage());
                return;
            }
        }

        try {
            System.out.println("Save file.");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE));
            oos.writeObject(students);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
