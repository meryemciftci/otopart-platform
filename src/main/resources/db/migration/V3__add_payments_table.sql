CREATE TABLE payments (
                          id                BIGSERIAL PRIMARY KEY,
                          order_id          BIGINT NOT NULL UNIQUE REFERENCES orders(id),
                          method            VARCHAR(30) NOT NULL,
                          status            VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                          amount            NUMERIC(10,2) NOT NULL,
                          installment_count INT,
                          card_last_four    VARCHAR(4),
                          bank_name         VARCHAR(100),
                          transaction_id    VARCHAR(100),
                          reference_code    VARCHAR(50),
                          paid_at           TIMESTAMP,
                          failure_reason    TEXT,
                          active            BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at        TIMESTAMP NOT NULL DEFAULT NOW(),
                          created_by        VARCHAR(255),
                          updated_by        VARCHAR(255)
);

CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);