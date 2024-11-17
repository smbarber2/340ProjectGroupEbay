package com._Project.Tbay.Cart;

import com._Project.Tbay.Listing.Listing;
import com._Project.Tbay.Listing.ListingService;
import com._Project.Tbay.User.User;
import com._Project.Tbay.User.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ListingService listingService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    public Cart getCartById(long cartId) { return cartRepository.findById(cartId).orElse(null);}

    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    public void deleteCartById(long cartId) {
        cartRepository.deleteById(cartId);
    }

    public List<Integer> getCartListing(long cartId){
        List<Integer> deserializedList = new ArrayList<>();

        try{
            Blob blobData = jdbcTemplate.queryForObject(
                    "SELECT cart_list FROM carts WHERE cart_id= ?",
                    new Object[]{cartId},
                    (rs, rowNum) -> rs.getBlob("cart_list")
            );
            if(blobData != null) {
                byte[] data = blobData.getBytes(1, (int) blobData.length());

                try(ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                    deserializedList = (ArrayList<Integer>) ois.readObject();
                }
            }
            else {
                logger.warn("No listings found for cart_id: {}", cartId);
            }
        } catch (Exception e){
            logger.error("Exception was thrown:",e);
        }
        return deserializedList;
    }

    public void addListing(long cartId, long listingId) {
        Cart existing = getCartById(cartId);
        List<Integer> list = existing.getCartList();
        list.add((int)listingId);
        existing.setCartList(list);
        existing.setTotalPrice(existing.getTotalPrice()+listingService.getListingById(listingId).getPrice());
        cartRepository.save(existing);
    }

    public void addNewCart(Cart cart){
        cartRepository.save(cart);
    }

    public void removeListing(long cartId, long listingId) {
        Cart existing = getCartById(cartId);
        List<Integer> list = existing.getCartList();
        list.remove(Integer.valueOf((int)listingId));
        existing.setCartList(list);
        existing.setTotalPrice(existing.getTotalPrice()-listingService.getListingById(listingId).getPrice());
        cartRepository.save(existing);
    }

}


