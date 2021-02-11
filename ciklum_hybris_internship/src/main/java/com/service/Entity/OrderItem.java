package com.service.Entity;

import java.util.Objects;

public class OrderItem {

    private int productId;
    private int quantity;

    private OrderItem(Builder builder) {
        this.productId = builder.productId;
        this.quantity = builder.quantity;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return productId == orderItem.productId &&
                quantity == orderItem.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity);
    }

    public static class Builder {

        private int productId;
        private int quantity;

        public Builder() {

        }

        public Builder setProductId (int productId) {
            this.productId = productId;
            return this;
        }

        public Builder setQuantity (int quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
