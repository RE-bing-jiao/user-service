package com.example.internship.microservice.exception;

public class InvalidStateException extends RuntimeException{
    public InvalidStateException(String message){
        super(message);
    }
}
