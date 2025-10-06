-- ============================================================================
-- INTERPAY PAYMENT SYSTEM - DATABASE SCHEMA
-- Database-per-Service Pattern with PostgreSQL, Cassandra, and Redis
-- ============================================================================

-- ============================================================================
-- 1. USER SERVICE DATABASE (PostgreSQL)
-- ============================================================================

CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED, CLOSED
    account_type VARCHAR(20) NOT NULL, -- PERSONAL, BUSINESS, MERCHANT
    kyc_status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, VERIFIED, REJECTED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status (status)
);

CREATE TABLE user_profiles (
    profile_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    country_code VARCHAR(3) NOT NULL,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

CREATE TABLE kyc_documents (
    document_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL, -- PASSPORT, DRIVERS_LICENSE, NATIONAL_ID
    document_number VARCHAR(100) NOT NULL,
    document_url VARCHAR(500),
    verification_status VARCHAR(20) DEFAULT 'PENDING',
    verified_at TIMESTAMP,
    verified_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (verification_status)
);

-- ============================================================================
-- 2. WALLET SERVICE DATABASE (PostgreSQL)
-- ============================================================================

CREATE TABLE wallets (
    wallet_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    currency_code VARCHAR(3) NOT NULL DEFAULT 'USD',
    balance DECIMAL(20, 4) NOT NULL DEFAULT 0.0000,
    available_balance DECIMAL(20, 4) NOT NULL DEFAULT 0.0000,
    pending_balance DECIMAL(20, 4) NOT NULL DEFAULT 0.0000,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, FROZEN, CLOSED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1, -- For optimistic locking
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    CONSTRAINT balance_non_negative CHECK (balance >= 0),
    CONSTRAINT available_balance_non_negative CHECK (available_balance >= 0)
);

CREATE TABLE wallet_transactions (
    transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id UUID NOT NULL,
    transaction_type VARCHAR(30) NOT NULL, -- CREDIT, DEBIT, HOLD, RELEASE
    amount DECIMAL(20, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    balance_before DECIMAL(20, 4) NOT NULL,
    balance_after DECIMAL(20, 4) NOT NULL,
    reference_id UUID, -- Links to payment or transfer
    reference_type VARCHAR(50), -- PAYMENT, TRANSFER, REFUND, FEE
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id),
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_reference (reference_id, reference_type),
    INDEX idx_created_at (created_at),
    INDEX idx_transaction_type (transaction_type)
);

CREATE TABLE wallet_holds (
    hold_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id UUID NOT NULL,
    amount DECIMAL(20, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    reference_id UUID NOT NULL,
    reference_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, RELEASED, CAPTURED
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    released_at TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id),
    INDEX idx_wallet_id (wallet_id),
    INDEX idx_status (status),
    INDEX idx_reference (reference_id),
    INDEX idx_expires_at (expires_at)
);

-- ============================================================================
-- 3. PAYMENT SERVICE DATABASE (PostgreSQL)
-- ============================================================================

CREATE TABLE payments (
    payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL,
    customer_id UUID, -- NULL for guest payments
    payment_method VARCHAR(30) NOT NULL, -- WALLET, CARD, GUEST
    amount DECIMAL(20, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(30) NOT NULL DEFAULT 'INITIATED', -- INITIATED, PENDING, AUTHORIZED, CAPTURED, FAILED, CANCELLED, REFUNDED
    payment_gateway VARCHAR(50), -- INTERPAY, STRIPE_FALLBACK
    merchant_order_id VARCHAR(100),
    description TEXT,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_merchant_order (merchant_id, merchant_order_id)
);

CREATE TABLE payment_methods (
    payment_method_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    method_type VARCHAR(30) NOT NULL, -- CARD, BANK_ACCOUNT, WALLET
    is_default BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);

CREATE TABLE cards (
    card_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_method_id UUID NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    card_token VARCHAR(255) NOT NULL, -- Tokenized card number
    card_brand VARCHAR(20) NOT NULL, -- VISA, MASTERCARD, AMEX
    last_four VARCHAR(4) NOT NULL,
    expiry_month INTEGER NOT NULL,
    expiry_year INTEGER NOT NULL,
    cardholder_name VARCHAR(200),
    billing_address_id UUID,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods(payment_method_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_card_token (card_token)
);

CREATE TABLE payment_transactions (
    transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id UUID NOT NULL,
    transaction_type VARCHAR(30) NOT NULL, -- AUTHORIZE, CAPTURE, VOID, REFUND
    amount DECIMAL(20, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL, -- SUCCESS, FAILED, PENDING
    gateway_transaction_id VARCHAR(255),
    gateway_response JSONB,
    error_code VARCHAR(50),
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id),
    INDEX idx_payment_id (payment_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

CREATE TABLE refunds (
    refund_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    payment_id UUID NOT NULL,
    amount DECIMAL(20, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    reason VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, COMPLETED, FAILED
    initiated_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id),
    INDEX idx_payment_id (payment_id),
    INDEX idx_status (status)
);

-- ============================================================================
-- 4. TRANSFER SERVICE DATABASE (PostgreSQL)
-- ============================================================================

CREATE TABLE transfers (
    transfer_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_wallet_id UUID NOT NULL,
    receiver_wallet_id UUID NOT NULL,
    amount DECIMAL(20, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    fee_amount DECIMAL(20, 4) DEFAULT 0.0000,
    status VARCHAR(30) NOT NULL DEFAULT 'INITIATED', -- INITIATED, PENDING, COMPLETED, FAILED, REVERSED
    transfer_type VARCHAR(30) NOT NULL, -- P2P, PAYOUT, REFUND
    description TEXT,
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    INDEX idx_sender_wallet (sender_wallet_id),
    INDEX idx_receiver_wallet (receiver_wallet_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- ============================================================================
-- 5. MERCHANT SERVICE DATABASE (PostgreSQL)
-- ============================================================================

CREATE TABLE merchants (
    merchant_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    business_name VARCHAR(255) NOT NULL,
    business_type VARCHAR(50) NOT NULL,
    business_category VARCHAR(100),
    website_url VARCHAR(500),
    tax_id VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, SUSPENDED, REJECTED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
);

CREATE TABLE merchant_api_keys (
    api_key_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL,
    key_name VARCHAR(100) NOT NULL,
    public_key VARCHAR(255) UNIQUE NOT NULL,
    secret_key_hash VARCHAR(255) NOT NULL,
    environment VARCHAR(20) NOT NULL, -- SANDBOX, PRODUCTION
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    permissions JSONB,
    last_used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id) ON DELETE CASCADE,
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_public_key (public_key)
);

CREATE TABLE merchant_settlements (
    settlement_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL,
    amount DECIMAL(20, 4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    fee_amount DECIMAL(20, 4) NOT NULL,
    net_amount DECIMAL(20, 4) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, COMPLETED, FAILED
    settlement_date DATE NOT NULL,
    bank_reference VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    FOREIGN KEY (merchant_id) REFERENCES merchants(merchant_id),
    INDEX idx_merchant_id (merchant_id),
    INDEX idx_settlement_date (settlement_date),
    INDEX idx_status (status)
);

-- ============================================================================
-- 6. FRAUD DETECTION SERVICE DATABASE (PostgreSQL)
-- ============================================================================

CREATE TABLE fraud_rules (
    rule_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_name VARCHAR(100) NOT NULL,
    rule_type VARCHAR(50) NOT NULL, -- VELOCITY, AMOUNT, LOCATION, DEVICE, PATTERN
    conditions JSONB NOT NULL,
    action VARCHAR(30) NOT NULL, -- BLOCK, REVIEW, ALLOW_WITH_MFA
    priority INTEGER NOT NULL DEFAULT 100,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_rule_type (rule_type),
    INDEX idx_priority (priority)
);

CREATE TABLE fraud_checks (
    check_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_id UUID NOT NULL, -- payment_id, transfer_id, etc
    entity_type VARCHAR(50) NOT NULL,
    risk_score DECIMAL(5, 2) NOT NULL,
    risk_level VARCHAR(20) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL
    decision VARCHAR(30) NOT NULL, -- APPROVED, DECLINED, REVIEW
    triggered_rules JSONB,
    ip_address INET,
    device_fingerprint VARCHAR(255),
    user_agent TEXT,
    geolocation JSONB,
    checked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_entity (entity_id, entity_type),
    INDEX idx_risk_level (risk_level),
    INDEX idx_decision (decision),
    INDEX idx_checked_at (checked_at)
);

CREATE TABLE blacklist (
    blacklist_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(50) NOT NULL, -- EMAIL, IP, CARD, DEVICE
    entity_value VARCHAR(500) NOT NULL,
    reason TEXT,
    added_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    INDEX idx_entity (entity_type, entity_value)
);

-- ============================================================================
-- 7. NOTIFICATION SERVICE DATABASE (PostgreSQL)
-- ============================================================================

CREATE TABLE notifications (
    notification_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    notification_type VARCHAR(50) NOT NULL, -- EMAIL, SMS, PUSH, IN_APP
    template_id VARCHAR(100) NOT NULL,
    subject VARCHAR(255),
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT, FAILED
    priority VARCHAR(20) DEFAULT 'NORMAL',
    metadata JSONB,
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_scheduled_at (scheduled_at)
);

-- ============================================================================
-- 8. AUDIT SERVICE DATABASE (PostgreSQL)
-- ============================================================================

CREATE TABLE audit_logs (
    audit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    service_name VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id UUID,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    status VARCHAR(20), -- SUCCESS, FAILURE
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_created_at (created_at),
    INDEX idx_service (service_name)
);

-- ============================================================================
-- CASSANDRA SCHEMAS (CQL)
-- ============================================================================

-- Session Store
CREATE KEYSPACE IF NOT EXISTS interpay_sessions
WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': 3};

CREATE TABLE interpay_sessions.user_sessions (
    session_id UUID,
    user_id UUID,
    device_id TEXT,
    ip_address TEXT,
    user_agent TEXT,
    created_at TIMESTAMP,
    last_activity TIMESTAMP,
    expires_at TIMESTAMP,
    session_data MAP<TEXT, TEXT>,
    PRIMARY KEY (session_id)
) WITH default_time_to_live = 86400
AND gc_grace_seconds = 86400;

CREATE INDEX ON interpay_sessions.user_sessions (user_id);

-- Activity Logs
CREATE KEYSPACE IF NOT EXISTS interpay_logs
WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': 3};

CREATE TABLE interpay_logs.payment_logs (
    payment_id UUID,
    log_timestamp TIMESTAMP,
    log_level TEXT,
    service_name TEXT,
    message TEXT,
    metadata MAP<TEXT, TEXT>,
    PRIMARY KEY (payment_id, log_timestamp)
) WITH CLUSTERING ORDER BY (log_timestamp DESC)
AND default_time_to_live = 2592000; -- 30 days

CREATE TABLE interpay_logs.api_request_logs (
    request_id UUID,
    timestamp TIMESTAMP,
    merchant_id UUID,
    endpoint TEXT,
    method TEXT,
    request_body TEXT,
    response_body TEXT,
    response_code INT,
    duration_ms INT,
    ip_address TEXT,
    PRIMARY KEY ((merchant_id), timestamp, request_id)
) WITH CLUSTERING ORDER BY (timestamp DESC)
AND default_time_to_live = 7776000; -- 90 days

CREATE TABLE interpay_logs.fraud_events (
    event_id UUID,
    timestamp TIMESTAMP,
    user_id UUID,
    event_type TEXT,
    risk_score DOUBLE,
    details MAP<TEXT, TEXT>,
    PRIMARY KEY ((user_id), timestamp, event_id)
) WITH CLUSTERING ORDER BY (timestamp DESC);

-- Transaction Analytics (Time-series data)
CREATE TABLE interpay_logs.transaction_metrics (
    metric_date DATE,
    hour INT,
    merchant_id UUID,
    transaction_count COUNTER,
    total_amount COUNTER,
    successful_count COUNTER,
    failed_count COUNTER,
    PRIMARY KEY ((metric_date, hour), merchant_id)
);

-- ============================================================================
-- REDIS CACHE STRUCTURES (Pseudo-schema for documentation)
-- ============================================================================

-- Key Patterns:
-- user:{user_id}:profile - User profile cache (TTL: 1 hour)
-- wallet:{wallet_id}:balance - Wallet balance cache (TTL: 5 minutes)
-- payment:{payment_id}:status - Payment status cache (TTL: 10 minutes)
-- session:{session_id} - User session data (TTL: 24 hours)
-- fraud:velocity:{user_id}:{timeframe} - Velocity check counters (TTL: variable)
-- api:ratelimit:{merchant_id}:{minute} - API rate limiting (TTL: 1 minute)
-- merchant:{merchant_id}:apikey:{key} - API key validation cache (TTL: 1 hour)
-- card:{card_token}:metadata - Card metadata cache (TTL: 30 minutes)
-- transfer:{transfer_id}:lock - Distributed lock for transfers (TTL: 30 seconds)

-- Redis Data Structures Used:
-- STRING: Simple key-value for caching
-- HASH: Complex objects like user profiles
-- SORTED SET: Leaderboards, recent transactions
-- SET: Unique collections (blacklisted IPs)
-- LIST: Recent activity feeds
-- BITMAP: Daily active users tracking
-- HyperLogLog: Unique visitor counts

-- ============================================================================
-- INDEXES AND PERFORMANCE OPTIMIZATION
-- ============================================================================

-- Additional composite indexes for common query patterns
CREATE INDEX idx_payments_merchant_status_created ON payments(merchant_id, status, created_at);
CREATE INDEX idx_wallet_transactions_wallet_created ON wallet_transactions(wallet_id, created_at DESC);
CREATE INDEX idx_transfers_status_created ON transfers(status, created_at);
CREATE INDEX idx_fraud_checks_risk_checked ON fraud_checks(risk_level, checked_at);

-- Partitioning strategy for large tables (PostgreSQL 12+)
-- Partition wallet_transactions by created_at (monthly)
-- Partition audit_logs by created_at (monthly)
-- Partition payment_transactions by created_at (monthly)

-- ============================================================================
-- MATERIALIZED VIEWS FOR ANALYTICS
-- ============================================================================

CREATE MATERIALIZED VIEW merchant_daily_summary AS
SELECT 
    merchant_id,
    DATE(created_at) as transaction_date,
    COUNT(*) as transaction_count,
    SUM(amount) as total_amount,
    SUM(CASE WHEN status = 'CAPTURED' THEN amount ELSE 0 END) as successful_amount,
    COUNT(CASE WHEN status = 'FAILED' THEN 1 END) as failed_count
FROM payments
GROUP BY merchant_id, DATE(created_at)
WITH DATA;

CREATE UNIQUE INDEX ON merchant_daily_summary(merchant_id, transaction_date);

-- ============================================================================
-- TRIGGERS FOR AUDIT AND CONSISTENCY
-- ============================================================================

CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_timestamp
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_wallets_timestamp
BEFORE UPDATE ON wallets
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_payments_timestamp
BEFORE UPDATE ON payments
FOR EACH ROW EXECUTE FUNCTION update_timestamp();