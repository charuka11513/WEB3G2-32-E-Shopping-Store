# WEB3G2-32 Market — SE2030 educational project

## Requirements
Java 17+ (Java 21 supported), Maven 3.9.x, Windows/Linux/macOS.

## Run
From the `source` directory containing `pom.xml`:

```bash
mvn clean spring-boot:run
```

- Storefront: http://localhost:8080/
- Cart: http://localhost:8080/cart
- Checkout: http://localhost:8080/checkout (after adding products)
- Admin: http://localhost:8080/admin

## Demo walkthrough
1. Add 2+ products using `/admin`.
2. From the storefront add different products to the cart.
3. Review `/cart`; update quantities or remove an item.
4. Complete checkout once for all cart items.
5. View orders and update statuses from `/admin`.
6. Restart the application to confirm H2 database persistence.

## Architecture
Spring Boot MVC + Thymeleaf + Spring Data JPA + file-based H2 database. HTTP session cart stores product IDs and quantities; the server calculates totals from database prices. `OrderService` creates one order with multiple order items in a transaction. Existing Observer and Singleton examples remain.

## Important limitations
- Educational prototype; no real payment integration or delivery service.
- `/admin` is **not authenticated**. Do not deploy publicly or use real customer data.
- No user accounts, real reviews, shipping tracking, tax/shipping calculation, or product photo upload.
- Session cart is browser-session-scoped and may be lost when the session expires.
- Stock concurrency protection and advanced security controls need additional work before production use.
- The original single-product `/checkout` POST endpoint remains for backwards compatibility; the new interface uses `/cart/checkout`.
- Product images are deliberately represented by category initials, not misleading stock photography.

## Submission
Test locally and capture genuine screenshots before submitting. Do not claim unimplemented features or testing outcomes. Exclude `data/`, `target/`, `.env` and secrets from GitHub.
