package com.asan.order.vo;

import java.time.LocalDate;

import com.asan.order.entity.Order;

public class OrderMessage {

  private Long id;
  private Long produkId;
  private Long pelangganId;
  private String email;
  private LocalDate tglTransaksi;
  private int jumlah;
  private double total;

  public OrderMessage() {
  }

  public OrderMessage(Order order, String email) {
    this.id = order.getId();
    this.produkId = order.getProdukId();
    this.pelangganId = order.getPelangganId();
    this.email = email;
    this.tglTransaksi = order.getTglTransaksi();
    this.jumlah = order.getJumlah();
    this.total = order.getTotal();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getProdukId() {
    return produkId;
  }

  public void setProdukId(Long produkId) {
    this.produkId = produkId;
  }

  public Long getPelangganId() {
    return pelangganId;
  }

  public void setPelangganId(Long pelangganId) {
    this.pelangganId = pelangganId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDate getTglTransaksi() {
    return tglTransaksi;
  }

  public void setTglTransaksi(LocalDate tglTransaksi) {
    this.tglTransaksi = tglTransaksi;
  }

  public int getJumlah() {
    return jumlah;
  }

  public void setJumlah(int jumlah) {
    this.jumlah = jumlah;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }
}
