package com._Project.Tbay.Cart;

import com._Project.Tbay.Listing.Listing;
import com._Project.Tbay.Listing.ListingService;
import com._Project.Tbay.User.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/carts")
public class CartController {
    @Autowired
    private CartService service;
    @Autowired
    private ListingService listingService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/all")
    public String getAllCarts(Model model){
        model.addAttribute("cartList",service.getAllCarts());
        model.addAttribute("title", "All Carts");
        return "cartList";
    }

    /*@GetMapping("/all")
    public List<Cart> getAllCarts(){
        return service.getAllCarts();
    }*/

    @PutMapping("/update/{cartId}")
    public Cart updateCart(@PathVariable long cartId, @RequestBody Cart cart) {
        service.updateCart(cartId, cart);
        return service.getCartById(cartId);
    }

    @GetMapping("/{cartId}")
    public String getCart(@PathVariable long cartId, Model model){

        List<Integer> deserializedList = new ArrayList<>();
        List<Listing> list = new ArrayList<>();

        model.addAttribute("cart", service.getCartById(cartId));
        model.addAttribute("title", "Cart:"+cartId);

        try{
            Blob blobData = jdbcTemplate.queryForObject(
                    "SELECT cart_list FROM carts WHERE cart_id= ?",
                    new Object[]{cartId},
                    (rs, rowNum) -> rs.getBlob("cart_list")
            );

            byte[] data = blobData.getBytes(1, (int) blobData.length());

            try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                deserializedList = (ArrayList<Integer>) ois.readObject();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        for(int val: deserializedList){
            list.add(listingService.getListingById(val));
        }

        model.addAttribute("listingList", list);

        return "checkout";
    }

    @PostMapping("/new")
    public void addNewCart(@RequestBody Cart cart) {
        service.addNewCart(cart);
    }

    /*@PostMapping("/new")
    public List<Cart> addNewCart(@RequestBody Cart cart) {
        service.addNewCart(cart);
        return service.getAllCarts();
    }*/

    @GetMapping("/update/{cartId}")
    public void showUpdateCart(@PathVariable long cartId, Model model) {
        model.addAttribute("cart",service.getCartById(cartId));
    }

    @PostMapping("/update")
    public String updateCart(Cart cart) {
        service.addNewCart(cart);
        return "redirect:/carts/"+cart.getCartId();
    }

    @GetMapping("/delete/{cartId}")
    public String deleteById(@PathVariable long cartId) {
        service.deleteCartById(cartId);
        return "redirect:/carts/all";
    }

}
