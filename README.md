# 🎵 Monopoly Distribuido: Edición Magnate de la Industria Musical

![Java Version](https://img.shields.io/badge/Java-25.0.2_LTS-blue.svg)
![Build](https://img.shields.io/badge/build-Maven-C71A36.svg)
![Status](https://img.shields.io/badge/Estado-En_Desarrollo-orange.svg)

## 📖 Descripción General
Este repositorio contiene el código fuente para el **Proyecto 1: Monopoly Distribuido con Estructuras Lineales**, desarrollado para el curso de Algoritmos y Estructuras de Datos 1 del Tecnológico de Costa Rica (TEC). 

Se trata de una versión simplificada del clásico Monopoly Electrónico, adaptada a una temática de la Industria Musical. El proyecto destaca por estar construido **estrictamente sobre estructuras de datos genéricas creadas desde cero** (sin utilizar colecciones nativas de Java como `List`, `LinkedList` o `Queue`), y por la integración de un cajero físico interactivo mediante hardware libre.

## 🏗️ Arquitectura y Tecnologías
* **Lenguaje y Entorno:** Java 25.0.2 LTS utilizando NetBeans y Maven.
* **Redes:** Arquitectura Cliente-Servidor mediante **Sockets TCP**[cite: 1, 3]. El Servidor (Banco) mantiene el estado centralizado y valida todas las acciones, mientras que los clientes actúan como interfaces de los jugadores.
* **Hardware (Cajero RFID):** Integración por puerto serial con un microcontrolador equipado con un lector RFID RC522 (billetera electrónica) y displays de 7 segmentos para los dados.

## ⚙️ Estructuras de Datos Implementadas
Cumpliendo con los requerimientos técnicos, el motor del juego utiliza exclusivamente:
* **Lista Circular Doblemente Enlazada:** Administra el tablero de 24 casillas temáticas (desde bares locales hasta el Estadio de Wembley)[cite: 1, 3].
* **Cola Circular:** Controla el flujo de turnos de la partida de 4 jugadores[cite: 3].
* **Listas Enlazadas Genéricas:** Administran las propiedades de cada jugador y el historial bidireccional de transacciones[cite: 3].

---

## 📂 Estructura del Proyecto

El código fuente sigue los estándares de empaquetado de Maven:

```text
Monopoly/
├── pom.xml
└── src/
    └── main/
        └── java/
            ├── structures/    # Clases Nodo<T>, ListaCircularDoble, ColaCircular
            ├── models/        # Jerarquía de Casillas, Jugador, Transaccion, Tablero
            ├── network/       # Lógica de Sockets: Servidor, Cliente, Banco central
            ├── rfid/          # Lógica de comunicación serial con el Arduino/Pico
            └── main/          # Clase principal de arranque del juego
