package com.devsuperior.dscommerce.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends CustomError {
    private List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Instant timestamp, Integer status, String error, String path) {
        super(timestamp, status, error, path);
    }

    public List<FieldMessage> getErrors() {
        return errors;
    }

    //adicionando mensagens de erro a lista
    public void addError(String fieldname, String message) {
        //removendo um fieldname caso ele já exista
        errors.removeIf(x -> x.getFieldName().equals(fieldname));

        //para só depois adicionar a mensagem (para que não tenha duplicatas)
        errors.add(new FieldMessage(fieldname, message));
    }
}
