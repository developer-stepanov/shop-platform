
# Shop Platform

This repository contains the microservice‑based e‑commerce demo **Shop Platform**.  
The diagram below illustrates the full happy‑path plus out‑of‑stock branch for a shopper moving from browsing products through paying with Stripe.

## Sequence diagram – end‑to‑end checkout flow

```mermaid
sequenceDiagram
    actor U  as User (browser)
    participant FE as Front‑end SPA
    participant GW as Gateway Service (WebSocket)
    participant K  as Kafka Bus
    participant SS as Stock Service
    participant OS as Order Service
    participant PS as Payment Service
    participant DB as Database(s)

    %% ---------------- 1. Catalogue ----------------
    Note over U,FE: Browse product catalogue
    U ->> FE: Loads SPA & opens WebSocket
    FE ->> GW: STOMP /app/get-product-items
    GW ->> K: ItemsForSellCmd
    K  ->> SS: ItemsForSellCmd
    SS ->> K: ItemsForSell evt
    K  ->> GW: ItemsForSell evt
    GW ->> FE: /topic/events (product list)

    %% ---------------- 2. Create Order ----------------
    Note over U,FE: Create order
    U ->> FE: Clicks "MAKE ORDER"
    FE ->> GW: /app/make-order (OrderItems)
    GW ->> K: CreateOrder cmd
    K  ->> OS: CreateOrder cmd
    OS ->> DB: Save Order (NEW)
    OS ->> SS: ReserveStock(orderId, items)

    %% ----- 2a. Stock reserved branch -----
    alt Stock reserved
        SS ->> DB: Decrease qty / reserve
        SS -->> OS: ConfirmationReservation
        OS ->> DB: Update Order → RESERVED
        OS -->> PS: InitiatePayment(orderId, amount)
        PS -->> OS: CheckoutPaymentLink(checkoutUrl)
        OS ->> DB: Update Order → AWAIT_PAYMENT
        OS ->> K: CheckoutPaymentLink evt
        K  ->> GW: CheckoutPaymentLink evt
        GW ->> FE: /topic/events (checkoutUrl)
        U  ->> PS: Completes payment (Stripe)
        PS ->> K: PaymentSuccessful evt
        K  ->> OS: PaymentSuccessful
        OS ->> DB: Update Order → PAID
        OS ->> K: OrderUpdated evt
        K  ->> GW: OrderUpdated evt
        GW ->> FE: /topic/events (status=PAID)
    else Out of stock
        SS -->> OS: OutOfStock
        OS ->> DB: Update Order → CANCELED
        OS ->> K: OutOfStock evt
        K  ->> GW: OutOfStock evt
        GW ->> FE: /topic/events (out‑of‑stock)
    end
```

_Re-render this diagram automatically on GitHub, GitLab, or any viewer that supports Mermaid._
