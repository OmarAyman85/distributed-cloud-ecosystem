# Payment Service

The **Payment Service** is a shared utility within the Distributed Infrastructure ecosystem designed to handle secure financial transactions using the **Paymob Unified Checkout** integration.

## Features

- **Paymob Integration**: Full support for Paymob's Unified Checkout flow.
- **Transaction Initiation**: Securely creates payment intentions and provides redirect URLs for clients.
- **Callback Verification**: Robust HMAC-based verification for payment notifications (webhooks).
- **Stateless & Scalable**: Designed to handle high-concurrency payment requests.

## Architecture

The service follows a clean, feature-based package structure:

- `com.ayman.distributed.payment.features.paymob.controller`: REST endpoints for payment operations.
- `com.ayman.distributed.payment.features.paymob.service`: Core logic for interacting with Paymob APIs.
- `com.ayman.distributed.payment.features.paymob.dto`: Data transfer objects for requests and responses.

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- A valid Paymob account and API keys.

### Configuration

Update your `application.yml` or environment variables with your Paymob credentials:

```yaml
paymob:
  api:
    base-url: https://accept.paymob.com
    intention-endpoint: /v1/intention/
  secret-key: ${PAYMOB_SECRET_KEY}
  public-key: ${PAYMOB_PUBLIC_KEY}
```

## API Endpoints

| Method | Endpoint                 | Description                                       |
| :----- | :----------------------- | :------------------------------------------------ |
| `POST` | `/api/payments/initiate` | Initiates a payment and returns the redirect URL. |
| `POST` | `/api/payments/callback` | Handles payment status updates from Paymob.       |

---

_Part of the Distributed Cloud Infrastructure._
