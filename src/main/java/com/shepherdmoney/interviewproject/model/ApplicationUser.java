package com.shepherdmoney.interviewproject.model;

import jakarta.persistence.*;
import lombok.*;

import java.lang.reflect.Array;
import java.util.ArrayList;


@Setter
@Getter
@ToString
@RequiredArgsConstructor
@Table(name="MyUser")
@Entity
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private String email;



    // TODO: User's credit card
    // HINT: A user can have one or more, or none at all. We want to be able to query credit cards by user
    //       and user by a credit card.
    private ArrayList<Integer> creditCards=new ArrayList<>();
}
