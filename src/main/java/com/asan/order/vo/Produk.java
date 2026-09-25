package com.asan.order.vo;

public class Produk {
  private Long id;
  private String nama;
  private double harga;
  private String deskripsi;
  private Long idjenis;

  public Produk() {
  }

  public Produk(Long id, String nama, double harga, String deskripsi, Long idjenis) {
    this.id = id;
    this.nama = nama;
    this.harga = harga;
    this.deskripsi = deskripsi;
    this.idjenis = idjenis;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNama() {
    return nama;
  }

  public void setNama(String nama) {
    this.nama = nama;
  }

  public double getHarga() {
    return harga;
  }

  public void setHarga(double harga) {
    this.harga = harga;
  }

  public String getDeskripsi() {
    return deskripsi;
  }

  public void setDeskripsi(String deskripsi) {
    this.deskripsi = deskripsi;
  }

  public Long getIdJenis() {
    return idjenis;
  }

  public void setIdJenis(Long idjenis) {
    this.idjenis = idjenis;
  }
}
