package com.service.Entity;

import java.util.Objects;

public class Order {
    private int userId;
    private String status;
    private String createdAt;

    public Order(Builder order) {
        this.userId = order.userId;
        this.status = order.status;
        this.createdAt = order.createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Order{" +
                "userId=" + userId +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return userId == order.userId &&
                Objects.equals(status, order.status) &&
                Objects.equals(createdAt, order.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, status, createdAt);
    }

    public static class Builder {

        private int userId;
        private String status;
        private String createdAt;

        public Builder() {
        }

        public Builder setUserId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setDate(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
