/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

package com.example.entities;

import io.micronaut.serde.annotation.Serdeable;

/**
 *
 * @author jporecha
 */

@Serdeable
public record Person(int id, String firstName, String lastName) {

    public Person(String firstName, String lastName) {
      this(0,firstName, lastName);   
    }

}
