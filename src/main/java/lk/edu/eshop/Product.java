package lk.edu.eshop;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
@Entity public class Product { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @NotBlank public String name; @NotBlank public String category; @NotNull @DecimalMin("0.01") public BigDecimal price; @Min(0) public int stock; public Product(){} public Product(String n,String c,BigDecimal p,int s){name=n;category=c;price=p;stock=s;} }
