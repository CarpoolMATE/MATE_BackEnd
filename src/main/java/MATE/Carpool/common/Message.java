package MATE.Carpool.common;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Message<T> {

    private String message;
    private String requestMethod;
    private T data;
}
