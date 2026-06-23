# Inventory & Order Management API

## How to Run

- **Requirements:** Java 17, Maven, Oracle Database
- **Database setup:** Connect to `orclpdb`, create user `inventory_user` with password `inventory123`
- **Run:** `mvn spring-boot:run`
- **Server starts on:** http://localhost:8080

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/categories` | Create a new category |
| POST | `/api/products` | Create a new product |
| POST | `/api/products/{productId}/stock-adjustment` | Add or subtract stock for a product |
| GET | `/api/products/low-stock?threshold=` | Get products with stock below threshold |
| POST | `/api/customers` | Create a new customer |
| POST | `/api/customers/{customerId}/orders` | Create a new DRAFT order for a customer |
| POST | `/api/orders/{orderId}/items` | Add an item to a DRAFT order |
| DELETE | `/api/orders/{orderId}/items/{itemId}` | Remove an item from a DRAFT order |
| POST | `/api/orders/{orderId}/confirm` | Confirm a DRAFT order (deducts stock) |
| POST | `/api/orders/{orderId}/cancel` | Cancel a DRAFT or CONFIRMED order |
| POST | `/api/orders/{orderId}/status` | Update order status (CONFIRMED→SHIPPED, SHIPPED→DELIVERED) |
| GET | `/api/orders/{orderId}` | Get order details by ID |

## Business Rules

1. Stock is **not** deducted when adding items to a DRAFT order
2. Stock **is** deducted atomically only when the order is CONFIRMED
3. If any item has insufficient stock at confirmation, the entire order is rejected with `409 Conflict`
4. Cancelling a CONFIRMED order restores all stock back to the products
5. Order status flow: `DRAFT → CONFIRMED → SHIPPED → DELIVERED` (or `CONFIRMED → CANCELLED`)

## Assumptions Made

1. `unit_price` is locked at confirmation time — price changes after confirmation do not affect existing orders
2. No authentication required as per assignment spec
3. Email must be unique per customer
4. Stock quantity cannot go below 0

## Database Schema

5 tables managed by Hibernate (`spring.jpa.hibernate.ddl-auto=update`):

| Table | Description |
|-------|-------------|
| `categories` | Product categories |
| `products` | Products with price and stock quantity |
| `customers` | Customers with unique email |
| `orders` | Orders linked to a customer with status and total price |
| `order_items` | Line items linking an order to a product with quantity and locked unit price |

All relationships enforced via foreign keys in Oracle DB.
