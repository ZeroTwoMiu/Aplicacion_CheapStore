package com.example.tiendaapp2.ui.network;

import java.util.List;

public class RespuestaIA {

    public List<Choice> choices;

    public static class Choice {
        public Mensaje message;
    }

    public static class Mensaje {
        public String role;
        public String content;
    }
}
