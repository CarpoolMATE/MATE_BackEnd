package MATE.Carpool.common;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Message<T> {

    private String message;
    private String requestMethod;
    private T data;
}
