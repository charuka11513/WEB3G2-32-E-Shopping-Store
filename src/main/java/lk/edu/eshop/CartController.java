package lk.edu.eshop;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.*;

@Controller
public class CartController {
    private final ProductRepository products;
    private final OrderService service;
    public CartController(ProductRepository products, OrderService service) {
        this.products = products;
        this.service = service;
    }
    @SuppressWarnings("unchecked")
    private Map<Long,Integer> cart(HttpSession session) {
        Object stored = session.getAttribute("cart");
        if (stored instanceof Map<?,?>) return (Map<Long,Integer>) stored;
        Map<Long,Integer> cart = new LinkedHashMap<>();
        session.setAttribute("cart",cart);
        return cart;
    }
    public record CartRow(Product product, int quantity, BigDecimal subtotal) {}
    @ModelAttribute("cartCount")
    public int cartCount(HttpSession session) { return cart(session).values().stream().mapToInt(Integer::intValue).sum(); }
    @PostMapping("/cart/add/{id}")
    public String add(@PathVariable Long id, @RequestParam(defaultValue="1") int quantity, HttpSession session, RedirectAttributes flash) {
        Product product = products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found"));
        if(quantity<1 || quantity>100 || product.stock<1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid quantity");
        Map<Long,Integer> cart = cart(session);
        int newQty = cart.getOrDefault(id,0)+quantity;
        if(newQty>product.stock) { flash.addFlashAttribute("error","Only "+product.stock+" units available for "+product.name); return "redirect:/cart"; }
        cart.put(id,newQty);
        flash.addFlashAttribute("message",product.name+" added to your cart");
        return "redirect:/cart";
    }
    @GetMapping("/cart")
    public String view(Model model, HttpSession session) {
        List<CartRow> rows = new ArrayList<>();
        BigDecimal total=BigDecimal.ZERO;
        for(var entry:cart(session).entrySet()) {
            Optional<Product> found=products.findById(entry.getKey());
            if(found.isPresent()) {
                Product product=found.get();
                BigDecimal subtotal=product.price.multiply(BigDecimal.valueOf(entry.getValue()));
                rows.add(new CartRow(product,entry.getValue(),subtotal));
                total=total.add(subtotal);
            }
        }
        model.addAttribute("rows",rows);
        model.addAttribute("total",total);
        return "cart";
    }
    @PostMapping("/cart/update/{id}")
    public String update(@PathVariable Long id, @RequestParam int quantity, HttpSession session, RedirectAttributes flash) {
        Map<Long,Integer> cart=cart(session);
        if(quantity<=0) cart.remove(id);
        else {
            Product product=products.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
            if(quantity>product.stock || quantity>100) flash.addFlashAttribute("error","Not enough stock for "+product.name);
            else cart.put(id,quantity);
        }
        return "redirect:/cart";
    }
    @PostMapping("/cart/remove/{id}")
    public String remove(@PathVariable Long id,HttpSession session) {cart(session).remove(id);return "redirect:/cart";}
    @GetMapping("/checkout")
    public String checkoutPage(Model model,HttpSession session) {
        view(model,session);
        if(((List<?>)model.getAttribute("rows")).isEmpty()) return "redirect:/cart";
        return "checkout";
    }
    @PostMapping("/cart/checkout")
    public String placeOrder(@RequestParam String customerName,@RequestParam String email,@RequestParam String address,
                             HttpSession session,RedirectAttributes flash) {
        Map<Long,Integer> cart=cart(session);
        if(cart.isEmpty()) return "redirect:/cart";
        List<OrderService.Line> lines=cart.entrySet().stream().map(e->new OrderService.Line(e.getKey(),e.getValue())).toList();
        try {
            CustomerOrder order=service.checkout(new OrderService.Checkout(customerName,email,address,lines));
            cart.clear();
            flash.addFlashAttribute("orderId",order.id);
            flash.addFlashAttribute("orderTotal",order.total);
            return "redirect:/order-success";
        } catch(ResponseStatusException ex) {
            flash.addFlashAttribute("error",ex.getReason());
            return "redirect:/checkout";
        }
    }
    @GetMapping("/order-success") public String success(){return "success";}
}
