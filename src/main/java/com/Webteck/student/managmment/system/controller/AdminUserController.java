package com.Webteck.student.managmment.system.controller;

import com.Webteck.student.managmment.system.model.Role;
import com.Webteck.student.managmment.system.model.User;
import com.Webteck.student.managmment.system.service.UserService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller

public class AdminUserController {

    private final UserService userService; // Assuming you have a UserService to handle user operations
    //private final FurnitureService furnitureService;

    public AdminUserController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/admin/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User()); // Create a new User object
        return "add-user"; // Return the add user template
    }

    @PostMapping("/admin/users")
    public String addUser (@ModelAttribute User user) {
        userService.registerUser (user); // Save the user using your service
        return "redirect:/admin"; // Redirect to the admin dashboard after saving
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser (@PathVariable Long id) {
        userService.deleteUser (id); // Call the service to delete the user
        return "redirect:/admin"; // Redirect to the admin dashboard after deletion
    }

    @GetMapping("/admin/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id); // Fetch the user by ID
        model.addAttribute("user", user); // Add the user to the model
        return "edit-user"; // Return the edit user template
    }

    @PostMapping("/admin/users/update")
    public String updateUser (@ModelAttribute User user) {
        userService.updateUser (user); // Call the service to update the user
        return "redirect:/admin"; // Redirect to the admin dashboard after updating
    }

    @GetMapping("/admin/search")
    public String showSearchForm() {
        return "search-user"; // Return the search user template
    }

    @GetMapping("/admin/search/results")
    public String searchUsers(@RequestParam(required = false) String username,
                              @RequestParam(required = false) String email,
                              Model model) {
        List<User> users = userService.searchUsers(username, email); // Call the service to search for users
        model.addAttribute("users", users); // Add the list of users to the model
        return "user-list"; // Return the template that displays the list of users
    }
    @GetMapping("/admin/upload")
    public String showUploadPage() {
        return "upload"; // Return the combined upload page template
    }
    // Handle GET requests for the user upload endpoint
    @GetMapping("/admin/upload/users")
    public String showUserUploadForm(Model model) {
        model.addAttribute("userMessage", "Please use the form to upload user data.");
        return "upload"; // Display the upload page with a message
    }

    // Handle POST requests for user upload
    @PostMapping("/admin/upload/users")
    public String uploadUsers(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("userMessage", "Please select a file to upload.");
            return "upload";
        }

        try {
            List<User> userList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            reader.readLine(); // Skip header line

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                User user = new User();
                user.setUsername(data[0]);
                user.setFirstName(data[1]);
                user.setLastName(data[2]);
                user.setEmail(data[3]);
                user.setPhoneNumber(data[4]);
                user.setProfilePicture(data[5]);
                user.setRole(Role.valueOf(data[6]));
                userList.add(user);
            }

            userService.saveAll(userList);
            model.addAttribute("userMessage", "User file uploaded successfully!");
            return "redirect:/admin";
        } catch (IOException | IllegalArgumentException e) {
            model.addAttribute("userMessage", "Failed to upload user file: " + e.getMessage());
        }

        return "upload";
    }


    @GetMapping("/admin/download/users")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> downloadUsers() throws IOException {
        List<User> users = userService.getAllUsers(); // Fetch all users from the service

        // Create CSV content
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        // Write CSV header
        writer.println("ID,Username,Email"); // Adjust according to your User fields

        // Write user data
        for (User  user : users) {
            writer.printf("%d,%s,%s%n", user.getId(), user.getUsername(), user.getEmail()); // Adjust according to your User fields
        }
        writer.flush();
        writer.close();

        ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

        // Set the content type and attachment header
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }



//    @GetMapping("/admin/upload/furniture")
//    public String showUploadForm() {
//        return "upload-furniture"; // Return the upload form template
//    }

//    @PostMapping("/admin/upload/furniture")
//    public String uploadFurniture(@RequestParam("file") MultipartFile file, Model model) {
//        if (file.isEmpty()) {
//            model.addAttribute("message", "Please select a file to upload.");
//            return "upload-furniture"; // Return to the upload form with an error message
//        }
//
//        try {
//            List<Furniture> furnitureList = new ArrayList<>();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
//            String line;
//
//            // Skip the header line
//            reader.readLine(); // This will read and ignore the header
//
//            // Read the CSV file line by line
//            while ((line = reader.readLine()) != null) {
//                String[] data = line.split(","); // Assuming CSV format
//                Furniture furniture = new Furniture();
//                furniture.setName(data[0]); // Assuming the first column is name
//                furniture.setType(data[1]); // Assuming the second column is type
//                furniture.setPrice(Double.parseDouble(data[2])); // Assuming the third column is price
//                furniture.setImageUrl(data[3]); // Assuming the fourth column is image URL
//                furnitureList.add(furniture);
//            }
//
//            furnitureService.saveAll(furnitureList); // Save all furniture data to the database
//            model.addAttribute("message", "File uploaded successfully!");
//        } catch (IOException e) {
//            model.addAttribute("message", "Failed to upload file: " + e.getMessage());
//        } catch (NumberFormatException e) {
//            model.addAttribute("message", "Invalid number format in the file: " + e.getMessage());
//        }
//
//        return "upload-furniture"; // Return to the upload form
//    }
}