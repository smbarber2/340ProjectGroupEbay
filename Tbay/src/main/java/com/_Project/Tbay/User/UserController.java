package com._Project.Tbay.User;

import com._Project.Tbay.Cart.Cart;
import com._Project.Tbay.Cart.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/users")

public class UserController {
    @Autowired
    private UserService service;
    @Autowired
    private CartService cartService;

    //Pre MVC new
//    @PostMapping("/new")
//    public List<User> addNewUser(@RequestBody User user){
//        Cart cart = new Cart();
//        cartService.addNewCart(cart);
//        user.setCartId(cart.getCartId());
//        service.addNewUser(user);
//        return service.getAllUsers();
//    }


    @GetMapping("create-acct")
    public String createAcct(){return "create-acct";}

    @PostMapping("/new")
    public String addNewUser(User user){
        Cart cart = new Cart();
        cartService.addNewCart(cart);
        user.setCartId(cart.getCartId());

        service.updateUser(user.getUserId(), user);
        return "redirect:/homepage";
    }

    //Pre MVC update
//    @PutMapping("/update/{userId}")
//    public User updateUser(@PathVariable long userId, @RequestBody User user) {
//        service.updateUser(userId, user);
//        return service.getUserById(userId);
//    }

    @GetMapping("/update/{userid}")
    public String showUpdateForm(@PathVariable int userId, Model model){
        model.addAttribute("user", service.getUserById(userId));
        return "edit-profile";
    }
    @PostMapping("/update")
    public String updateUser(User user) {
        service.updateUser(user.getUserId(), user);
        return "redirect:/profile/" + user.getUserId();
    }


}
