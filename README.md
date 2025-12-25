# Spring Security JWT (JSON Web Token) Architecture

Implementasi keamanan REST API berbasis **Stateless Authentication** menggunakan Spring Boot dan JWT. Proyek ini mendemonstrasikan bagaimana mengamankan endpoint tanpa menggunakan session server tradisional.

![Architecture Diagram](https://github.com/Harisny/spring-jwt/blob/main/image/JWT.png?raw=true)

---

## Komponen Utama Arsitektur

Dalam ekosistem Spring Security, integrasi JWT dilakukan melalui komponen-komponen kunci berikut:

* **Security Filter Chain**: Rangkaian filter keamanan yang berfungsi sebagai *gatekeeper*. Setiap request yang masuk akan dicegat untuk diperiksa otorisasi dan autentikasinya sebelum mencapai Controller.
* **JwtAuthenticationFilter**: Filter kustom yang bertugas mengekstraksi token dari Header HTTP (`Authorization: Bearer <token>`). Filter ini memvalidasi integritas token dan menetapkan identitas pengguna ke dalam `SecurityContextHolder`.
* **AuthenticationManager**: Pusat koordinasi proses autentikasi. Komponen ini bertanggung jawab memverifikasi kredensial pengguna (username/password) pada saat proses login.
* **UserDetailsService**: Komponen abstraksi data yang bertugas mengambil informasi spesifik pengguna dari database berdasarkan username untuk keperluan validasi.
* **JwtUtils / JwtService**: Kelas utilitas yang menangani logika kriptografi untuk:
    * **Generate**: Membuat token baru setelah login berhasil.
    * **Extract**: Mengambil informasi (claims) dari token.
    * **Validate**: Memastikan token asli, belum expired, dan sesuai dengan user terkait.

---

## Struktur JSON Web Token (JWT)

Token yang dihasilkan mengikuti standar **RFC 7519** dan terdiri dari tiga bagian utama:

| Bagian | Deskripsi | Konten Umum |
| :--- | :--- | :--- |
| **Header** | Metadata Token | Mendefinisikan tipe token (JWT) dan algoritma hashing (misal: HS256). |
| **Payload** | Data Pengguna (Claims) | Berisi informasi subjek (`sub`), peran pengguna (`roles`), serta waktu diterbitkan (`iat`). |
| **Signature** | Verifikasi Keamanan | Hasil enkripsi gabungan Header dan Payload menggunakan *Secret Key* server untuk menjamin integritas data. |

---

## Alur Autentikasi

1.  **Client Login**: Mengirimkan username & password.
2.  **Server Validation**: `AuthenticationManager` memverifikasi data melalui `UserDetailsService`.
3.  **Token Generation**: Jika valid, `JwtUtils` membuat token dan dikirim kembali ke Client.
4.  **Authorized Access**: Client menyertakan token pada header di setiap request berikutnya.
5.  **Filter Interception**: `JwtAuthenticationFilter` memvalidasi token secara *stateless* dan memberikan akses jika valid.
