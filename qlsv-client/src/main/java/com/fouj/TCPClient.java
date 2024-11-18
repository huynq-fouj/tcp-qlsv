package com.fouj;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TCPClient {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream inp;

    public TCPClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            inp = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendObject(Map<String, Object> data) throws IOException {
        out.writeObject(data);
    }

    private Object receiveObject() throws IOException, ClassNotFoundException {
        return inp.readObject();
    }

    private void closeConnections() {
        try {
            if (inp != null) inp.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMenu() {
        System.out.println("CHƯƠNG TRÌNH QLSV-2024");
        System.out.println("------------------------------------------");
        System.out.println("1. Thêm sinh viên");
        System.out.println("2. Sửa");
        System.out.println("3. Xóa");
        System.out.println("4. Tìm sinh viên theo mã sinh viên");
        System.out.println("5. Thoát");
        System.out.print("Chọn chức năng: ");
    }

    private Student inputStudent(Scanner scanner) {
        System.out.print("Mã sinh viên: ");
        String id = scanner.nextLine();
        System.out.print("Họ tên: ");
        String name = scanner.nextLine();
        System.out.print("Ngành học: ");
        String major = scanner.nextLine();
        System.out.print("Ngoại ngữ: ");
        String language = scanner.nextLine();
        double gpa;
        while (true) {
            try {
                System.out.print("Điểm tổng kết: ");
                gpa = Double.parseDouble(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Điểm không hợp lệ! Vui lòng nhập lại.");
            }
        }
        scanner.nextLine();
        return new Student()
                .setId(id)
                .setName(name)
                .setMajor(major)
                .setLanguage(language)
                .setGpa(gpa);
    }

    private  void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            showMenu();
            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Lựa chọn không hợp lệ, vui lòng nhập số!");
                continue;
            }

            try {
                Map<String, Object> request = new HashMap<>();
                switch (choice) {
                    case 1:
                        request.put("method", "ADD");
                        System.out.println("Nhập thông tin sinh viên:");
                        Student newStudent = inputStudent(sc);
                        request.put("student", newStudent);
                        sendObject(request);
                        System.out.println(receiveObject());
                        break;
                    case 2:
                        request.put("method", "UPDATE");
                        System.out.println("Nhập thông tin sinh viên:");
                        Student updStudent = inputStudent(sc);
                        request.put("student", updStudent);
                        request.put("id", updStudent.getId());
                        sendObject(request);
                        System.out.println(receiveObject());
                        break;
                    case 3:
                        request.put("method", "DELETE");
                        System.out.print("Nhập mã sinh viên cần xóa: ");
                        String deleteId = sc.nextLine();
                        request.put("id", deleteId);
                        sendObject(request);
                        System.out.println(receiveObject());
                        break;
                    case 4:
                        System.out.print("Nhập mã sinh viên cần tìm: ");
                        String searchId = sc.nextLine();
                        request.put("method", "SEARCH");
                        request.put("id", searchId);
                        sendObject(request);
                        Object searchResult = receiveObject();
                        System.out.println(searchResult != null ? searchResult : "Không tìm thấy sinh viên.");
                        break;
                    case 5:
                        request.put("method", "EXIT");
                        sendObject(request);
                        System.out.println(receiveObject());
                        closeConnections();
                        return;
                    default:
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
