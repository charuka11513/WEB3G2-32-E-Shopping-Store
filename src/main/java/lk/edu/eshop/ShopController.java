package lk.edu.eshop;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
@Controller public class ShopController {
 private final ProductRepository products;private final OrderRepository orders;private final OrderService service;
 public ShopController(ProductRepository p,OrderRepository o,OrderService s){products=p;orders=o;service=s;}
 @GetMapping("/") public String home(Model m){m.addAttribute("products",products.findAll());m.addAttribute("store",StoreConfiguration.getInstance().storeName());return "index";}
 @GetMapping("/admin") public String admin(Model m){m.addAttribute("products",products.findAll());m.addAttribute("orders",orders.findAll());return "admin";}
 @PostMapping("/admin/products") public String add(@RequestParam String name,@RequestParam String category,@RequestParam BigDecimal price,@RequestParam int stock){if(name.isBlank()||category.isBlank()||price.signum()<=0||stock<0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid product");products.save(new Product(name,category,price,stock));return "redirect:/admin";}
 @PostMapping("/admin/products/{id}") public String edit(@PathVariable Long id,@RequestParam String name,@RequestParam String category,@RequestParam BigDecimal price,@RequestParam int stock){Product p=products.findById(id).orElseThrow();if(name.isBlank()||category.isBlank()||price.signum()<=0||stock<0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid product");p.name=name;p.category=category;p.price=price;p.stock=stock;products.save(p);return "redirect:/admin";}
 @PostMapping("/admin/products/{id}/delete") public String delete(@PathVariable Long id){products.deleteById(id);return "redirect:/admin";}
 @PostMapping("/admin/orders/{id}/status") public String status(@PathVariable Long id,@RequestParam String status){service.updateStatus(id,status);return "redirect:/admin";}
 @PostMapping("/checkout") public String checkout(@RequestParam String customerName,@RequestParam String email,@RequestParam String address,@RequestParam Long productId,@RequestParam int quantity,RedirectAttributes r){CustomerOrder o=service.checkout(new OrderService.Checkout(customerName,email,address,java.util.List.of(new OrderService.Line(productId,quantity))));r.addFlashAttribute("message","Order #"+o.id+" placed successfully. Total: LKR "+o.total);return "redirect:/";}
}
