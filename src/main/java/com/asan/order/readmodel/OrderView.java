package com.asan.order.readmodel;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_view")
public class OrderView {

  @Id
  private Long id;
  private Long produkId;
  private String namaProduk;
  private Long pelangganId;
  private String namaPelanggan;
  private String emailPelanggan;
  private LocalDate tglTransaksi;
  private int jumlah;
  private double total;

  public OrderView() {
  }

  public OrderView(Long id, Long produkId, String namaProduk, Long pelangganId, String namaPelanggan,
      String emailPelanggan, LocalDate tglTransaksi, int jumlah, double total) {
    this.id = id;
    this.produkId = produkId;
    this.namaProduk = namaProduk;
    this.pelangganId = pelangganId;
    this.namaPelanggan = namaPelanggan;
    this.emailPelanggan = emailPelanggan;
    this.tglTransaksi = tglTransaksi;
    this.jumlah = jumlah;
    this.total = total;
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

  public String getNamaProduk() {
    return namaProduk;
  }

  public void setNamaProduk(String namaProduk) {
    this.namaProduk = namaProduk;
  }

  public Long getPelangganId() {
    return pelangganId;
  }

  public void setPelangganId(Long pelangganId) {
    this.pelangganId = pelangganId;
  }

  public String getNamaPelanggan() {
    return namaPelanggan;
  }

  public void setNamaPelanggan(String namaPelanggan) {
    this.namaPelanggan = namaPelanggan;
  }

  public String getEmailPelanggan() {
    return emailPelanggan;
  }

  public void setEmailPelanggan(String emailPelanggan) {
    this.emailPelanggan = emailPelanggan;
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
