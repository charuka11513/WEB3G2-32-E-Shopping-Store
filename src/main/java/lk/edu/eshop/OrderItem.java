package lk.edu.eshop;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity public class OrderItem { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; public Long orderId; public Long productId; public String productName; public int quantity; public BigDecimal unitPrice; }
