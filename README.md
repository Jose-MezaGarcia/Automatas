# Autómatas

**Proyecto para simular modelos de lenguajes formales** como AFD, GLC y MT.  
Permite ingresar información por teclado o archivo, probar cadenas y ver paso a paso cómo funciona cada modo.  
Desarrollado para la materia de **Teoría de la Computación**.

---

## Simulador de Modelos de Reconocimiento de Lenguajes Formales

Este repositorio contiene una aplicación educativa que permite **simular, visualizar y analizar distintos modelos de reconocimiento de lenguajes formales**, desde autómatas finitos deterministas (AFD) hasta máquinas de Turing (MT).  

El proyecto fue realizado como parte del curso **Teoría de la Computación** en la Facultad de Matemáticas de la **Universidad Autónoma de Yucatán**.

---

## Modos de operación implementados

El sistema permite elegir entre distintos modos de operación, cada uno orientado a simular y analizar distintos modelos de lenguajes formales:

---

## 1. Modo AFD (Autómata Finito Determinista)
- Permite ingresar:
  - Alfabeto
  - Conjunto de estados
  - Estado inicial
  - Estados finales
  - Transiciones
- Funcionalidad:
  - El usuario introduce una palabra y el programa indica si es **aceptada o no**.
- Basado en los contenidos de la **Unidad 2 de ADA**.

---

## 2. Modo Gramática Regular
- Permite definir una gramática regular con producciones del tipo:  
  `A → aB | b`
- Funcionalidad:
  - Verificar si una **cadena pertenece o no al lenguaje** generado por la gramática.
  - La verificación se realiza mediante **comparación con un autómata equivalente**.

---

## 3. Modo Gramática Libre de Contexto (GLC)
- Permite definir producciones como:  
  `S → aSb | ε`
- Funcionalidad:
  - Mostrar **derivaciones** (izquierda o derecha) de una palabra.
  - Generar el **árbol sintáctico** correspondiente.
- Opcional (puntos extra):
  - Transformación a **Forma Normal de Chomsky** o **Greibach**.

---

## 4. Modo Autómata de Pila (AP)
- Permite ingresar la definición formal de un **autómata de pila**.
- Funcionalidad:
  - Simular la ejecución sobre cadenas de entrada.
  - Mostrar el **contenido de la pila en cada paso**.

---

## 5. Modo Máquina de Turing (MT) *(opcional / puntos extra)*
- Permite definir:
  - Estados
  - Alfabeto de cinta
  - Transiciones
  - Estados de aceptación
- Funcionalidad:
  - Mostrar **paso a paso** cómo se modifica la cinta durante la ejecución.
- Ejemplos de uso:
  - Reconocer **palíndromos**.
  - Sumar **números binarios**.

---

## Características

- Interfaz por **consola o gráfica** (según versión).  
- Entrada de datos desde **teclado o archivo**.  
- **Ejemplos de prueba** incluidos.  
- Código **comentado y organizado por módulos**.  

---

## Entregables

- Código fuente completo.  
- Manual de usuario con instrucciones y ejemplos.  
- Presentación teórica y práctica de cada modo.
