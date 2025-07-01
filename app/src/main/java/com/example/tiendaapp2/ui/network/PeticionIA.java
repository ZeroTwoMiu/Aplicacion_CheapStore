package com.example.tiendaapp2.ui.network;

import java.util.List;

public class PeticionIA {
    public String model;
    public List<MensajeIA> messages;

    public PeticionIA(String model, List<MensajeIA> messages) {
        this.model = model;
        this.messages = messages;
    }

    public static class MensajeIA {
        public String role;
        public String content;

        public MensajeIA(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
