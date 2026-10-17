package lk.edu.eshop;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity public class CustomerOrder { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; public String customerName; public String email; public String address; public String status; public BigDecimal total; public LocalDateTime createdAt=LocalDateTime.now(); }
