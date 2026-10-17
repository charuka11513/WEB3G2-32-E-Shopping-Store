package lk.edu.eshop;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.math.BigDecimal;
import java.util.*;
@Service public class OrderService {
 private final ProductRepository products; private final OrderRepository orders; private final OrderItemRepository items; private final List<OrderObserver> observers;
 public OrderService(ProductRepository p,OrderRepository o,OrderItemRepository i,List<OrderObserver> obs){products=p;orders=o;items=i;observers=obs;}
 public record Line(Long productId,int quantity){}
 public record Checkout(String customerName,String email,String address,List<Line> lines){}
 @Transactional public CustomerOrder checkout(Checkout input){
 if(input==null||blank(input.customerName())||blank(input.email())||!input.email().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")||blank(input.address())||input.lines()==null||input.lines().isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Complete all checkout fields and add products");
 Map<Long,Integer> qty=new LinkedHashMap<>(); for(Line l:input.lines()){if(l==null||l.productId()==null||l.quantity()<1)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid cart quantity");qty.merge(l.productId(),l.quantity(),Integer::sum);}
 BigDecimal total=BigDecimal.ZERO;List<OrderItem> pending=new ArrayList<>();
 for(var entry:qty.entrySet()){Product p=products.findById(entry.getKey()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Product missing")); if(p.stock<entry.getValue())throw new ResponseStatusException(HttpStatus.CONFLICT,"Not enough stock for "+p.name);total=total.add(p.price.multiply(BigDecimal.valueOf(entry.getValue())));OrderItem item=new OrderItem();item.productId=p.id;item.productName=p.name;item.quantity=entry.getValue();item.unitPrice=p.price;pending.add(item);p.stock-=entry.getValue();products.save(p);}
 CustomerOrder order=new CustomerOrder();order.customerName=input.customerName().trim();order.email=input.email().trim();order.address=input.address().trim();order.total=total;order.status="PLACED";order=orders.save(order);for(OrderItem item:pending){item.orderId=order.id;items.save(item);}notifyObservers(order);return order;
 }
 @Transactional public CustomerOrder updateStatus(Long id,String status){if(!List.of("PLACED","PROCESSING","SHIPPED","DELIVERED","CANCELLED").contains(status))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid status");CustomerOrder order=orders.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found"));if("CANCELLED".equals(order.status)||"DELIVERED".equals(order.status))throw new ResponseStatusException(HttpStatus.CONFLICT,"Cancelled order is final");if("CANCELLED".equals(status)){for(OrderItem item:items.findByOrderId(id)){products.findById(item.productId).ifPresent(p->{p.stock+=item.quantity;products.save(p);});}}order.status=status;order=orders.save(order);notifyObservers(order);return order;}
 private void notifyObservers(CustomerOrder o){for(OrderObserver observer:observers)observer.onStatusChange(o);}
 private boolean blank(String s){return s==null||s.isBlank();}
}
