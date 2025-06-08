package com.evaluacion;

import java.util.Scanner;

import com.evaluacion.alquiler.model.Cliente;
import com.evaluacion.alquiler.model.Pelicula;
import com.evaluacion.alquiler.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class App {

    /** Pequeña pausa para ver el estado de la BD en cada paso */
    private static void pausar(Scanner sc) {
        System.out.print("Presiona ENTER para continuar...");
        sc.nextLine();
    }

    public static void main(String[] args) {

        try (Scanner sc = new Scanner(System.in)) {

            // 1. Obtener EntityManager
            EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
            EntityTransaction tx = em.getTransaction();

            /*
             * ------------------------------------------------------------------
             * CREATE – 3 clientes de prueba
             * ------------------------------------------------------------------
             */
            tx.begin();
            // Usamos constructor sin id, JPA asignará automáticamente el idCliente
            Cliente c1 = new Cliente(1L, "Pedro", "pedro@gmail.com");
            Cliente c2 = new Cliente(2L, "Jose Luis", "joseluis@gmail.com");
            Cliente c3 = new Cliente(5L, "Gualberto", "gualberto@gmail.com");

            em.persist(c1);
            em.persist(c2);
            em.persist(c3);
            tx.commit();
            System.out.println("\nClientes creados:");
            System.out.println(c1);
            System.out.println(c2);
            System.out.println(c3);

            pausar(sc);

            /*
             * ------------------------------------------------------------------
             * CREATE – 3 películas de prueba
             * ------------------------------------------------------------------
             */
            tx.begin();
            // Usamos constructor sin id, JPA asignará automáticamente el idPelicula
            Pelicula p1 = new Pelicula("El Padrino", "Drama", 5, 15.99);
            Pelicula p2 = new Pelicula("Matrix", "Ciencia Ficción", 3, 12.50);
            Pelicula p3 = new Pelicula("Toy Story", "Animación", 7, 10.00);

            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            tx.commit();

            System.out.println("\nPelículas creadas:");
            System.out.println(p1);
            System.out.println(p2);
            System.out.println(p3);

            pausar(sc);

            em.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cierra el factory creado en JPAUtil
            JPAUtil.shutdown();
            System.out.println("\n>>> APLICACIÓN FINALIZADA <<<");
        }
    }
}
