package com.asan.order.vo;

import java.time.LocalDate;

import com.asan.order.entity.Order;

public class OrderMessage {

  public static final String ORDER_CREATED = "ORDER_CREATED";
  public static final String ORDER_UPDATED = "ORDER_UPDATED";
  public static final String ORDER_DELETED = "ORDER_DELETED";

  private String eventType;
  private Long id;
  private Long produkId;
  private String produkNama;
  private Double produkHarga;
  private Long pelangganId;
  private String pelangganNama;
  private String pelangganAlamat;
  private String email;
  private LocalDate tglTransaksi;
  private int jumlah;
  private double total;

  public OrderMessage() {
  }

  public OrderMessage(Order order, String eventType, Produk produk, Pelanggan pelanggan) {
    this.eventType = eventType;
    this.id = order.getId();
    this.produkId = order.getProdukId();
    this.pelangganId = order.getPelangganId();
    this.email = pelanggan == null ? null : pelanggan.getEmail();
    this.produkNama = produk == null ? null : produk.getNama();
    this.produkHarga = produk == null ? null : produk.getHarga();
    this.pelangganNama = pelanggan == null ? null : pelanggan.getNama();
    this.pelangganAlamat = pelanggan == null ? null : pelanggan.getAlamat();
    this.tglTransaksi = order.getTglTransaksi();
    this.jumlah = order.getJumlah();
    this.total = order.getTotal();
  }

  public String getEventType() {
    return eventType;
  }

  public void setEventType(String eventType) {
    this.eventType = eventType;
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

  public String getProdukNama() {
    return produkNama;
  }

  public void setProdukNama(String produkNama) {
    this.produkNama = produkNama;
  }

  public Double getProdukHarga() {
    return produkHarga;
  }

  public void setProdukHarga(Double produkHarga) {
    this.produkHarga = produkHarga;
  }

  public Long getPelangganId() {
    return pelangganId;
  }

  public void setPelangganId(Long pelangganId) {
    this.pelangganId = pelangganId;
  }

  public String getPelangganNama() {
    return pelangganNama;
  }

  public void setPelangganNama(String pelangganNama) {
    this.pelangganNama = pelangganNama;
  }

  public String getPelangganAlamat() {
    return pelangganAlamat;
  }

  public void setPelangganAlamat(String pelangganAlamat) {
    this.pelangganAlamat = pelangganAlamat;
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
