package com.service.Entity;

import java.util.Objects;

public class Product {

    private String name;
    private int price;
    private String status;
    private String createdAt;

    public Product(Builder builder) {
        this.name = builder.name;
        this.price = builder.price;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", date='" + createdAt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return price == product.price &&
                Objects.equals(name, product.name) &&
                Objects.equals(status, product.status) &&
                Objects.equals(createdAt, product.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, status, createdAt);
    }

    public static class Builder {

        private String name;
        private int price;
        private String status;
        private String createdAt;

        public Builder() {
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPrice(int price) {
            this.price = price;
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

        public Product build() {
            return new Product(this);
        }
    }
}
