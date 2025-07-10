
# Shop Platform

This repository contains the microservice‑based e‑commerce demo **Shop Platform**.  
The diagram below illustrates the full happy‑path plus out‑of‑stock branch for a shopper moving from browsing products through paying with Stripe.

## Sequence diagram – end‑to‑end checkout flow

```mermaid
sequenceDiagram
    actor U  as User (browser)
    participant FE as Front‑end SPA
    participant GW as Gateway Service (WebSocket)
    participant OS as Order Service
    participant SS as Stock Service
    participant PS as Payment Service
    participant ST as Stripe Service
    participant DB as Database(s)

    %% ---------------- 1. Catalogue ----------------
    Note over U,FE: BROWSE PRODUCT CATALOGUE
    U ->> FE: Loads SPA & opens WebSocket
    FE ->> GW: STOMP /app/get-product-items
    GW ->> SS: ItemsForSellCmd
    SS ->> GW: ItemsForSell evt
    GW ->> FE: /topic/events (product list)

    %% ---------------- 2. Create Order ----------------
    Note over U,FE: CREATE ORDER
    U ->> FE: Clicks "MAKE ORDER"
    FE ->> GW: /app/make-order (OrderItems)
    GW ->> OS: CreateOrder evt
    OS -->> DB: Save Order (NEW)
    OS ->> GW: OrderAccepted evt (UI show)
    GW ->> FE: /topic/events (status=CREATED)
    OS ->> SS: ReserveStock(orderId, items)

    %% ----- 2a. Stock reserved branch -----
    alt stock item RESERVED
        Note over FE,DB: RESERVED
        SS -->> DB: Decrease available qty of Stock item + Create Reservation item (status=RESERVED)
        SS ->> GW: StockItemUpdateQty evt (UI show)
        GW ->> FE: /topic/events (decrease available qty by sku)
        SS ->> OS: ConfirmationReservation evt
        OS -->> DB: Update Order -> status=RESERVED + resolve PAY_UNTIL_TIME
        OS ->> GW: OrderReserved evt (UI show)
        GW ->> FE: /topic/events (status=RESERVED)
        OS ->> PS: ConfirmationReservation evt
        PS -->> DB: Create paymentCheckoutLink and save Payment (NEW)
        PS ->> OS: CheckoutPaymentLink evt
        OS -->> DB: Update Order with checkoutPaymentLink
        OS ->> GW: OrderPaymentLinkUpdate evt
        GW ->> FE: /topic/events (render BUTTON PAY with checkoutPaymentLink)
        
        Note over U,FE: PAY ORDER
        U  ->> ST: Clicks "PAY" and redirects to Stripe by checkoutPaymentLink
        ST -->> U: Redirects back to FE to "gateway-service/payment-confirmation.html"
        ST ->> PS: Webhook: "checkout.session.completed"
        PS -->> DB: Update Payment -> status=SUCCEEDED
        PS ->> OS: PaymentSuccessful evt
        OS -->> DB: Update Order -> status=PAID
        OS ->> GW: OrderPaid evt
        GW ->> FE: /topic/events (status=PAID)
    %% ----- 2b. Stock OUT_OF_STOCK branch -----
    else stock item OUT_OF_STOCK
        Note over FE,DB: OUT OF STOCK
        SS ->> OS: OutOfStock evt
        OS -->> DB: Update Order -> status=CANCELLED, cancelReason=OUT_OF_STOCK
        OS ->> GW: OrderCancelled evt
        GW ->> FE: /topic/events (status=CANCELLED, details=OUT_OF_STOCK)
    end
    Note over FE,DB: PAYMENT EXPIRED
    OS -->> DB: Update Order -> status=CANCELLED, cancelReason=NOT_PAID
    OS ->> GW: OrderCancelled evt
    GW ->> FE: /topic/events (status=CANCELLED, details=NOT_PAID)
    OS ->> SS: StockRelease evt
    SS -->> DB: Increase available qty of Stock item + Update Reservation item (status=RELEASED)
    SS ->> GW: StockItemUpdateQty evt (UI show)
    GW ->> FE: /topic/events (increase available qty by sku)
```
