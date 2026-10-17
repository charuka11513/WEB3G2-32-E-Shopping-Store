package lk.edu.eshop;
import org.springframework.stereotype.Component;
@Component public class NotificationObserver implements OrderObserver { public void onStatusChange(CustomerOrder order){System.out.println("ORDER NOTIFICATION: #"+order.id+" status="+order.status+" for "+order.email);} }
